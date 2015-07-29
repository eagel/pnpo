package web;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

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
			PooledConnection pooledConnection = getConnection();
			
			Connection connection = pooledConnection.getConnection();
			
			Statement statement = connection.createStatement();
			
			statement.execute("CREATE TABLE IF NOT EXISTS web_data (id int primary key, data bytea)");
			
			connection.commit();
			
			pooledConnection.close();
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		
		
	}

	@Override
	public void destroy() {
		connectionPoolDataSource = null;
	}

	public static PooledConnection getConnection() throws SQLException {
		return connectionPoolDataSource.getPooledConnection();
	}

}
