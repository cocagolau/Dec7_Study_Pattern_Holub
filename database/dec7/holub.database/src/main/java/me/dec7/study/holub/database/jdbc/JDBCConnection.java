package me.dec7.study.holub.database.jdbc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;

import me.dec7.study.holub.database.Database;
import me.dec7.study.holub.database.jdbc.adapters.ConnectionAdapter;
import me.dec7.study.holub.text.ParseFailure;

public class JDBCConnection extends ConnectionAdapter {
	
	private Database database;

	public JDBCConnection(String uri) throws SQLException, URISyntaxException, IOException {
		this(new URI(uri));
	}
	
	public JDBCConnection(URI uri) throws SQLException, IOException {
		database = new Database(uri);
	}

	/*
	 * 데이터베이스 커넥션을 닫는다.
	 * 오토커밋모드가 비황성화되어 있다면 자동으로 커밋 수행
	 */
	@Override
	public void close() throws SQLException {
		
		try {
			autoCommitState.close();
			
			database.dump();
			
			/*
			 * 메모리를 재사용할 수 있도록 함.
			 * 그리고 누군가 이미 close한 커넥션을 사용하려 시도시 NullPointerException을 던지도록 함
			 */
			database = null;
		}
		catch (IOException e) {
			
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Statement createStatement() throws SQLException {
		
		return new JDBCStatement(database);
	}

	/*
	 * 현재 트랜잭션을 끝내고, 새로운 트랜잭션 시작
	 * 오토커밋모드에서는 아무일도 하지 않음
	 */
	@Override
	public void commit() throws SQLException {
		autoCommitState.commit();
	}

	/*
	 * 현재 트랜잭션을 롤백하고, 새로운 트랜잭션을 시작
	 * 오토커밋모드에서는 아무일도 하지 않음
	 */
	@Override
	public void rollback() throws SQLException {
		autoCommitState.rollback();
	}

	/*
	 * true로 셋팅시, 모든 SQL문들이 트랜잭션을 형성
	 * 오토커밋모드가 비활성화시 begin이 자동으로 호출되어 commit, rollback 메소드가 정상적으로 작동하도록 함
	 * 비슷하게 오토커밋모드가 활성화될 떄에도 commit이 자동으로 호출됨
	 */
	@Override
	public void setAutoCommit(boolean enable) throws SQLException {
		autoCommitState.setAutoCommit(enable);
	}

	/*
	 * 오토커밋모드가 활성화시 true를 반환
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		
		return autoCommitState == enabled;
	}
	
	
	private interface AutoCommitBehavior {
		
		void close() throws SQLException;
		void commit() throws SQLException;
		void rollback() throws SQLException;
		void setAutoCommit(boolean enable) throws SQLException;
		
	}
	
	private AutoCommitBehavior enabled = new AutoCommitBehavior() {

		@Override
		public void close() throws SQLException {
			// do nothing
		}

		@Override
		public void commit() throws SQLException {
			// do nothing
		}

		@Override
		public void rollback() throws SQLException {
			// do nothing
		}

		@Override
		public void setAutoCommit(boolean enable) throws SQLException {
			
			if (enable == false) {
				database.begin();
				autoCommitState = disabled;
			}
		}
		
	};
	
	private AutoCommitBehavior disabled = new AutoCommitBehavior() {

		@Override
		public void close() throws SQLException {
			
			try {
				database.commit();
			}
			catch (ParseFailure e) {
				
				throw new SQLException(e.getMessage());
			}
		}

		@Override
		public void commit() throws SQLException {
			
			try {
				database.commit();
				database.begin();
			}
			catch (ParseFailure e) {
				
				throw new SQLException(e.getMessage());
			}
			
		}

		@Override
		public void rollback() throws SQLException {
			
			try {
				database.rollback();
				database.begin();
			}
			catch (ParseFailure e) {
				
				throw new SQLException(e.getMessage());
			}
			
		}

		@Override
		public void setAutoCommit(boolean enable) throws SQLException {
			
			try {
				
				if (enable == true) {
					database.commit();
					autoCommitState = enabled;
				}
			}
			catch (ParseFailure e) {
				
				throw new SQLException(e.getMessage());
			}
		}
		
	};
	
	private AutoCommitBehavior autoCommitState = enabled;

}
