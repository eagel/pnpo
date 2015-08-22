package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionFactory {
	private static final String KEY_USERNAME = "user";
	private static final String KEY_PASSWORD = "password";

	private String url;
	private Properties properties = new Properties();

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void setProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	public Properties getProperties() {
		return new Properties(properties);
	}

	public void setPassword(String password) {
		properties.setProperty(KEY_PASSWORD, password);
	}

	public void setUsername(String username) {
		properties.setProperty(KEY_USERNAME, username);
	}

	public void setURL(String url) {
		this.url = url;
	}

	public Connection create() throws SQLException {
		return DriverManager.getConnection(url, properties);
	}

}
