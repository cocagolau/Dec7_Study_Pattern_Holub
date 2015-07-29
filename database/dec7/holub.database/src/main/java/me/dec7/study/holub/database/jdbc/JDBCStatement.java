package me.dec7.study.holub.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.dec7.study.holub.database.Database;
import me.dec7.study.holub.database.Table;
import me.dec7.study.holub.database.jdbc.adapters.StatementAdapter;

public class JDBCStatement extends StatementAdapter {
	
	Database database;
	
	public JDBCStatement(Database database) {
		this.database = database;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		
		try {
			database.execute(sql);
			
			return database.affectedRows();
		}
		catch (Exception e) {
			
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		
		try {
			Table result = database.execute(sql);
			
			return new JDBCResultSet(result.rows());
		}
		catch (Exception e) {
			
			throw new SQLException(e.getMessage());
		}
	}


}
