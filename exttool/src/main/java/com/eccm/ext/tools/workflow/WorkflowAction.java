package com.eccm.ext.tools.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.eccm.ext.tools.util.StringUtil;
import com.eccm.ext.tools.workflow.ActionType;
import com.econage.eccm.biz.container.ContainerOperationHelper;
import com.econage.eccm.facade.SessionFacade;
import com.econage.eccm.framework.CommandException;
import com.econage.eccm.oa.form.FormHelper;
import com.econage.eccm.util.CodedValueHelper;

public class WorkflowAction {
	
	public WorkflowAction(ActionType type, HttpServletRequest req,Connection conn){
		_wfId = StringUtil.isBlank(req.getParameter("workflow_id"),req.getParameter("workplace_id"));
		_formDateId = req.getParameter("form_data_id");
		
		if( StringUtil.isBlank(_wfId) )return;
		try{
			switch(type){
				case WF_ST: 
							wfBean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(_wfId));
							actName = wfBean.getPre_cmd();
							actParam = wfBean.getPre_param();break;
				case WF_ED:	wfBean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(_wfId));
							actName = wfBean.getEnd_cmd();
							actParam = wfBean.getEnd_param();break;
		//		case WF_CC: wf_bean = ContainerOperationHelper.getWorkflowById(conn, Integer.parseInt(wf_id));
		//					actName = wf_bean.getCancel_cmd();
		//					actParam = wf_bean.getCancel_param();break;
				case TK_CK1:if("1".equals(req.getParameter("save_flag")))return;
				case TK_CK0:							
							actName = req.getParameter("check_cmd");
							actParam = req.getParameter("check_param");break;
				case TK_ST:	taskBean = SessionFacade.getWorkflowTaskBean(req.getSession());
							actName = taskBean.getPre_cmd();
							actParam = taskBean.getPre_param();break;						
				case TK_ED: taskBean = SessionFacade.getWorkflowTaskBean(req.getSession());
							actName = taskBean.getPost_cmd();
							actParam = taskBean.getPost_param();break;
			}
			
			params_v = CodedValueHelper.getFormItemColumnWithPrefix(actParam);
	        values_v = FormHelper.getFormItemValueWithPrefix2(conn,String.valueOf(_formDateId), params_v);
		}catch(CommandException e ){e.printStackTrace();}
		
		if(!StringUtil.isBlank(actName)) _init = true;
	}
	
	public WorkflowAction addHandler(WorkflowActionHandler handler){
		if(null == handler)return this;
		hds.add(handler);
		return this;
	}
	
	public void execute() throws CommandException {
		if(!_init)return;
		if(hds.isEmpty())return;
        
		for(WorkflowActionHandler hd : hds){
			hd.doHandler(this);
			ArrayList<String> err = hd.getException();
			if(!err.isEmpty())
				throw new CommandException(err.toString());
		}		
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
	private HashSet<WorkflowActionHandler> hds = (HashSet<WorkflowActionHandler>) Collections.<WorkflowActionHandler>emptySet();
	
	private com.econage.eccm.bean.RequestSheetBean 	wfBean;
	private com.econage.eccm.bean.TaskBean taskBean;
	private String actName;
	private String actParam;
	private Vector params_v = new Vector(0);
	private Vector values_v = new Vector(0);
}
