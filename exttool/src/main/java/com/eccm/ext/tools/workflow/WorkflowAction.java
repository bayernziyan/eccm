package com.eccm.ext.tools.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eccm.ext.tools.util.StringUtil;
import com.eccm.ext.tools.workflow.ActionType;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemDefMulti;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemDefSingly;
import com.econage.eccm.biz.container.ContainerOperationHelper;
import com.econage.eccm.facade.SessionFacade;
import com.econage.eccm.framework.CommandException;
import com.econage.eccm.oa.form.FormHelper;
import com.econage.eccm.util.CodedValueHelper;
/**
 * <pre>参数赋值说明：
 * 方法一、直接赋值 -- {@code action.argIn(GetFormDataValuesByItemDefMulti.param_in_whereitem_string, "\"wjbt\"='"+v+"'");}
 * 
 * 方法二、动态赋值 -- {@code 
 * 	 action通过执行trigger 给 action添加参数
 * 	 action.argIn(GetFormDataValuesByItemDefMulti.param_in_trigger,new ParamTranslator(action,GetFormDataValuesByItemDefSingly.param_out_map) {
		public void translate() {
		//获取参数
		Object paramin = action.argOut(this.paramName);
		if(null == paramin) return;
		//todo 参数转换
		action.argIn(GetFormDataValuesByItemDefMulti.param_in_whereitem_string, "\"wjbt\"='"+v+"'");
	}}
 * 方法三、关联赋值 -- {@code  action 通过 getArg("test") 赋值给 GetFormDataValuesByItemDefSingly.param_in_list  
   HashMap<String,ArrayList<String>> argRelationShip = new HashMap<String, ArrayList<String>>();
   ArrayList<String> paramlist = new ArrayList<String>();paramlist.add("test1");
   argRelationShip.put(GetFormDataValuesByItemDefSingly.param_in_list, paramlist);
       初始化WorkflowActionHandler时作为参数传入
   action.addHandler(new GetFormDataValuesByItemDefSingly(argRelationShip))			 
	}
 * </pre>
 * @author bayern
 *
 */
public class WorkflowAction {
	private static final Logger LOG = Logger.getLogger(WorkflowAction.class);
	public WorkflowAction(ActionType type, HttpServletRequest req,Connection conn){
		this.conn = conn;
		if(null == req)return;
		_wfId = StringUtil.isBlank(req.getParameter("workflow_id"),req.getParameter("workplace_id"));
		_formDateId = req.getParameter("form_data_id");
		LOG.debug("workflow action init --- " + _wfId);
		init(type,conn,req.getSession(),_wfId,_formDateId,req.getParameter("save_flag"),req.getParameter("check_cmd"),req.getParameter("check_param"));
		
		//if(!StringUtil.isBlank(actName)) _init = true;
	}
	public void init(ActionType type,Connection conn,HttpSession session, String wfId,String formDataId,String save_flag,String checkCmd,String checkParam){
		_wfId = wfId;_formDateId = formDataId;
		if( StringUtil.isBlank(_wfId) )return;
		try{
			switch(type){
				case WF_ST: 
							wfBean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(wfId));
							actName = wfBean.getPre_cmd();
							actParam = wfBean.getPre_param();break;
				case WF_ED:	wfBean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(wfId));
							actName = wfBean.getEnd_cmd();
							actParam = wfBean.getEnd_param();break;
		//		case WF_CC: wf_bean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(wf_id));
		//					actName = wf_bean.getCancel_cmd();
		//					actParam = wf_bean.getCancel_param();break;
				case TK_CK1:if("1".equals(checkCmd))return;
				case TK_CK0:							
							actName = checkCmd;//req.getParameter("check_cmd");
							actParam = checkParam;//req.getParameter("check_param");break;
				case TK_ST:	taskBean = SessionFacade.getWorkflowTaskBean(session);
							actName = taskBean.getPre_cmd();
							actParam = taskBean.getPre_param();break;						
				case TK_ED: taskBean = SessionFacade.getWorkflowTaskBean(session);
							actName = taskBean.getPost_cmd();
							actParam = taskBean.getPost_param();break;
			}
			if(!StringUtil.isBlank(actParam)){
				params_v = CodedValueHelper.getFormItemColumnWithPrefix(actParam);
		        values_v = FormHelper.getFormItemValueWithPrefix2(conn,String.valueOf(_formDateId), params_v);
			}
		}catch(CommandException e ){e.printStackTrace();LOG.error(e);}
		if(!StringUtil.isBlank(actName)) _init = true;
	}
	
	public WorkflowAction addHandler(WorkflowActionHandler handler){
		if(!_init){LOG.warn("action 未被初始化");return this;}
		if(null == handler )return this;
		if(hds.isEmpty()) hds = new LinkedHashSet<WorkflowActionHandler>();
		if(!hds.add(handler))
			LOG.warn("添加流程处理事件["+handler.getName()+"]失败！");
		return this;
	}
	
	public void execute() throws CommandException {
		if(!_init)return;
		if(hds.isEmpty())return;
        
		for(WorkflowActionHandler hd : hds){
			hd.doHandler(this,this.conn);
			List<String> err = hd.getException();
			if(!err.isEmpty())
				throw new CommandException(err.toString());
		}		
	}
	
	public void argIn(String key, Object v){
		if(v instanceof ParamTranslator && !key.endsWith("_trigger") ){LOG.warn("ParamTranslator只允许添加给trigger参数");return;}
		if(in_and_out.isEmpty())in_and_out = new HashMap<String, Object>();
		in_and_out.put(key, v);
	}
	public Object argOut(String key){
		if(in_and_out.containsKey(key))return in_and_out.get(key);
		return null;
	}
	
	public String get_wfId() {
		return _wfId;
	}

	public String get_formDateId() {
		return _formDateId;
	}

	public com.econage.eccm.bean.RequestSheetBean getWfBean() {
		return wfBean;
	}

	public com.econage.eccm.bean.TaskBean getTaskBean() {
		return taskBean;
	}

	public Vector getParams_v() {
		return params_v;
	}

	public Vector getValues_v() {
		return values_v;
	}



	private String _wfId;
	private String _formDateId;
	private boolean _init = false;
	private Set<WorkflowActionHandler> hds = Collections.<WorkflowActionHandler>emptySet();
	private Map<String,Object> in_and_out = Collections.<String,Object>emptyMap();
	
	private final Connection conn ;
	
	private com.econage.eccm.bean.RequestSheetBean 	wfBean;
	private com.econage.eccm.bean.TaskBean taskBean;
	private String actName;
	private String actParam;
	private Vector params_v = new Vector(0);
	private Vector values_v = new Vector(0);
}
