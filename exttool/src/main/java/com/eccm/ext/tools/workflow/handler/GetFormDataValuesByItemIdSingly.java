package com.eccm.ext.tools.workflow.handler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.eccm.ext.tools.util.WorkflowUtil;
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;

public class GetFormDataValuesByItemIdSingly extends WorkflowActionHandler {
	private static final Logger LOG = Logger.getLogger(GetFormDataValuesByItemIdSingly.class);
	
	public final String name = "get_form_data_itemid_single";
	
	public final String param_in_list = name+"_in_list";
	public final String param_out_map = name+"_out_formdata_byid_hashmap";
	@Override
	public void doHandler(WorkflowAction action,Connection conn) {
		Object _list  =  getArg(action, param_in_list);		
		try{	
			ArrayList<String> inlistRel = getRelateArgList(param_in_list);
			if( null == _list && null!=inlistRel){
				for(String i : inlistRel){
					_list  =  getArg(action, i);
					if(null!=_list)break;
				}
			}
			if( null == _list ) throw new IllegalArgumentException("参数为空");
			HashMap<String,String> map = WorkflowUtil.getFormDataValueByItemIdList(conn, (List<String>)_list, Integer.parseInt(action.get_formDateId()));
			if(null != map) setArg(action, param_out_map, map);			
		}catch(Exception e){setException(e);LOG.error(e);}
	}

}
