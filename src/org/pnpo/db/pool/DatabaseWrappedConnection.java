package org.pnpo.db.pool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class DatabaseWrappedConnection implements Connection {
	private DatabasePooledConnection pooledConnection;

	public DatabaseWrappedConnection(DatabasePooledConnection pooledConnection) {
		this.pooledConnection = pooledConnection;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		check();
		return pooledConnection.getConnection().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		check();
		return pooledConnection.getConnection().isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().commit();
	}

	@Override
	public void rollback() throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().rollback();
	}

	@Override
	public void close() throws SQLException {
		check();
		this.pooledConnection.idle();
		this.pooledConnection = null;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return pooledConnection == null ? false : true;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setReadOnly(true);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		check();
		return pooledConnection.getConnection().getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		check();
		pooledConnection.getConnection().setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		try {
			check();
		} catch (SQLException e) {
			throw new SQLClientInfoException();
		}
		pooledConnection.touch();
		pooledConnection.getConnection().setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		try {
			check();
		} catch (SQLException e) {
			throw new SQLClientInfoException();
		}
		pooledConnection.touch();
		pooledConnection.getConnection().setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		close();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		check();
		pooledConnection.touch();
		pooledConnection.getConnection().setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		check();
		pooledConnection.touch();
		return pooledConnection.getConnection().getNetworkTimeout();
	}

	private void check() throws SQLException {
		if (pooledConnection == null) {
			throw new SQLException("connection closed");
		}
	}

}
