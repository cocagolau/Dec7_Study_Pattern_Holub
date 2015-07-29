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

/***
 * @include /etc/license.txt
 */
public class StatementAdapter implements java.sql.Statement {
	public StatementAdapter() {
	}

	@Override
	public void setFetchSize(int fetchSize) throws SQLException {
		throw new SQLException("Statement.setFetchSize(int fetchSize) not supported");
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new SQLException("Statement.getFetchSize() not supported");
	}

	@Override
	public int getMaxRows() throws SQLException {
		throw new SQLException("Statement.getMaxRows() not supported");
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		throw new SQLException("Statement.setMaxRows(int max) not supported");
	}

	@Override
	public void setFetchDirection(int fetchDirection) throws SQLException {
		throw new SQLException("Statement.setFetchDirection(int fetchDirection) not supported");
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new SQLException("Statement.getFetchDirection() not supported");
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new SQLException("Statement.getResultSetConcurrency() not supported");
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		throw new SQLException("Statement.getResultSetHoldability() not supported");
	}

	@Override
	public int getResultSetType() throws SQLException {
		throw new SQLException("Statement.getResultSetType() not supported");
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		throw new SQLException("Statement.setQueryTimeout(int seconds) not supported");
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		throw new SQLException("Statement.getQueryTimeout() not supported");
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new SQLException("Statement.getResultSet() not supported");
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		throw new SQLException("Statement.executeQuery(String sql) not supported");
	}

	@Override
	public int executeUpdate(String sql, int i) throws SQLException {
		throw new SQLException("Statement.executeUpdate(String sql, int i) not supported");
	}

	@Override
	public int executeUpdate(String sql, String[] cols) throws SQLException {
		throw new SQLException("Statement.executeUpdate(String sql, String[] cols) not supported");
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		throw new SQLException("Statement.execute(String sql) not supported");
	}

	@Override
	public boolean execute(String sql, String[] cols) throws SQLException {
		throw new SQLException("Statement.execute(String sql, String[] cols) not supported");
	}

	@Override
	public boolean execute(String sql, int i) throws SQLException {
		throw new SQLException("Statement.execute(String sql, int i) not supported");
	}

	@Override
	public boolean execute(String sql, int[] cols) throws SQLException {
		throw new SQLException("Statement.execute(String sql, int[] cols) not supported");
	}

	@Override
	public void cancel() throws SQLException {
		throw new SQLException("Statement.cancel() not supported");
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new SQLException("Statement.clearWarnings() not supported");
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw new SQLException("Statement.getConnection() not supported");
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		throw new SQLException("Statement.getGeneratedKeys() not supported");
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		throw new SQLException("Statement.addBatch(String sql) not supported");
	}

	@Override
	public int[] executeBatch() throws SQLException {
		throw new SQLException("not supported");
	}

	@Override
	public void clearBatch() throws SQLException {
		throw new SQLException("Statement.clearBatch() not supported");
	}

	@Override
	public void close() throws SQLException {
		throw new SQLException("Statement.close() not supported");
	}

	@Override
	public int executeUpdate(String sql, int[] i) throws SQLException {
		throw new SQLException("Statement.executeUpdate(String sql, int[] i) not supported");
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		throw new SQLException("Statement.executeUpdate(String sql) not supported");
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		throw new SQLException("Statement.getMaxFieldSize() not supported");
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new SQLException("Statement.getMoreResults() not supported");
	}

	@Override
	public boolean getMoreResults(int i) throws SQLException {
		throw new SQLException("Statement.getMoreResults(int i) not supported");
	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new SQLException("Statement.getUpdateCount() not supported");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Statement.getWarnings() not supported");
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		throw new SQLException("Statement.setCursorName(String name) not supported");
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new SQLException("Statement.setEscapeProcessing(boolean enable) not supported");
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		throw new SQLException("Statement.setMaxFieldSize(int max) not supported");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Statement.unwrap() not supported");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new SQLException("Statement.isWrapperFor() not supported");
	}

	@Override
	public boolean isClosed() throws SQLException {
		throw new SQLException("Statement.isClosed() not supported");
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		throw new SQLException("Statement.setPoolable() not supported");
	}

	@Override
	public boolean isPoolable() throws SQLException {
		throw new SQLException("Statement.isPoolable() not supported");
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		throw new SQLException("Statement.closeOnCompletion() not supported");
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		throw new SQLException("Statement.isCloseOnCompletion() not supported");
	}

}
