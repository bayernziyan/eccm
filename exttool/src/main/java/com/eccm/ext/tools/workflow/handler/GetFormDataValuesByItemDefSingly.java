package com.eccm.ext.tools.workflow.handler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.eccm.ext.tools.util.WorkflowUtil;
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;

public class GetFormDataValuesByItemDefSingly extends WorkflowActionHandler {
	private static final Logger LOG = Logger.getLogger(GetFormDataValuesByItemDefSingly.class);
	
	public static final String name = "get_form_data_itemdef_single";	
	public static final String param_in_list = name+"_in_list";
	public static final String param_out_map = name+"_out_getformdata_bydef_hashmap";
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void doHandler(WorkflowAction action,Connection conn) {
		Object _list  =  getArg(action, param_in_list);		
		try{	
			if( null == _list ) throw new IllegalArgumentException("参数为空");
			HashMap<String,String> map = WorkflowUtil.getFormDataValueByItemDefList(conn,(List<String>)_list, action.getWfBean().getFormDataId(),action.getWfBean().getFormId());
			if(null != map) {
				setArg(action, param_out_map, map);
			}			
		}catch(Exception e){setException(e);LOG.error(e);}
	}

}
