/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */
package me.dec7.study.holub.database.jdbc.adapters;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/***
 * @include /etc/license.txt
 */

public class ConnectionAdapter implements Connection
{
	public ConnectionAdapter() throws SQLException {} 	// Not an error if this one is called.


	public ConnectionAdapter(java.sql.Driver driver, String url, java.util.Properties info)
		throws SQLException
	{	throw new SQLException("ConnectionAdapter constructor unsupported");
	}

	@Override
	public void setHoldability(int h)
		throws SQLException
	{	throw new SQLException("Connection.setHoldability(int h) unsupported");
	}

	@Override
	public int getHoldability()
		throws SQLException
	{	throw new SQLException("Connection.getHoldability() unsupported");
	}


	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Connection.unwrap() unsupported");
	}


	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new SQLException("Connection.isWrapperFor() unsupported");
	}


	@Override
	public Statement createStatement() throws SQLException {
		throw new SQLException("Connection.createStatement() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new SQLException("Connection.prepareCall() unsupported");
	}


	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new SQLException("Connection.nativeSQL() unsupported");
	}


	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		throw new SQLException("Connection.setAutoCommit() unsupported");
	}


	@Override
	public boolean getAutoCommit() throws SQLException {
		throw new SQLException("Connection.getAutoCommit() unsupported");
	}


	@Override
	public void commit() throws SQLException {
		throw new SQLException("Connection.commit() unsupported");
	}


	@Override
	public void rollback() throws SQLException {
		throw new SQLException("Connection.rollback() unsupported");
	}


	@Override
	public void close() throws SQLException {
		throw new SQLException("Connection.close() unsupported");
	}


	@Override
	public boolean isClosed() throws SQLException {
		throw new SQLException("Connection.isClosed() unsupported");
	}


	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		throw new SQLException("Connection.getMetaData() unsupported");
	}


	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		throw new SQLException("Connection.setReadOnly() unsupported");
	}


	@Override
	public boolean isReadOnly() throws SQLException {
		throw new SQLException("Connection.isReadOnly() unsupported");
	}


	@Override
	public void setCatalog(String catalog) throws SQLException {
		throw new SQLException("Connection.setCatalog() unsupported");
	}


	@Override
	public String getCatalog() throws SQLException {
		throw new SQLException("Connection.getCatalog() unsupported");
	}


	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		throw new SQLException("Connection.setTransactionIsolation() unsupported");
	}


	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new SQLException("Connection.getTransactionIsolation() unsupported");
	}


	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Connection.getWarnings() unsupported");
	}


	@Override
	public void clearWarnings() throws SQLException {
		throw new SQLException("Connection.clearWarnings() unsupported");
	}


	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLException("Connection.createStatement() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLException("Connection.prepareCall() unsupported");
	}


	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new SQLException("Connection.getTypeMap() unsupported");
	}


	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new SQLException("Connection.setTypeMap() unsupported");
	}


	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new SQLException("Connection.setSavepoint() unsupported");
	}


	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new SQLException("Connection.setSavepoint() unsupported");
	}


	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new SQLException("Connection.rollback() unsupported");
	}


	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new SQLException("Connection.releaseSavepoint() unsupported");
	}


	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		throw new SQLException("Connection.createStatement() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new SQLException("Connection.prepareCall() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		throw new SQLException("Connection.prepareStatement() unsupported");
	}


	@Override
	public Clob createClob() throws SQLException {
		throw new SQLException("Connection.createClob() unsupported");
	}


	@Override
	public Blob createBlob() throws SQLException {
		throw new SQLException("Connection.createBlob() unsupported");
	}


	@Override
	public NClob createNClob() throws SQLException {
		throw new SQLException("Connection.createNClob() unsupported");
	}


	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new SQLException("Connection.createSQLXML() unsupported");
	}


	@Override
	public boolean isValid(int timeout) throws SQLException {
		throw new SQLException("Connection.isValid() unsupported");
	}


	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		throw new SQLClientInfoException("Connection.setClientInfo() unsupported", new HashMap<String, ClientInfoStatus>());
	}


	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		throw new SQLClientInfoException("Connection.setClientInfo() unsupported", new HashMap<String, ClientInfoStatus>());
	}


	@Override
	public String getClientInfo(String name) throws SQLException {
		throw new SQLException("Connection.getClientInfo() unsupported");
	}


	@Override
	public Properties getClientInfo() throws SQLException {
		throw new SQLException("Connection.getClientInfo() unsupported");
	}


	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new SQLException("Connection.createArrayOf() unsupported");
	}


	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new SQLException("Connection.createStruct() unsupported");
	}


	@Override
	public void setSchema(String schema) throws SQLException {
		throw new SQLException("Connection.setSchema() unsupported");
	}


	@Override
	public String getSchema() throws SQLException {
		throw new SQLException("Connection.getSchema() unsupported");
	}


	@Override
	public void abort(Executor executor) throws SQLException {
		throw new SQLException("Connection.abort() unsupported");
	}


	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new SQLException("Connection.setNetworkTimeout() unsupported");
	}


	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new SQLException("Connection.getNetworkTimeout() unsupported");
	}

}

