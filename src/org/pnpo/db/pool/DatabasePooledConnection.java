package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class DatabasePooledConnection {
	public enum Status {
		IDLE, BUSY, DEAD
	}

	private DatabaseConnectionPool pool;
	private Connection connection;
	private Date createdDate = new Date();
	private Date updatedDate = new Date();

	private Status status;
	private DatabaseWrappedConnection wrappedConnection;

	private boolean autoCommit;
	private int transactionIsolation;
	private boolean readOnly;
	private String catalog;
	private String schema;
	private Map<String, Class<?>> typeMap;
	private int holdability;
	private Properties clientInfo;
	private int networkTimeout;

	public DatabasePooledConnection(DatabaseConnectionPool pool, Connection connection, boolean autoCommit,
			int transactionIsolation, boolean readOnly, String catalog, String schema, Map<String, Class<?>> typeMap,
			int holdability, Properties clientInfo, int networkTimeout) {
		this.pool = pool;
		this.connection = connection;
		this.autoCommit = autoCommit;
		this.transactionIsolation = transactionIsolation;
		this.readOnly = readOnly;
		this.catalog = catalog;
		this.schema = schema;
		this.typeMap = typeMap;
		this.holdability = holdability;
		this.clientInfo = clientInfo;
		this.networkTimeout = networkTimeout;
	}

	public void touch() {
		updatedDate = new Date();
	}

	public void idle() throws IllegalStateException {
		// reset status
		try {
			try {
				connection.rollback();
			} catch (SQLException e) {
			}

			connection.clearWarnings();

			try {
				connection.setAutoCommit(autoCommit);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setTransactionIsolation(transactionIsolation);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setReadOnly(readOnly);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setCatalog(catalog);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setSchema(schema);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setTypeMap(typeMap);
			} catch (SQLFeatureNotSupportedException e) {
			}

			try {
				connection.setHoldability(holdability);
			} catch (SQLFeatureNotSupportedException e) {
			}

			connection.setClientInfo(clientInfo);
			
			try {
				connection.setNetworkTimeout(pool.getExecutor(), networkTimeout);
			} catch (SQLFeatureNotSupportedException e) {
			}
		} catch (SQLException e) {
			pool.dead(this);
			this.wrappedConnection = null;
			this.status = Status.DEAD;
		}
		this.wrappedConnection = null;
		this.status = Status.IDLE;
		pool.idle(this);
	}

	public void busy() throws IllegalStateException {
		pool.busy(this);
		this.wrappedConnection = new DatabaseWrappedConnection(this);
		this.status = Status.BUSY;
	}

	public void dead() throws IllegalStateException {
		pool.dead(this);
		this.wrappedConnection = null;
		this.status = Status.DEAD;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public Connection getConnection() {
		return connection;
	}

	public DatabaseWrappedConnection getDatabaseWrappedConnection() throws IllegalStateException {
		if (!Status.BUSY.equals(status)) {
			throw new IllegalStateException("The status is not " + Status.BUSY + ", but is " + status);
		}
		return wrappedConnection;
	}
}
