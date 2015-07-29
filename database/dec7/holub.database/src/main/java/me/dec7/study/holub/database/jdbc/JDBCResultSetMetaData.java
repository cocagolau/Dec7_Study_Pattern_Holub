package me.dec7.study.holub.database.jdbc;

import java.sql.SQLException;
import java.sql.Types;

import me.dec7.study.holub.database.Cursor;
import me.dec7.study.holub.database.jdbc.adapters.ResultSetMetaDataAdapter;

public class JDBCResultSetMetaData extends ResultSetMetaDataAdapter {

	private final Cursor cursor;
	
	public JDBCResultSetMetaData(Cursor cursor) {
		this.cursor = cursor;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		
		return Types.VARCHAR;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		
		return "VARCHAR";
	}
	
	

}
