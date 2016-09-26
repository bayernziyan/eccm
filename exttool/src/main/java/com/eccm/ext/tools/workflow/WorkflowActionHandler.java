package com.eccm.ext.tools.workflow;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

@SuppressWarnings("unused")
public abstract class WorkflowActionHandler {
	
	private String name = "";
	private int status;
	private ArrayList<String> errmsg = (ArrayList<String>) Collections.<String>emptyList();
	
	
	public WorkflowActionHandler(String name){
		this.name = name;
	}
	public WorkflowActionHandler(){}
	public abstract void doHandler(WorkflowAction action);
	
	
	public void setException(Exception e){
		Throwable cause = e;
		do{
			if(cause.getCause()!=null)
				cause = cause.getCause();
			else break;
		}while(cause != null);
		errmsg.add(cause.getMessage());
		error();
	}
	public ArrayList<String> getException(){
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
