package com.eccm.ext.tools.db;

import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import com.eccm.ext.tools.db.exception.DatabaseRequestException;


public class DbEventThread extends Thread {

    private static final Logger LOG = Logger.getLogger(DbEventThread.class);

    private BlockingQueue<DbEvent> _events_1m = new  LinkedBlockingQueue<DbEvent>();
    private BlockingQueue<DbEvent> _events_30m = new  LinkedBlockingQueue<DbEvent>();
    private BlockingQueue<DbEvent> _events_1h = new  LinkedBlockingQueue<DbEvent>();
    private BlockingQueue<DbEvent> _events_24h = new  LinkedBlockingQueue<DbEvent>();
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition cond = lock.newCondition();
    
    private final String dataSourceName;
    private final DataSourceHandler _dh ;
    private static AtomicLong _eventId = new AtomicLong(0);

    static abstract class DbEvent<T> {
    	
        private String _sql;
        private long _eventId;
        private long latestTime;
        private AtomicInteger count = new AtomicInteger(0);

        public DbEvent(String sql) {
            _sql = sql;            
        }
        public void setEventId(long eventId){
        	_eventId = eventId;
        }
        public long getEvenId(){
        	return _eventId;
        }
        public int getCount(){
        	return count.get();
        }
        boolean event() throws Exception {
        	long systime = System.currentTimeMillis();
        	if(latestTime != 0l){
        		double diff = (systime - latestTime)/(1000.0);        	
        		int c = count.get();
        		//LOG.debug(c+"!!"+diff);
        		if(c < 10 && diff < 60)
        			return false;
        		else if( c ==10 && (diff/60.0) < 30)
        			return false;
        		else if( c == 11 && (diff/3600.0) < 1)
        			return false;
        		else if( c == 12 && (diff/3600.0) < 24 )
        			return false;
        		else if (c > 12)
        			return true;
        	}
        	try{
        		 LOG.debug("["+ Thread.currentThread().getName() + "] Delivering event #" + _eventId + " " + toString());	
        	return run();
        	}finally{
        		int c = count.incrementAndGet();
        		latestTime = systime;
        		LOG.debug("[eventId-"+_eventId+"] execute times: "+c);
        	}
        }
        
        abstract boolean run() throws Exception;

        @Override
        public String toString() {
            return "DbEvent[" + _sql + "]";
        }
    }

    public DbEventThread(String name,DataSourceHandler dh) {       
        dataSourceName = name;
        _dh = dh;      
       setName("DataSource-EventThread-" + getId() + "-" + name);
    }
    
    public boolean checkConnection(){
    	Connection conn = null;
    	boolean check = false;
    	try{
    		conn = _dh.getConnection();
    		check = null == conn ?false :true;
    	}catch(Exception e){
    		LOG.debug("["+getName()+"] check connection error: "+e.getMessage());
    	}finally{
    		_dh.returnBackConnectionToPool(conn);
    	}
    	return check;
    }
    
    @Override
    public void run() {
    	//System.out.println(getName()+"Starting DataSource event thread.");
        LOG.info("["+getName()+"] Starting DataSource event thread.");
        try {
            while (!isInterrupted()) {
            	DbEvent dbEvent = null;
            	//System.out.println(Thread.currentThread().getName()+"=========="+_events.size());
            	lock.lock();
            	try {
            		 boolean stillWaiting = true;
            		 while(!checkConnection()){
            		
            			stillWaiting = cond.await(5, TimeUnit.SECONDS);  
            			//LOG.debug("["+getName()+"] checkconnection isstill:"+stillWaiting);
            		 }
            		 
	                 dbEvent = _events_1m.peek();
	                if( null != dbEvent){	                 
		                long eventId = dbEvent.getEvenId();
		                if(eventId == 0l){
		                	eventId = _eventId.incrementAndGet();
		                	dbEvent.setEventId(eventId);               	
		                }
		               
		               
		                if( dbEvent.event()){
		               	  _events_1m.remove(dbEvent);
		               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] Delivering event #" + dbEvent.getEvenId() + " done");
		               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] show  dbevents_1m size " + _events_1m.size() );
		                }else {	                 
		                  int c = dbEvent.getCount() ;
		               	  if(c == 10){
		               		 _events_1m.remove(dbEvent);
		               		  _events_30m.add(dbEvent);
		               		LOG.info("["+getName()+"] - [eventId:"+eventId+"] event (" + dbEvent.toString() + ") move from 1m queue to 30m queue");
		               	  }
		                }
	                }
	                dbEvent = _events_30m.peek();
	                if( null != dbEvent){	                 
		                long eventId = dbEvent.getEvenId();
		                if(eventId == 0l){
		                	eventId = _eventId.incrementAndGet();
		                	dbEvent.setEventId(eventId);               	
		                }
		                if( dbEvent.event()){
			               	  _events_30m.remove(dbEvent);
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] Delivering event #" + dbEvent.getEvenId() + " done");
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] show  dbevents_30m size " + _events_30m.size() );
			                }else {	                 
			                  int c = dbEvent.getCount() ;
			               	  if(c == 11 ){
			               		 _events_30m.remove(dbEvent);
			               		  _events_1h.add(dbEvent);
			               		LOG.info("["+getName()+"] - [eventId:"+eventId+"] event (" + dbEvent.toString() + ") move from 30m queue to 1h queue");
			               	  }
			            }
	                }
	                // 1h 
	                dbEvent = _events_1h.peek();
	                if( null != dbEvent){	                 
		                long eventId = dbEvent.getEvenId();
		                if(eventId == 0l){
		                	eventId = _eventId.incrementAndGet();
		                	dbEvent.setEventId(eventId);               	
		                }
		                if( dbEvent.event()){
			               	  _events_1h.remove(dbEvent);
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] Delivering event #" + dbEvent.getEvenId() + " done");
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] show  dbevents_1h size " + _events_1h.size() );
			                }else {	                 
			                  int c = dbEvent.getCount() ;
			               	  if(c == 12 ){
			               		 _events_1h.remove(dbEvent);
			               		  _events_24h.add(dbEvent);
			               		 LOG.info("["+getName()+"] - [eventId:"+eventId+"] event (" + dbEvent.toString() + ") move from 1h queue to 24h queue");
			               	  }
			            }
	                }
	                
	                // 24h 
	                dbEvent = _events_24h.peek();
	                if( null != dbEvent){	                 
		                long eventId = dbEvent.getEvenId();
		                if(eventId == 0l){
		                	eventId = _eventId.incrementAndGet();
		                	dbEvent.setEventId(eventId);               	
		                }
		                if( dbEvent.event()){
			               	  _events_24h.remove(dbEvent);
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] Delivering event #" + dbEvent.getEvenId() + " done");
			               	  LOG.debug("["+getName()+"] - [eventId:"+eventId+"] show  dbevents_24h size " + _events_24h.size() );
			                }else {	                 
			                  int c = dbEvent.getCount() ;
			               	  if(c > 12 ){
			               		 _events_24h.remove(dbEvent);	
			               		 LOG.error("["+getName()+"] - [eventId:"+eventId+"] event (" + dbEvent.toString() + ") removed");
				               	 LOG.debug("["+getName()+"] - [eventId:"+eventId+"] show  dbevents_24h size " + _events_24h.size() );
			               	  }
			            }
	                }
	                
	                
                } catch (InterruptedException e) {
                	LOG.error(e);
                	Thread.currentThread().interrupt();
                } catch (DatabaseRequestException e) {
                	LOG.warn(e.getMessage());
                	continue;
                } catch (Throwable e) {
                	LOG.error("["+getName()+"] Error handling event " + dbEvent, e);
                }finally{          	
                	lock.unlock();
                }
               
            }
        } catch (Exception e) {        	
            LOG.info("["+getName()+"] Terminate dbevent thread.");
        }
    }
    
  /*  public void setEventQueue(BlockingQueue<DbEvent> events){
    	_events = events;
    }
    */
    public void send(DbEvent event) {
        if (!isInterrupted()) {
            LOG.debug("New event: " + event);            
            _events_1m.add(event);
            
        }
    }
}
