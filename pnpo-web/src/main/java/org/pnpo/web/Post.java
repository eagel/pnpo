package org.pnpo.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "post", urlPatterns = "/post")
public class Post extends HttpServlet {
	private static final long serialVersionUID = 8808293686192061803L;
	private static final Logger logger = LoggerFactory.getLogger(Post.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("request");
		request.setCharacterEncoding("UTF-8");

		if (request.getParameter("data") != null && !request.getParameter("data").equals("")) {
			Connection connection = null;
			try {
				connection = Database.getConnection();

				Statement statement = connection.createStatement();

				statement.executeQuery("SELECT MAX(id) FROM pnpo_message");

				ResultSet resultSet = statement.getResultSet();

				resultSet.next();

				int id = resultSet.getInt(1) + 1;

				String data = request.getParameter("data");

				resultSet.close();
				statement.close();

				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO pnpo_message VALUES(?, ?)");

				preparedStatement.setInt(1, id);
				preparedStatement.setBytes(2, data.getBytes("UTF-8"));

				preparedStatement.execute();

				preparedStatement.close();

				connection.commit();

			} catch (SQLException e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				throw new ServletException(e);
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		response.sendRedirect(".");
	}

}
