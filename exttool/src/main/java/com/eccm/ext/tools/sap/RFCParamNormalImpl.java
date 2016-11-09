package com.eccm.ext.tools.sap;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.eccm.ext.tools.sap.RFCFuncInvokeHandler.RFC_FUNC_PARAM;
import com.eccm.ext.tools.sap.util.SAPDataWrapper;
import com.econage.eccm.ext.util.StringUtil;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class RFCParamNormalImpl implements RFCBaseParam {
	private final static Logger logger = Logger.getLogger(RFCParamNormalImpl.class);
	private final static RFC_FUNC_PARAM PARAM_TYPE = RFC_FUNC_PARAM.NORMAL;
	public RFCParamNormalImpl(RFC_FUNC_PARAM type) throws RuntimeException{
		if(type!=PARAM_TYPE)throw new RuntimeException("RFC参数类型错误");
	}
	
	
	
	@Override
	public JSONArray getInputMeta(JCoFunction func) {
		JCoParameterList ilist =  func.getImportParameterList();		
		JSONArray arr = new JSONArray();
		if(ilist!=null){			
			for(int i=0 ;i<ilist.getMetaData().getFieldCount();i++){
				JSONObject mobj = new JSONObject();
				mobj.put("NAME", ilist.getMetaData().getName(i));
				mobj.put("TYPE", ilist.getMetaData().getTypeAsString(i));
				if(ilist.getMetaData().getTypeAsString(i).equals("STRUCTURE")) continue;
				
				arr.add(mobj);
			}			
		}else
			logger.debug(func.getName() + " meta data import parameter list is null");	
		return arr;
	}

	@Override
	public JSONArray getOutputMeta(JCoFunction func) { 
		JCoParameterList olist =  func.getExportParameterList();		
		JSONArray arr = new JSONArray();
		if(olist!=null){			
			for(int i=0 ;i<olist.getMetaData().getFieldCount();i++){
				JSONObject mobj = new JSONObject();
				mobj.put("NAME", olist.getMetaData().getName(i));
				mobj.put("TYPE", olist.getMetaData().getTypeAsString(i));
				if(olist.getMetaData().getTypeAsString(i).equals("STRUCTURE")) continue;
				arr.add(mobj);
			}			
		}else
			logger.debug(func.getName() + " meta data export parameter list is null");		
		return arr;
	}


	/**
	 * param2.put("FORM_ITEM_ID", "k9875");
		param2.put("FORM_ITEM_VALUE", "111");
		param2.put("FUNC_PARAM_NAME", "InputParams.STRUCTURE.DOCUMENTHEADER.ZLCBH");
	 */
	@Override
	public void setInputParams(JCoFunction func,JSONObject pjson) {
		if(pjson==null)return;
		String value = pjson.getString("FORM_ITEM_VALUE");
		String type =  pjson.getString("TYPE");
		String func_param_name =  pjson.getString("FUNC_PARAM_NAME");
		String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.NORMAL.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		func.getImportParameterList().setValue(param_name, SAPDataWrapper.wrapperData(value, type, null));
	}



	@Override
	public void setOutputParams(JCoFunction func, JSONObject pjson) {
		if(pjson==null)return;
		String type =  pjson.getString("TYPE");
		String func_param_name =  pjson.getString("FUNC_PARAM_NAME");
		String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.NORMAL.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		String value="";
		try{	
		value  = SAPDataWrapper.wrapperData(func.getExportParameterList().getValue(param_name),java.lang.String.class,null);
		}catch(Exception e){logger.error(" Output "+func_param_name+", saptype:"+type+" error:"+e);}
		pjson.put("FORM_ITEM_VALUE", value);
	}

}
