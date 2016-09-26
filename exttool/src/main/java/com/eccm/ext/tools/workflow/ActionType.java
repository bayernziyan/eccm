package com.eccm.ext.tools.workflow;

public enum ActionType {
	/**
	 * 流程发起
	 */
	WF_ST("0"),
	/**
	 * 流程结束
	 */
	WF_ED("0"),
	/**
	 * 流程取消
	 */
	WF_CC("0"),
	/**
	 * 环节check
	 */
	TK_CK0("0"),
	TK_CK1("1"),
	/**
	 * 环节开始
	 */
	TK_ST("0"),
	/**
	 * 环节结束
	 */
	TK_ED("0")
	;
	private String saveFlag;
	
	ActionType(String saveFlag){
			this.saveFlag = saveFlag; 
	}
}
