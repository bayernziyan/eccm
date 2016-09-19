package com.eccm.ext.tools.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp.BasicDataSource;
import com.eccm.ext.tools.db.DbEventThread.DbEvent;
import com.eccm.ext.tools.db.exception.DatabaseRequestException;
import com.eccm.ext.tools.db.pojo.DBConnectionResource;

public class DataSourceHandler extends AbstractDataSourceHandler {
	private final String dataSourceName;
	private volatile BasicDataSource extDataSource = null;
	
	private final DbEventThread _eventThread;
	private boolean deamon = false;
	
	public DataSourceHandler(String dataSourceName) {
		this.dataSourceName = dataSourceName;
		_eventThread = null;
	}
	
	public DataSourceHandler(String dataSourceName,boolean deamon) {
		this.dataSourceName = dataSourceName;
		_eventThread = new DbEventThread(dataSourceName,this);
		this.deamon = deamon;
	}
	
	
	
	
	public  void returnBackConnectionToPool( Connection conn ) {
		if ( conn != null )
			try {
				conn.close();
			} catch ( SQLException e ) {
			}// IGNORE
	}
	
	
	/**
	 * 执行插入SQL<br>
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public int executeInsert( String insertSql ) throws DatabaseRequestException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			if ( null == conn )
				throw new DatabaseRequestException( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( insertSql );
		} catch ( Exception e ) {
			throw new DatabaseRequestException( "Error when execute insert [" + insertSql + "],error: " + e.getMessage() , e.getCause() );
		} finally {
			closeResultSetAndStatement( null,stmt );
			returnBackConnectionToPool( conn );
		}
	}

	/**
	 *  flag=true，若插入失败，新建dbevent添加到队列中执行
	 * @param insertSql
	 * @param flag
	 * @return
	 */
	public int executeInsert( final String insertSql , boolean flag )  {
		try{
			return executeInsert(  insertSql );
		}catch( DatabaseRequestException e){
			e.printStackTrace();
			if(deamon && flag)
				_eventThread.send(new DbEvent<Boolean>(insertSql) {
				@Override
				public boolean run() throws Exception {
		
					return Integer.valueOf(executeInsert(insertSql)) > 0 ;
				}
			});
		}
		return 0;
	}
	
	
	/**
	 * 执行插入SQL,并获取最后一次插入主键值
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public  int executeInsertAndReturnGeneratedKeys( String insertSql ) throws DatabaseRequestException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( null == conn )
				throw new DatabaseRequestException( "No available connection" );
			stmt = conn.prepareStatement( insertSql, Statement.RETURN_GENERATED_KEYS );
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			if ( null != rs && rs.next() ) {
				return rs.getInt( 1 );
			}
			return -1;
		}catch ( Exception e ) {
			throw new DatabaseRequestException( "执行数据库插入[" + insertSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			closeResultSetAndStatement( rs, stmt );
			returnBackConnectionToPool( conn );
		}
	}

	/**
	 * 更新数据库 update
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public int executeUpdate( String updateSql ) throws DatabaseRequestException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			if ( null == conn )
				throw new DatabaseRequestException( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( updateSql );
		} catch ( Exception e ) {
			throw new DatabaseRequestException( "执行数据库更新[" + updateSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			closeResultSetAndStatement( null, stmt );
			returnBackConnectionToPool( conn );
		}
	}

	/**
	 * 删除
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public int executeDelete( String deleteSql ) throws DatabaseRequestException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			if ( null == conn )
				throw new DatabaseRequestException( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( deleteSql );
		} catch ( Exception e ) {
			throw new DatabaseRequestException( "执行数据库删除[" + deleteSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			closeResultSetAndStatement( null, stmt );
			returnBackConnectionToPool( conn );
		}
	}
	
	
	
	/**
	 * 执行查询SQL, 注意，执行完这个方法必须执行： <br>
	 * 1. closeResultSetAndStatement( resultSet, stmt ); <br>
	 * 2. returnBackConnectionToPool( conn );
	 * @param selectSql
	 *            查询SQL语句
	 */
	public  DBConnectionResource executeQuery( String querySql ) throws DatabaseRequestException {
		try {
			Connection conn = getConnection();
			if ( null == conn )
				throw new DatabaseRequestException( "No available connection" );
			Statement stmt = conn.createStatement();
			return new DBConnectionResource( conn, stmt, stmt.executeQuery( querySql ) );
		} catch ( Exception e ) {
			throw new DatabaseRequestException( "执行数据库查询[" + querySql + "]出错: " + e.getMessage(), e.getCause() );
		}
	}
	
	public void closeResultSetAndStatement( ResultSet resultSet, Statement statement ) {
		if ( resultSet != null )
			try {
				resultSet.close();
			} catch ( SQLException e ) {
			}// IGNORE}
		if ( null != statement )
			try {
				statement.close();
			} catch ( SQLException e ) {
			}// IGNORE
	}
	
	public synchronized Connection getConnection() throws Exception {
		if ( null == extDataSource ) {
			extDataSource = build();
			if(deamon && null != _eventThread)
				_eventThread.start();
		}
		Connection conn = null;
		if ( null != extDataSource ) {
			try {
				conn = extDataSource.getConnection();
			} catch ( Throwable e ) {
				throw new Exception( "Can't create conncetion, please make sure if database is available, " + e.getMessage(), e.getCause() );
			}
		}
		return conn;
	}
	
	/*public <T> T retryUntilConnected(Callable<T> callable) {
		while (true) {
		      try {
		        return callable.call();
		      } catch (Throwable e) {
		    	  e.printStackTrace();
		      }
		}
	}*/
	
	
	public BasicDataSource build() throws Exception {
		shutdownDataSource();
		return super.build();
	};
	
	@Override
	protected String getDatasourceName() {
		// TODO Auto-generated method stub
		return dataSourceName;
	}

	public  void shutdownDataSource() {
		if ( null != extDataSource ) {
			try {
				extDataSource.close();
			} catch ( Exception e ) {
				// ignore
			}
			extDataSource = null;
		}
	}
}
