package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class TestDatabaseConnectionFactory {
	@Test
	public void testGetConnection() {
		DatabaseConnectionFactory factory = new DatabaseConnectionFactory();

		factory.setURL("jdbc:postgresql://localhost/root");

		factory.setUsername("root");

		factory.setPassword("root");

		Connection connection = null;
		try {
			connection = factory.create();
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		try {
			connection.close();
		} catch (SQLException e) {
		}

	}
}
