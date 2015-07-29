package me.dec7.study.holub.database.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class JDBCDriver implements Driver {
	
	private JDBCConnection connection;
	
	static {
		
		try {
			DriverManager.registerDriver(new JDBCDriver());
		}
		catch (SQLException e) {
			System.err.println(e);
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		
		try {
			
			return connection = new JDBCConnection(url);
		}
		catch (Exception e) {
			
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		
		return url.startsWith("file:/");
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		
		return new DriverPropertyInfo[0];
	}

	@Override
	public int getMajorVersion() {
		
		return 1;
	}

	@Override
	public int getMinorVersion() {
		
		return 0;
	}

	@Override
	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
