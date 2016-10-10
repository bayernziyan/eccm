package com.eccm.ext.tools.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.log4j.Logger;

@SuppressWarnings("unused")
public abstract class WorkflowActionHandler {
	private static final Logger LOG = Logger.getLogger(WorkflowActionHandler.class);
	private String name = "";
	private int status;
	private List<String> errmsg = Collections.<String>emptyList();
	protected HashMap<String,ArrayList<String>> argRelationShip = null;
	
	public  WorkflowActionHandler(String name,HashMap<String,ArrayList<String>> argRelationShip){
		this.name = name;
		this.argRelationShip = argRelationShip;
	}
	public  WorkflowActionHandler(HashMap<String,ArrayList<String>> argRelationShip){		
		this.argRelationShip = argRelationShip;
	}
	
	public WorkflowActionHandler(String name){
		this.name = name;
	}
	public WorkflowActionHandler(){ this.name = UUID.randomUUID().toString();}
	public abstract void doHandler(WorkflowAction action,Connection conn);
	
	
	public ArrayList<String> getRelateArgList(String name){
		if( null == argRelationShip )return null;
		if( argRelationShip.containsKey(name) ){
			return argRelationShip.get(name);
		}
		return null;
	}
	
	public void setArg(WorkflowAction action,String key,Object v){
		action.argIn(key, v);
	}
	public Object getArg(WorkflowAction action,String key){
		Object _obj = action.argOut(key);
		if( null == _obj){
			ArrayList<String> inlistRel = getRelateArgList(key);
			if(  null!=inlistRel){
				for(String i : inlistRel){
					_obj  =  action.argOut(i);
					if(null!=_obj){LOG.info(getName()+",从["+i+"]获取参数:"+_obj);break;}
				}
			}
		}
		return _obj;
	}
	
	public void setException(Exception e){
		Throwable cause = e;
		do{
			if(cause.getCause()!=null)
				cause = cause.getCause();
			else break;
		}while(cause != null);
		if(errmsg.isEmpty()) errmsg = new ArrayList<String>();
		errmsg.add(cause.getMessage());
		error();
	}
	public List<String> getException(){
		return errmsg;
	}
	
	@Override
	public boolean equals(Object obj) {
		 if(!(obj instanceof WorkflowActionHandler)){  
	            return false;  
	        }  
		if(getName().isEmpty()) return true;
		return getName() .equals(((WorkflowActionHandler)obj).getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode() << 6 + getName().length();
	}
	
	public String getName(){return this.name;}
	private boolean isError(){return status == 2?true :false;}
	private void success(){ status = 1; }
	private void error(){ status = 2; }
}
