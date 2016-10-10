package com.eccm.ext.tools.workflow.handler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.eccm.ext.tools.util.WorkflowUtil;
import com.eccm.ext.tools.workflow.ParamTranslator;
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;
/**
 * 
 * @author bayern
 * <pre>基于当前流程，通过自定义标识 获取对应的参数值</pre>
 * 
 * 	  @param param_in_trigger 其他输入参数之前读取，如果存在执行ParamTranslator 生成输入参数
 * 	  @param param_in_list 为自定义标识列表
 * 	  @return param_out_map 为自定义标识 和 对应参数值的映射
 */
public class GetFormDataValuesByItemDefSingly extends WorkflowActionHandler {
	private static final Logger LOG = Logger.getLogger(GetFormDataValuesByItemDefSingly.class);
	
	public static final String name = "get_form_data_itemdef_single";	
	
	public static final String param_in_trigger = name+"_in_trigger";
	public static final String param_in_list = name+"_in_list";
	/**
	 *  HashMap&lt;String,String&gt;
	 */
	public static final String param_out_map = name+"_out_getformdata_bydef_hashmap";
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void doHandler(WorkflowAction action,Connection conn) {
		Object _trigger = getArg(action, param_in_trigger);
		if(null!=_trigger && _trigger instanceof ParamTranslator)
			((ParamTranslator)_trigger).translate();
		Object _list  =  getArg(action, param_in_list);		
		try{	
			if( null == _list ) throw new IllegalArgumentException("参数为空");
			HashMap<String,String> map = WorkflowUtil.getFormDataValueByItemDefList(conn,(List<String>)_list, action.getWfBean().getFormDataId(),action.getWfBean().getFormId());
			if(null != map) {
				setArg(action, param_out_map, map);
			}			
		}catch(Exception e){setException(e);LOG.error(e);}
	}
	
	public GetFormDataValuesByItemDefSingly(){}
	public GetFormDataValuesByItemDefSingly(HashMap<String,ArrayList<String>> argRelationShip){
		this.argRelationShip = argRelationShip;
	}
}
