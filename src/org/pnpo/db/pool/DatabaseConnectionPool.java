package org.pnpo.db.pool;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * A simple database connection implementation.
 */
public class DatabaseConnectionPool {
	/**
	 * The status of the database connection pool.
	 * {@link DatabaseConnectionPool.Status#CREATED}: The database connection
	 * pool be created but not startup.<br/>
	 * {@link DatabaseConnectionPool.Status#STARTUP}: The database connection
	 * pool be started.<br/>
	 * {@link DatabaseConnectionPool.Status#SHUTDOWN}: The database connection
	 * pool was shutting down but not be terminated.<br/>
	 * {@link DatabaseConnectionPool.Status#SHUTDOWN}: The database connection
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
	 * The status of the database connection pool
	 * 
	 * @return {@link DatabaseConnectionPool.Status}
	 */
	public Status getStatus() {
		return status;
	}

	public Connection getConnection() throws InterruptedException, IllegalStateException {
		// TODO
		return null;
	}

	public void startup() throws IllegalStateException {
		// TODO
	}

	public void shutdown() throws IllegalStateException {
		// TODO
	}

	public void shutdownNow() throws IllegalStateException {
		// TODO
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException, IllegalStateException {
		// TODO
		return false;
	}
}
