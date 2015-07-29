package web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.PooledConnection;

public class Get extends HttpServlet {
	private static final long serialVersionUID = 3375775372535909378L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			PooledConnection pooledConnection = null;
			pooledConnection = Database.getConnection();

			Connection connection = pooledConnection.getConnection();

			Statement statement = connection.createStatement();

			statement.executeQuery("SELECT * FROM web_data ORDER BY id DESC LIMIT 1000");

			ResultSet resultSet = statement.getResultSet();

			List<Map<String, String>> data = new ArrayList<Map<String, String>>();

			while (resultSet.next()) {
				Map<String, String> entry = new HashMap<String, String>();

				entry.put("id", Integer.toString(resultSet.getInt("id")));
				entry.put("data", new String(resultSet.getBytes("data"), "UTF-8"));

				data.add(entry);
			}

			request.setAttribute("data", data);

			connection.commit();

			pooledConnection.close();

		} catch (SQLException e) {
			throw new ServletException(e);
		}
		request.getRequestDispatcher("WEB-INF/pages/get.jsp").forward(request, response);
	}
}
