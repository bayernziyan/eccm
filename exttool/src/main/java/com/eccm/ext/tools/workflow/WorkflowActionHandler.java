package com.eccm.ext.tools.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

@SuppressWarnings("unused")
public abstract class WorkflowActionHandler {
	
	private String name = "";
	private int status;
	private List<String> errmsg = Collections.<String>emptyList();
	private HashMap<String,ArrayList<String>> argRelationShip = null;
	
	public  WorkflowActionHandler(String name,HashMap<String,ArrayList<String>> argRelationShip){
		this.name = name;
		this.argRelationShip = argRelationShip;
	}
	
	public WorkflowActionHandler(String name){
		this.name = name;
	}
	public WorkflowActionHandler(){}
	public abstract void doHandler(WorkflowAction action,Connection conn);
	
	
	public ArrayList<String> getRelateArgList(String name){
		if( null == argRelationShip )return null;
		if( argRelationShip.containsKey(name) ){
			return argRelationShip.get(name);
		}
		return null;
	}
	
	protected void setArg(WorkflowAction action,String key,Object v){
		action.argIn(key, v);
	}
	protected Object getArg(WorkflowAction action,String key){
		return action.argOut(key);
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
		if(this.name.isEmpty()) return true;
		return this.name .equals(((WorkflowActionHandler)obj).getName());
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode() << 6 + this.name.length();
	}
	
	public String getName(){return this.name;}
	private boolean isError(){return status == 2?true :false;}
	private void success(){ status = 1; }
	private void error(){ status = 2; }
}
