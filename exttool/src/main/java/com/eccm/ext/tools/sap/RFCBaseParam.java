package com.eccm.ext.tools.sap;

import com.sap.conn.jco.JCoFunction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface RFCBaseParam {
	public JSONArray getInputMeta(JCoFunction func);
	public JSONArray getOutputMeta(JCoFunction func);
	public void setInputParams(JCoFunction func,JSONObject pjson);
	public void setOutputParams(JCoFunction func,JSONObject pjson);
}
