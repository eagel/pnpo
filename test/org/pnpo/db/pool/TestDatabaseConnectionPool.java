package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class TestDatabaseConnectionPool {
	@Test
	public void testCreateDatabaseConnectionPool() {
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);

		Assert.assertEquals(DatabaseConnectionPool.Status.CREATED, pool.getStatus());
	}

	@Test
	public void testStartupAndShutdow() throws IllegalStateException, SQLException {
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);

		pool.startup();

		pool.shutdown();

		Assert.assertEquals(DatabaseConnectionPool.Status.TERMINATED, pool.getStatus());

		Assert.assertTrue(pool.getExecutor().isTerminated());
	}

	@Test
	public void testStartupAndShutdowAndTernimated() throws IllegalStateException, SQLException {
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);

		pool.startup();

		Connection c = null;
		try {
			c = pool.getConnection();
			Assert.assertTrue(c.isValid(0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		pool.shutdown();

		Assert.assertEquals(DatabaseConnectionPool.Status.SHUTDOWN, pool.getStatus());

		Assert.assertTrue(pool.getExecutor().isTerminated());

		c.close();

		Assert.assertEquals(DatabaseConnectionPool.Status.TERMINATED, pool.getStatus());
	}

	@Test
	public void testAwaitTermination() throws IllegalStateException, SQLException, InterruptedException {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
		
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);

		pool.startup();

		new Thread(new Runnable() {
			public void run() {
				try {
					Connection c = pool.getConnection();
					cyclicBarrier.await();
					TimeUnit.MILLISECONDS.sleep(100);
					c.close();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			cyclicBarrier.await();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		pool.shutdown();

		Assert.assertEquals(DatabaseConnectionPool.Status.SHUTDOWN, pool.getStatus());

		Assert.assertTrue(pool.getExecutor().isTerminated());

		Assert.assertTrue(pool.awaitTermination(200, TimeUnit.MILLISECONDS));
		
		Assert.assertEquals(DatabaseConnectionPool.Status.TERMINATED, pool.getStatus());
	}
	
	@Test
	public void testAwaitTerminationTimeout() throws IllegalStateException, SQLException, InterruptedException {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
		
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);

		pool.startup();

		new Thread(new Runnable() {
			public void run() {
				try {
					Connection c = pool.getConnection();
					cyclicBarrier.await();
					TimeUnit.MILLISECONDS.sleep(200);
					c.close();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			cyclicBarrier.await();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		pool.shutdown();

		Assert.assertEquals(DatabaseConnectionPool.Status.SHUTDOWN, pool.getStatus());

		Assert.assertTrue(pool.getExecutor().isTerminated());

		Assert.assertFalse(pool.awaitTermination(100, TimeUnit.MILLISECONDS));
		Assert.assertEquals(DatabaseConnectionPool.Status.SHUTDOWN, pool.getStatus());

		Assert.assertTrue(pool.awaitTermination(200, TimeUnit.MILLISECONDS));
		Assert.assertEquals(DatabaseConnectionPool.Status.TERMINATED, pool.getStatus());
	}

}
