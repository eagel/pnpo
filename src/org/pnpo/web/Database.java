package org.pnpo.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.pnpo.PNPO;
import org.postgresql.ds.PGConnectionPoolDataSource;

public class Database extends HttpServlet {
	private static final long serialVersionUID = 2460919096215614297L;

	private static ConnectionPoolDataSource connectionPoolDataSource;

	@Override
	public void init() throws ServletException {
		PGConnectionPoolDataSource pgConnectionPoolDataSource = new PGConnectionPoolDataSource();
		pgConnectionPoolDataSource.setUrl("jdbc:postgresql://localhost:5432/root");
		pgConnectionPoolDataSource.setUser("root");
		pgConnectionPoolDataSource.setPassword("root");
		pgConnectionPoolDataSource.setApplicationName("web");
		pgConnectionPoolDataSource.setDefaultAutoCommit(false);
		connectionPoolDataSource = pgConnectionPoolDataSource;

		try {
			Connection connection = getConnection();

			Statement statement = connection.createStatement();

			// create message table
			statement.execute("CREATE TABLE IF NOT EXISTS pnpo_message (id int primary key, data bytea)");

			// create installation table
			statement.execute(
					"CREATE TABLE IF NOT EXISTS pnpo_installation (id int primary key, name varchar(256), major int, minor int, micro int, qualifier varchar(256))");

			// check installation information
			if (statement.execute("SELECT * FROM pnpo_installation") && statement.getResultSet().next()) {
				// update the installation information
				statement.execute("UPDATE pnpo_installation SET name = '" + "PNPO" + "', major = '" + PNPO.MAJOR_VERSION
						+ "', minor = '" + PNPO.MINOR_VERSION + "', micro = '" + PNPO.MICRO_VERSION + "',qualifier = '"
						+ PNPO.QUALIFIER_VERSION + "'  WHERE id = 1");
			} else {
				// insert the installation information
				statement.execute("INSERT INTO pnpo_installation VALUES ( " + 0 + ", 'PNPO', " + PNPO.MAJOR_VERSION
						+ ", " + PNPO.MINOR_VERSION + ", " + PNPO.MICRO_VERSION + ", '" + PNPO.QUALIFIER_VERSION
						+ "')");
			}

			connection.commit();

			connection.close();
		} catch (SQLException e) {
			throw new ServletException(e);
		}

	}

	@Override
	public void destroy() {
		connectionPoolDataSource = null;
	}

	public static Connection getConnection() throws SQLException {
		PooledConnection pooledConnection = connectionPoolDataSource.getPooledConnection();

		pooledConnection.addConnectionEventListener(new ConnectionEventListener() {

			@Override
			public void connectionErrorOccurred(ConnectionEvent event) {
				PooledConnection pooledConnection = (PooledConnection) event.getSource();
				try {
					pooledConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void connectionClosed(ConnectionEvent event) {
				PooledConnection pooledConnection = (PooledConnection) event.getSource();
				try {
					pooledConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		return pooledConnection.getConnection();
	}

}
