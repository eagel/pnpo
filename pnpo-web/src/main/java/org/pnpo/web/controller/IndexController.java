package org.pnpo.web.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pnpo.web.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("index")
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping(path = "post", method = RequestMethod.POST)
	public ModelAndView post(@RequestParam String data, ModelAndView modelAndView)
			throws UnsupportedEncodingException, SQLException {
		logger.debug("post");
		if (data != null && !data.equals("")) {
			Connection connection = null;
			try {
				connection = Database.getConnection();

				Statement statement = connection.createStatement();

				statement.executeQuery("SELECT MAX(id) FROM pnpo_message");

				ResultSet resultSet = statement.getResultSet();

				resultSet.next();

				int id = resultSet.getInt(1) + 1;

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
				throw e;
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		modelAndView.setView(new RedirectView("../../"));
		return modelAndView;
	}

	@RequestMapping(path = "get", method = RequestMethod.GET)
	public ModelAndView get(ModelAndView modelAndView) throws UnsupportedEncodingException, SQLException {

		Connection connection = null;
		List<Map<String, String>> data = null;
		try {
			connection = Database.getConnection();

			Statement statement = connection.createStatement();

			statement.executeQuery("SELECT * FROM pnpo_message ORDER BY id DESC LIMIT 1000");

			ResultSet resultSet = statement.getResultSet();

			data = new ArrayList<Map<String, String>>();

			while (resultSet.next()) {
				Map<String, String> entry = new HashMap<String, String>();

				entry.put("id", Integer.toString(resultSet.getInt("id")));
				entry.put("data", new String(resultSet.getBytes("data"), "UTF-8"));

				data.add(entry);
			}

			connection.commit();

		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("[");
		boolean first = true;
		for (Map<String, String> message : data) {
			if (!first) {
				stringBuilder.append(",");
			} else {
				first = false;
			}
			stringBuilder.append("{\"id\":");
			stringBuilder.append(message.get("id"));
			stringBuilder.append(",");
			stringBuilder.append("\"data\":");
			stringBuilder.append("\"");
			stringBuilder.append(message.get("data").replaceAll("\"", "\\\\\""));
			stringBuilder.append("\"");
			stringBuilder.append("}");
		}
		stringBuilder.append("]");

		modelAndView.setView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(stringBuilder.toString());
				response.getWriter().close();
			}

			@Override
			public String getContentType() {
				return "application/json; charset=UTF-8";
			}
		});
		return modelAndView;
	}

}
