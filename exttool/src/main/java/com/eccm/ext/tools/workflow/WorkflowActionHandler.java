package com.eccm.ext.tools.workflow;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

@SuppressWarnings("unused")
public abstract class WorkflowActionHandler {
	
	
	private int status;
	private ArrayList<String> errmsg = (ArrayList<String>) Collections.<String>emptyList();
	
	
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
	
	private boolean isError(){return status == 2?true :false;}
	private void success(){ status = 1; }
	private void error(){ status = 2; }
}
