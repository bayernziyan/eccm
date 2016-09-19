package com.eccm.ext.tools;


import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.eccm.ext.tools.db.DataSourceHandler;
import com.eccm.ext.tools.db.DbEventThread;
import com.eccm.ext.tools.db.ExtDBProvider;
import com.eccm.ext.tools.db.exception.DatabaseRequestException;
import com.eccm.ext.tools.db.pojo.DBConnectionResource;

public class TestUtilTest {
	@Test
	public void test1(){
		//ThreadPoolManager.init();
		DataSourceHandler db  = null;
		try {
			 db =  ExtDBProvider.getInstance().getDataSourceHandler("whe8");
			/*			
			DBConnectionResource sqlsource = db.executeQuery("select 1 from dual");
			if(sqlsource.resultSet.next())
				System.out.println(sqlsource.resultSet.getString(1));
			
			db.closeResultSetAndStatement(sqlsource.resultSet, sqlsource.statement);*/
			// System.out.println(Thread.currentThread().getId());
			 db.executeInsert("insert into ext_test (1,'a','val_a')", true);
			 //System.out.println(Thread.currentThread().getId());
			
			 try {
					Thread.sleep(TimeUnit.DAYS.toDays(10000000));
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			
		}  catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	
	public void test() {
		//DataSourceBuilder db =  ExtDBProvider.getInstance().getDataSourcBuilder("wh");
				Thread th1 =	new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i=0;
						//while(true){
						DataSourceHandler db  = null;
						try {
							 db =  ExtDBProvider.getInstance().getDataSourceHandler("whe8");
							/*			
							DBConnectionResource sqlsource = db.executeQuery("select 1 from dual");
							if(sqlsource.resultSet.next())
								System.out.println(sqlsource.resultSet.getString(1));
							
							db.closeResultSetAndStatement(sqlsource.resultSet, sqlsource.statement);*/
							 
							 db.executeInsert("insert into ext_test (1,'a','val_a')", true);
						
							
							
							
							
						}  catch (Exception e) {
							e.printStackTrace();
						}finally{
							
						}
						//}
					}
				});
				
				Thread th2 =	new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i=0;
						while(true){
						try {
							DataSourceHandler db =  ExtDBProvider.getInstance().getDataSourceHandler("wh");
							Connection conn = db.getConnection();
							
							System.out.println((conn == null)+"@@"+(++i));
							Thread.sleep(2000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					}
				});
				
				//Thread.currentThread().yield();
				th1.start();th1.yield();
				try {
					Thread.currentThread().sleep(TimeUnit.DAYS.toDays(2));
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//th2.start();th2.yield();
				try {
					th1.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
