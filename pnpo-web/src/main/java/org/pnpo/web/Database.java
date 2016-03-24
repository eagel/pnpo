package org.pnpo.web;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.pnpo.PNPO;
import org.pnpo.db.pool.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "database", loadOnStartup = 1, value = {})
public class Database extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	private static final long serialVersionUID = 2460919096215614297L;
	private static DatabaseConnectionPool pool;
	private static Driver driver;

	@Override
	public void init() throws ServletException {
		logger.info("initialize");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}

		for (Driver d : Collections.list(DriverManager.getDrivers())) {
			if (d.getClass().equals(org.postgresql.Driver.class)) {
				driver = d;
			}
		}

		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e) {
			throw new ServletException(e);
		}

		pool = new DatabaseConnectionPool(10);
		try {
			pool.startup();
		} catch (IllegalStateException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
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
		logger.info("destroy");
		pool.shutdown();
		try {
			pool.awaitTermination(-1, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pool = null;

		try {
			DriverManager.deregisterDriver(driver);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		driver = null;
	}

	public static Connection getConnection() throws SQLException {
		logger.debug("getConnection");
		try {
			Connection connection = pool.getConnection();
			connection.setAutoCommit(false);
			return connection;
		} catch (IllegalStateException e) {
			throw new SQLException(e);
		} catch (InterruptedException e) {
			throw new SQLException(e);
		}
	}

}
