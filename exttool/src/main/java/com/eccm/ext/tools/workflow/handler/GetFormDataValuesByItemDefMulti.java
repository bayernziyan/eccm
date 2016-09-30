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
import com.econage.eccm.bean.RequestSheetBean;
/**
 * 
 * @author bayern
 * <pre>
 * 基于当前流程模板
 * 获取满足条件的表单内容，通过表单组件自定义标识 获取对应的参数值
 * 栏位名需要大写，栏位为数值的 需要加双引号"36400"
 * </pre>
 * 	  @param param_in_trigger 其他输入参数之前读取，如果存在执行ParamTranslator 生成输入参数
 * 	  @param param_in_list 为表单组件自定义标识
 * 	  @param param_in_pagestart_int 分页起始序号
 *    @param param_in_pagesize_int 每页记录数
 * 	  @param param_in_whereitem_string 表单组件筛选条件
 *    @param param_in_whereworkflow_string 流程筛选条件
 * 
 * 	  @return param_out_list_map 为表单组件ID 和 对应参数值的映射 列表 {@link List&lt;HashMap&lt;String,Object&gt;&gt;}
 * 
 */
public class GetFormDataValuesByItemDefMulti extends WorkflowActionHandler {
	private static final Logger LOG = Logger.getLogger(GetFormDataValuesByItemDefMulti.class);
	
	public static final String name = "get_form_data_itemdef_multi";
	
	
	public static final String param_in_trigger = name+"_in_trigger";
	public static final String param_in_list = name+"_in_list";
	/**
	 * 开始序号
	 */
	public static final String param_in_pagestart_int = name+"_pagestart";
	/**
	 * 单页记录数
	 */
	public static final String param_in_pagesize_int = name + "_pagesize";
	/**
	 * 对表单组件值的筛选
	 */
	public static final String param_in_whereitem_string = name + "_whereitem";
	/**
	 * 对流程的筛选条件
	 */
	public static final String param_in_whereworkflow_string = name + "_whereworkflow";
	
	public static final String param_out_list_map = name+"_out_formdata_byid_list_hashmap";
	
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
		Object _pagestart = getArg(action, param_in_pagestart_int);
		Object _pagesize = getArg(action, param_in_pagesize_int);
		Object _whereitem = getArg(action, param_in_whereitem_string);
		Object _whereworkflow = getArg(action,param_in_whereworkflow_string);
		try{	
			if( null == _list ) throw new IllegalArgumentException("参数为空");
			if( null == _pagestart) _pagestart = Integer.parseInt("0");
			if( null == _pagesize) _pagesize = Integer.parseInt("10");
			RequestSheetBean wfbean = action.getWfBean();
			String whereitem = null == _whereitem?"":String.valueOf(_whereitem);
			String whereworkflow = null == _whereworkflow?"":String.valueOf(_whereworkflow);
			
			
			
			List<HashMap<String,Object>> map = WorkflowUtil.getFormDataValueListByItemDefList(conn, (List<String>)_list, wfbean.getTemplateId(),wfbean.getFormId(),whereitem, whereworkflow, Integer.parseInt(String.valueOf(_pagestart)), Integer.parseInt(String.valueOf(_pagesize)));
			if(null != map) setArg(action, param_out_list_map, map);			
		}catch(Exception e){setException(e);LOG.error(e);}
	}
	public GetFormDataValuesByItemDefMulti(){}
	public GetFormDataValuesByItemDefMulti(HashMap<String,ArrayList<String>> argRelationShip){
		this.argRelationShip = argRelationShip;
	}

}
