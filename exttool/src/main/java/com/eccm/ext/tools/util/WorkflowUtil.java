package com.eccm.ext.tools.util;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.eccm.ext.tools.workflow.ActionType;
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;
import com.eccm.jooq.newsoft.tables.EclRequestSheet;
import com.econage.eccm.framework.CommandException;

public class WorkflowUtil {
	
	public Result<Record> getWorkflowById(Connection conn, String wfId){
		DSLContext ds = DSL.using(conn,SQLDialect.ORACLE);
		Result<Record> re = ds.select(EclRequestSheet.ECL_REQUEST_SHEET.fields()).from(EclRequestSheet.ECL_REQUEST_SHEET)
				.where(EclRequestSheet.ECL_REQUEST_SHEET.REQUEST_ID.eq(wfId)).fetch();
		return re;
	}
	
	public void test(Connection conn) throws CommandException{
		
		new WorkflowAction(ActionType.TK_CK0, null,conn ).addHandler(new WorkflowActionHandler() {
			
			@Override
			public void doHandler(WorkflowAction action) {
				// TODO Auto-generated method stub
				
			}
		}).execute();
	}
}
