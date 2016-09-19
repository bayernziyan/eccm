package com.eccm.ext.tools.db;


import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ExtDBProvider {
	private static final ExtDBProvider provider = new ExtDBProvider(); 
	
	private HashMap<String, DataSourceHandler> dsMap = new HashMap<String ,DataSourceHandler>();
	private ReentrantLock dslock = new ReentrantLock(true);
	private Condition cond = dslock.newCondition();
	
	
	
	
	public DataSourceHandler getDataSourceHandler(String dbname){
		if(dsMap.containsKey(dbname)){return dsMap.get(dbname);}
		dslock.lock();
		try{
			System.out.println(dbname+"----lock");
			if(dsMap.containsKey(dbname)){return dsMap.get(dbname);}
			DataSourceHandler db = new DataSourceHandler(dbname,true);
			dsMap.put(dbname, db);
			return dsMap.get(dbname);
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			System.out.println(dbname+"----unlock");
			dslock.unlock();
			
		}
		return null;
	}
	
	public static ExtDBProvider getInstance(){
		return provider;
	}
	private ExtDBProvider(){}
	
}
