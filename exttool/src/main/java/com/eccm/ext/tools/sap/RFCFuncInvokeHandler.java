package com.eccm.ext.tools.sap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.econage.eccm.ext.util.StringUtil;
import com.sap.conn.jco.JCoFunction;

public class RFCFuncInvokeHandler {
	public void setFuncOutputParams(JCoFunction func,JSONObject jobj){
		if(jobj.containsKey("FUNC_PARAM_NAME")){
			String param_name = jobj.getString("FUNC_PARAM_NAME");
			if(StringUtil.isMatchPattern("[a-zA-Z]+.STRUCTURE.[a-zA-Z.]+", param_name)){
				 RFC_FUNC_PARAM.STRUCTURE.getRFCParamImpl().setOutputParams(func, jobj);
			}else if(StringUtil.isMatchPattern("[a-zA-Z]+.NORMAL.[a-zA-Z.]+", param_name)){
				 RFC_FUNC_PARAM.NORMAL.getRFCParamImpl().setOutputParams(func, jobj);
			}
		}else if(jobj.containsKey("DATA_TABLE")){
			 RFC_FUNC_PARAM.TABLE.getRFCParamImpl().setOutputParams(func, jobj);
		}
	}
	
	public void setFuncInputParams(JCoFunction func,JSONObject jobj){
		if(jobj.containsKey("FUNC_PARAM_NAME")){
			String param_name = jobj.getString("FUNC_PARAM_NAME");
			if(StringUtil.isMatchPattern("[a-zA-Z]+.STRUCTURE.[a-zA-Z.]+", param_name)){
				 RFC_FUNC_PARAM.STRUCTURE.getRFCParamImpl().setInputParams(func, jobj);
			}else if(StringUtil.isMatchPattern("[a-zA-Z]+.NORMAL.[a-zA-Z.]+", param_name)){
				 RFC_FUNC_PARAM.NORMAL.getRFCParamImpl().setInputParams(func, jobj);
			}
		}else if(jobj.containsKey("DATA_TABLE")){
			 RFC_FUNC_PARAM.TABLE.getRFCParamImpl().setInputParams(func, jobj);
		}
	}
	public JSONArray getStructureParamInputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.STRUCTURE.getRFCParamImpl().getInputMeta(func);	
	}
	public JSONArray getStructureParamOutputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.STRUCTURE.getRFCParamImpl().getOutputMeta(func);	
	}
	public JSONArray getTableParamInputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.TABLE.getRFCParamImpl().getInputMeta(func);	
	}
	public JSONArray getTableParamOutputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.TABLE.getRFCParamImpl().getOutputMeta(func);	
	}
	public JSONArray getNormalParamInputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.NORMAL.getRFCParamImpl().getInputMeta(func);		
	}
	public JSONArray getNormalParamOutputMeta(JCoFunction func){
		return RFC_FUNC_PARAM.NORMAL.getRFCParamImpl().getOutputMeta(func);		
	}
	public enum RFC_FUNC_PARAM{
		NORMAL,TABLE,STRUCTURE;
		RFCBaseParam getRFCParamImpl(){
			switch(this){
			case NORMAL: return new RFCParamNormalImpl(this);
			case TABLE:	return new RFCParamTableImpl(this);
			case STRUCTURE: return new RFCParamStructureImpl(this);
			default :
				return new RFCParamNormalImpl(this);
			}
		}		
	}//end enum RFC_FUNC_PARAM
	public static void main(String[] args) {
		
		//	System.out.println(RFC_FUNC_PARAM.NORMAL.toString());
		
		
	}
}	
