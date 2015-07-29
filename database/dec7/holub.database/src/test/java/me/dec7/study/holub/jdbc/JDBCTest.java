package me.dec7.study.holub.jdbc;

import java.sql.*;

public class JDBCTest {

	static String[] data = {
		"(1, 'John', 'Mon', 1, 'JustJoe')",
		"(2, 'JS', 'Mon', 1, 'Cappuccino')",
		"(3, 'Marie', 'Mon', 2, 'CaffeMocha')"
	};
	
	public static void main(String[] args) throws Exception {
		Class.forName("me.dec7.study.holub.jdbc.JDBCDriver").newInstance();
		
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = DriverManager.getConnection("file:/Users/lineplus/Documents/workspace/dec7/data/holub_pattern/dbase", "harpo", "swordfish");
			statement = connection.createStatement();
			
			statement.executeUpdate(
					"create table test(" +
					"Entry INTEGER NOT NULL, " +
					"Customer VARCHAR (20) NOT NULL, " +
					"DOW VARCHAR (3) NOT NULL, " +
					"Cups INTEGER NOT NULL," +
					"Type VARCHAR (10) NOT NULL, " +
					"PRIMARY KEY (Entry)" + 
					")");
			
			for (int i=0; i<data.length; ++i) {
				statement.execute("INSERT INTO test VALUES " + data[i]);
			}
			
			connection.setAutoCommit(false);
			statement.executeUpdate("INSERT INTO test VALUES (4, 'James', 'Thu', 1, 'Cappuccino')");
			connection.commit();
			
			statement.executeUpdate("INSERT INTO test (Customer) VALUES ('Fred')");
			connection.rollback();
			connection.setAutoCommit(true);
			
			// 모든것을 출력
			ResultSet result = statement.executeQuery("SELECT * FROM test");
			while (result.next()) {
				System.out.println(
						result.getInt("Entry") + ", " +
						result.getString("Customer") + ", " +
						result.getString("DOW") +", " +
						result.getInt("Cups") + ", " +
						result.getString("Type"));
			}
		}
		finally {
			
			try {
				if (statement != null) {
					statement.close();
				}
			}
			catch (Exception e) { }
			
			try {
				if (connection != null) {
					connection.close();
				}
			}
			catch (Exception e) { }
		}
	}
}
