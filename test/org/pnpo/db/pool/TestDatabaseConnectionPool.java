package org.pnpo.db.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

	@Test
	public void testRunning() throws IllegalStateException, SQLException, InterruptedException {
		DatabaseConnectionPool pool = new DatabaseConnectionPool(10);
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		AtomicInteger poolBeenShutdown = new AtomicInteger(0);
		AtomicInteger serviceBeenShutdown = new AtomicInteger(0);

		AtomicInteger count = new AtomicInteger(0);

		CountDownLatch latch = new CountDownLatch(100);

		pool.startup();

		Connection c = pool.getConnection();
		c.setAutoCommit(false);
		PreparedStatement preparedStatement = c
				.prepareStatement("CREATE TABLE IF NOT EXISTS pnpo_test(id int primary key, name varchar(256))");
		preparedStatement.execute();
		c.commit();
		c.close();

		for (int i = 0; i < 100; i++) {
			executorService.execute(new Runnable() {
				public void run() {
					Connection c = null;
					try {
						c = pool.getConnection();
						c.setAutoCommit(false);

						PreparedStatement preparedStatement = c.prepareStatement("SELECT * from pnpo_test;");

						preparedStatement.execute();

						c.commit();
						c.close();

						count.incrementAndGet();

						executorService.execute(this);
					} catch (IllegalStateException e) {
						poolBeenShutdown.incrementAndGet();
						latch.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (RejectedExecutionException e) {
						serviceBeenShutdown.incrementAndGet();
						latch.countDown();
					}
				}
			});
		}

		TimeUnit.SECONDS.sleep(1);

		executorService.shutdown();
		pool.shutdown();

		Assert.assertTrue(pool.awaitTermination(0, null));
		latch.await();

		Assert.assertEquals(100, poolBeenShutdown.get() + serviceBeenShutdown.get());

		DatabaseConnectionPool tempPool = new DatabaseConnectionPool(1);
		tempPool.startup();
		Connection tempC = tempPool.getConnection();
		tempC.setAutoCommit(false);
		PreparedStatement tempPreparedStatement = tempC.prepareStatement("DROP TABLE pnpo_test;");
		tempPreparedStatement.execute();
		tempC.commit();
		tempC.close();
		tempPool.startup();

		tempPool.shutdown();
		tempPool.awaitTermination(-1, null);
	}

}
