package me.dec7.study.holub.database.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;

import me.dec7.study.holub.database.Cursor;
import me.dec7.study.holub.database.jdbc.adapters.ResultSetAdapter;
import me.dec7.study.holub.text.ParseFailure;

public class JDBCResultSet extends ResultSetAdapter {
	
	private final Cursor cursor;
	
	private static final NumberFormat format = NumberFormat.getInstance();
	
	public JDBCResultSet(Cursor cursor) throws SQLException {
		this.cursor = cursor;
	}
	
	@Override
	public boolean next() {
		
		return cursor.advance();
	}
	
	@Override
	public String getString(String columnName) throws SQLException {
		
		try {
			Object contents = cursor.column(columnName);
			
			return (contents == null) ? null : contents.toString();
		}
		catch (IndexOutOfBoundsException e) {
			
			throw new SQLException("column " + columnName + "doesn't exist");
		}
	}
	
	@Override
	public double getDouble(String columnName) throws SQLException {
		
		try {
			String contents = getString(columnName);
			
			return (contents == null) ? 0.0 : format.parse(contents).doubleValue();
		}
		catch (ParseException e) {
			
			throw new SQLException("filed doesn't contain a number");
		}
	}
	
	@Override
	public int getInt(String columnName) throws SQLException {
		
		try {
			String contents = getString(columnName);
			
			return (contents == null) ? 0 : format.parse(contents).intValue();
		}
		catch (ParseException e) {
			
			throw new SQLException("field doesn't contain a number");
		}
	}
	
	@Override
	public long getLong(String columnName) throws SQLException {
		
		try {
			String contents = getString(columnName);
			
			return (contents == null) ? 0L : format.parse(contents).longValue();
		}
		catch (ParseException e) {
			
			throw new SQLException("field doesn't contain a number");
		}
	}
	
	@Override
	public void updateNull(String columnName) {
		cursor.update(columnName, null);
	}
	
	@Override
	public void updateDouble(String columnName, double value) {
		cursor.update(columnName, format.format(value));
	}
	
	@Override
	public void updateInt(String columnName, int value) {
		cursor.update(columnName, format.format(value));
	}
	
	@Override
	public void updateLong(String columnName, long value) {
		cursor.update(columnName, format.format(value));
	}
	
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		
		return new JDBCResultSetMetaData(cursor);
	}
	

}
