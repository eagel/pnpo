package org.pnpo.db.pool;

import org.junit.Assert;
import org.junit.Test;

public class TestDatabaseConnectionPool {
	@Test
	public void testCreateDatabaseConnectionPool() {
		DatabaseConnectionPool pool = new DatabaseConnectionPool();

		Assert.assertEquals(DatabaseConnectionPool.Status.CREATED, pool.getStatus());
	}
}
