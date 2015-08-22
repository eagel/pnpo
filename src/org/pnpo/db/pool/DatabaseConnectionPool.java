package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple database connection implementation.
 */
public class DatabaseConnectionPool {
	/**
	 * The status of the database connection pool.
	 * {@link DatabaseConnectionPool.Status#CREATED}: The database connection
	 * pool be created but not startup.<br>
	 * {@link DatabaseConnectionPool.Status#STARTUP}: The database connection
	 * pool be started.<br>
	 * {@link DatabaseConnectionPool.Status#SHUTDOWN}: The database connection
	 * pool was shutting down but not be terminated.<br>
	 * {@link DatabaseConnectionPool.Status#TERMINATED}: The database connection
	 * pool be terminated.
	 */
	public enum Status {
		CREATED, STARTUP, SHUTDOWN, TERMINATED
	}

	/**
	 * Current status
	 */
	private Status status = Status.CREATED;

	/**
	 * The factory object use to create connection to database;
	 */
	private DatabaseConnectionFactory databaseConnectionFactory = null;

	/**
	 * The executor
	 */
	private ScheduledThreadPoolExecutor executor = null;
	private int executorThreadSize = 1;

	private List<DatabasePooledConnection> idleConnections = new ArrayList<DatabasePooledConnection>();
	private List<DatabasePooledConnection> busyConnections = new ArrayList<DatabasePooledConnection>();
	private ReentrantLock connectionLock = new ReentrantLock();
	private Condition hasIdleConnection = connectionLock.newCondition();
	private Condition terminatedCondition = connectionLock.newCondition();
	private int maxConnectionSize;
	private int maxIdleTime = 180000;

	/**
	 * Create a DatabaseConnectionPool and configure a default
	 * DatabaseConnectionFactory.
	 * 
	 * @param maxConnectionSize
	 *            The max content size
	 */
	public DatabaseConnectionPool(int maxConnectionSize) {
		this.databaseConnectionFactory = new DatabaseConnectionFactory();
		this.databaseConnectionFactory.setURL("jdbc:postgresql://localhost/root");
		this.databaseConnectionFactory.setUsername("root");
		this.databaseConnectionFactory.setPassword("root");
		this.maxConnectionSize = maxConnectionSize;
	}

	/**
	 * Create a DatabaseConnectionPool
	 * 
	 * @param maxConnectionSize
	 *            the max content size
	 * @param databaseConnectionFactory
	 *            The DatabaseConnectionFactory for creating JDBC connection.
	 */
	public DatabaseConnectionPool(int maxConnectionSize, DatabaseConnectionFactory databaseConnectionFactory) {
		this.databaseConnectionFactory = databaseConnectionFactory;
		this.maxConnectionSize = maxConnectionSize;
	}

	/**
	 * The status of the database connection pool
	 * 
	 * @return {@link DatabaseConnectionPool.Status}
	 */
	public Status getStatus() {
		return status;
	}

	public ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void setMaxConnectionSize(int maxConnectionSize) throws IllegalStateException {
		if (Status.CREATED.equals(status)) {
			throw new IllegalStateException();
		}
		this.maxConnectionSize = maxConnectionSize;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		if (Status.CREATED.equals(status)) {
			throw new IllegalStateException();
		}
		this.maxIdleTime = maxIdleTime;
	}

	public void setExecutorThreadSize(int executorThreadSize) {
		if (Status.CREATED.equals(status)) {
			throw new IllegalStateException();
		}
		this.executorThreadSize = executorThreadSize;
	}

	public void idle(DatabasePooledConnection pooledConnection) throws IllegalStateException {

		connectionLock.lock();
		try {
			if (Status.STARTUP.equals(status)) {
				if (busyConnections.remove(pooledConnection)) {
					idleConnections.add(pooledConnection);
					hasIdleConnection.signalAll();
				} else {
					throw new IllegalStateException();
				}
			} else {
				dead(pooledConnection);
				if (idleConnections.isEmpty()) {
					status = Status.TERMINATED;
					terminatedCondition.signalAll();
				}
			}
		} finally {
			connectionLock.unlock();
		}
	}

	public void busy(DatabasePooledConnection pooledConnection) throws IllegalStateException {
		connectionLock.lock();
		try {
			if (idleConnections.remove(pooledConnection)) {
				busyConnections.add(pooledConnection);
			} else {
				throw new IllegalStateException();
			}
		} finally {
			connectionLock.unlock();
		}
	}

	public void dead(DatabasePooledConnection pooledConnection) throws IllegalStateException {
		connectionLock.lock();
		try {
			if (idleConnections.remove(pooledConnection) || busyConnections.remove(pooledConnection)) {
				try {
					pooledConnection.getConnection().close();
				} catch (SQLException e) {
				}
			} else {
				throw new IllegalStateException();
			}
		} finally {
			connectionLock.unlock();
		}
	}

	public Connection getConnection() throws InterruptedException, IllegalStateException, SQLException {
		return getConnection(0);
	}

	public Connection getConnection(int timeout) throws SQLException, InterruptedException, IllegalStateException {
		connectionLock.lock();
		DatabaseWrappedConnection wrappedConnection = null;
		try {
			while (wrappedConnection == null) {
				DatabasePooledConnection pooledConnection = null;
				if (!Status.STARTUP.equals(status)) {
					throw new IllegalStateException();
				}
				if (!idleConnections.isEmpty()) {
					pooledConnection = idleConnections.get(0);
				} else {
					if ((idleConnections.size() + busyConnections.size()) < maxConnectionSize) {
						fillConnections();
					} else {
						if (!hasIdleConnection.await(timeout, TimeUnit.MILLISECONDS)) {
							break;
						}
					}
				}

				if (pooledConnection != null) {
					pooledConnection.busy();
					if (pooledConnection.getConnection().isValid(0) && ((System.currentTimeMillis()
							- pooledConnection.getUpdatedDate().getTime()) < maxIdleTime)) {
						wrappedConnection = pooledConnection.getDatabaseWrappedConnection();
						break;
					} else {
						pooledConnection.dead();
					}
				}
			}
		} finally {
			connectionLock.unlock();
		}
		return wrappedConnection;
	}

	public void startup() throws SQLException, IllegalStateException {
		executor = new ScheduledThreadPoolExecutor(executorThreadSize);
		status = Status.STARTUP;
		fillConnections();
	}

	public void shutdown() throws IllegalStateException {
		if (!Status.STARTUP.equals(status) && !Status.SHUTDOWN.equals(status)) {
			throw new IllegalStateException();
		}
		executor.shutdown();
		status = Status.SHUTDOWN;
		connectionLock.lock();
		try {
			for (DatabasePooledConnection pooledConnection : new ArrayList<DatabasePooledConnection>(idleConnections)) {
				dead(pooledConnection);
			}
			if (busyConnections.isEmpty()) {
				status = Status.TERMINATED;
				terminatedCondition.signalAll();
			}
			hasIdleConnection.signalAll();
		} finally {
			connectionLock.unlock();
		}
	}

	public void shutdownNow() throws IllegalStateException {
		if (!Status.STARTUP.equals(status) && !Status.SHUTDOWN.equals(status)) {
			throw new IllegalStateException();
		}
		executor.shutdown();
		status = Status.SHUTDOWN;
		connectionLock.lock();
		try {
			for (DatabasePooledConnection pooledConnection : new ArrayList<DatabasePooledConnection>(idleConnections)) {
				dead(pooledConnection);
			}
			for (DatabasePooledConnection pooledConnection : new ArrayList<DatabasePooledConnection>(busyConnections)) {
				dead(pooledConnection);
			}
			status = Status.TERMINATED;
			terminatedCondition.signalAll();
			hasIdleConnection.signalAll();
		} finally {
			connectionLock.unlock();
		}
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException, IllegalStateException {
		if (Status.TERMINATED.equals(status)) {
			return true;
		}
		if (!Status.SHUTDOWN.equals(status)) {
			throw new IllegalStateException();
		}

		connectionLock.lock();
		try {
			return terminatedCondition.await(timeout, unit);
		} finally {
			connectionLock.unlock();
		}
	}

	private void fillConnections() throws SQLException {
		if (!Status.STARTUP.equals(status)) {
			return;
		}
		connectionLock.lock();
		try {
			int currentSize = idleConnections.size() + busyConnections.size();
			int freeSize = maxConnectionSize - currentSize;
			for (int i = 0; i < freeSize; i++) {
				idleConnections.add(createConnection());
			}
			if (freeSize > 0) {
				hasIdleConnection.signalAll();
			}
		} finally {
			connectionLock.unlock();
		}
	}

	private DatabasePooledConnection createConnection() throws SQLException {
		Connection connection = databaseConnectionFactory.create();

		boolean autoCommit = false;
		int transactionIsolation = 0;
		boolean readOnly = false;
		String catalog = null;
		String schema = null;
		Map<String, Class<?>> typeMap = null;
		int holdability = 0;
		Properties clientInfo = null;
		int networkTimeout = 0;

		try {
			autoCommit = connection.getAutoCommit();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			transactionIsolation = connection.getTransactionIsolation();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			readOnly = connection.isReadOnly();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			catalog = connection.getCatalog();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			schema = connection.getSchema();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			typeMap = connection.getTypeMap();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			holdability = connection.getHoldability();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			clientInfo = connection.getClientInfo();
		} catch (SQLFeatureNotSupportedException e) {
		}

		try {
			networkTimeout = connection.getNetworkTimeout();
		} catch (SQLFeatureNotSupportedException e) {
		}

		return new DatabasePooledConnection(this, connection, autoCommit, transactionIsolation, readOnly, catalog,
				schema, typeMap, holdability, clientInfo, networkTimeout);
	}
}
