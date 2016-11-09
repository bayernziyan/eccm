package com.eccm.ext.tools.sap;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
/**
 * 
 * @author Administrator
 *
 */
public class RFCProvider {
	private  final static Logger logger  = Logger.getLogger(RFCProvider.class);
	private  String c_sap_connector_id = null; 
	private  String c_sap_funcName = null;
    private  JCoDestination DES_ABAP_AS = null;
    private  JCoFunction function = null;
   	private  RFCFuncInvokeHandler rfc_func_handler = null;
    
    private void getRFCFuncInvokeHandler(){
    	if(function!=null)
    		this.rfc_func_handler = new RFCFuncInvokeHandler();
    	
    }
    public boolean setOutParam(JSONArray outputParamArray ){
    	if(outputParamArray==null&&outputParamArray.size()==0){logger.debug(c_sap_funcName+"@"+c_sap_connector_id+" output param is null.");return true;}
    	try{
    		int i = 0;
	    	for(;i<outputParamArray.size();i++){
	    		JSONObject jobj = (JSONObject)outputParamArray.get(i);
	    		rfc_func_handler.setFuncOutputParams(function, jobj);
	    	}
    	}catch(Exception e){logger.error(e);return false;}
    	return true;
    }
    public boolean setInputParam(JSONArray inputParamArray ){
    	if(inputParamArray==null&&inputParamArray.size()==0){logger.debug(c_sap_funcName+"@"+c_sap_connector_id+" input param is null.");return true;}
    	try{
	    	for(Object obj:inputParamArray){
	    		JSONObject jobj = JSONObject.fromObject(obj);
	    		rfc_func_handler.setFuncInputParams(function, jobj);
	    	}
    	}catch(Exception e){logger.error(e);return false;}
    	return true;
    }
    /**
     * 执行rfc返回 outputParamArray
     * @param inputParamArray
     * @param outputParamArray
     * @return
     * @throws JCoException
     */
    public  boolean execute(JSONArray inputParamArray,JSONArray outputParamArray ) throws JCoException{
    	try{
    	if(setInputParam(inputParamArray )){
    		function.execute(DES_ABAP_AS);
    		return setOutParam(outputParamArray);
    	}
    	}catch(Exception e){e.printStackTrace();logger.error(e);}
    	return false;
    }
    public JSONObject getRFCInputParam(){
    	if(rfc_func_handler==null)return null;
    	JSONObject obj = new JSONObject();
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.NORMAL.toString(), rfc_func_handler.getNormalParamInputMeta(function));
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.STRUCTURE.toString(), rfc_func_handler.getStructureParamInputMeta(function));
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.TABLE.toString(), rfc_func_handler.getTableParamInputMeta(function));
    	return obj;
    }
    
    public JSONObject getRFCOutputParam(){
    	if(rfc_func_handler==null)return null;
    	JSONObject obj = new JSONObject();
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.NORMAL.toString(), rfc_func_handler.getNormalParamOutputMeta(function));
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.STRUCTURE.toString(), rfc_func_handler.getStructureParamOutputMeta(function));
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.TABLE.toString(), rfc_func_handler.getTableParamOutputMeta(function));
    	return obj;
    }
    
    public JSONObject getAllRFCParams(){
    	if(rfc_func_handler==null)return null;
    	JSONObject obj = new JSONObject();
    	JSONObject inputParamsObj = new JSONObject();
    	inputParamsObj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.NORMAL.toString(), rfc_func_handler.getNormalParamInputMeta(function));
    	inputParamsObj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.STRUCTURE.toString(), rfc_func_handler.getStructureParamInputMeta(function));
    	obj.put("InputParams", inputParamsObj);
    	
    	obj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.TABLE.toString(), rfc_func_handler.getTableParamInputMeta(function));
    	
    	JSONObject outputParamsObj = new JSONObject();
    	outputParamsObj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.NORMAL.toString(), rfc_func_handler.getNormalParamOutputMeta(function));
    	outputParamsObj.put(RFCFuncInvokeHandler.RFC_FUNC_PARAM.STRUCTURE.toString(), rfc_func_handler.getStructureParamOutputMeta(function));
    	obj.put("OutputParams", outputParamsObj);    	
    	return obj;
    }
    
    
    /**
     * @param context  json <br>
     * key:SAPSystem   sap系统对象名称 <br>
     * key:SAPFunc	      调用方法名称<br>
     * 
     */
    public  RFCProvider(String connectorId,String funcName)throws RuntimeException{
    	this.c_sap_connector_id = connectorId;
    	this.c_sap_funcName = funcName;
    	
    	initContext();
    	getJCoFunction();
    	getRFCFuncInvokeHandler();
    }
    /**
     * 初始化环境,配置参数系统对象名称  "SAPSystem" 
     * 
     */
    private void initContext()throws RuntimeException{    
    		try {
				DES_ABAP_AS = JCoDestinationManager.getDestination(c_sap_connector_id);
				DES_ABAP_AS.ping();
			} catch (JCoException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}   	
    }
    /**
     *  json 获取调用方法名称SAPFunc
     * 
     */
    private void getJCoFunction() throws RuntimeException{    
    		try {
				function = DES_ABAP_AS.getRepository().getFunction(c_sap_funcName);
			} catch (JCoException e) {
				e.printStackTrace();throw new RuntimeException(e);				
			}
			if (function == null)
				throw new RuntimeException("SAP 方法\""+c_sap_funcName+"\"未找到");
	}
    
}
