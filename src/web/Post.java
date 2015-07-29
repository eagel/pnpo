package web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.PooledConnection;

public class Post extends HttpServlet {
	private static final long serialVersionUID = 8808293686192061803L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		if (request.getParameter("data") != null && !request.getParameter("data").equals("")) {
			try {
				PooledConnection pooledConnection = null;
				pooledConnection = Database.getConnection();

				Connection connection = pooledConnection.getConnection();

				Statement statement = connection.createStatement();

				statement.executeQuery("SELECT MAX(id) FROM web_data");

				ResultSet resultSet = statement.getResultSet();

				resultSet.next();

				int id = resultSet.getInt(1) + 1;
				
				String data = request.getParameter("data");
				
				resultSet.close();
				statement.close();

				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO web_data VALUES(?, ?)");

				preparedStatement.setInt(1, id);
				preparedStatement.setBytes(2, data.getBytes("UTF-8"));

				preparedStatement.execute();

				preparedStatement.close();

				connection.commit();

				pooledConnection.close();

			} catch (SQLException e) {
				throw new ServletException(e);
			}
		}
		
		response.sendRedirect("");
	}

}
