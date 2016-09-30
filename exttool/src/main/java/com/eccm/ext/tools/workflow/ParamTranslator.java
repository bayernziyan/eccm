package com.eccm.ext.tools.workflow;

public abstract class ParamTranslator {
	
	public String paramName;	
	public WorkflowAction action;
	public  ParamTranslator(WorkflowAction action,String paramName){
		this.paramName = paramName;		
		this.action = action;
	}
	public abstract void translate(); 
}
