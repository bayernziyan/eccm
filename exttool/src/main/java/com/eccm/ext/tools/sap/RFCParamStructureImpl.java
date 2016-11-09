package com.eccm.ext.tools.sap;

import java.util.Iterator;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.eccm.ext.tools.sap.RFCFuncInvokeHandler.RFC_FUNC_PARAM;
import com.eccm.ext.tools.sap.util.SAPDataWrapper;
import com.econage.eccm.ext.util.StringUtil;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class RFCParamStructureImpl implements RFCBaseParam {
	private final static Logger logger = Logger.getLogger(RFCParamStructureImpl.class);
	private final static RFC_FUNC_PARAM PARAM_TYPE = RFC_FUNC_PARAM.STRUCTURE;
	public RFCParamStructureImpl(RFC_FUNC_PARAM type) throws RuntimeException{
		if(type!=PARAM_TYPE)throw new RuntimeException("RFC参数类型错误");
	}
	@Override
	public JSONArray getInputMeta(JCoFunction func) {	
		JSONArray arr = new JSONArray();
		JCoParameterList  stlist =  func.getImportParameterList();
		Iterator it = stlist.iterator();
		for(;it.hasNext();){
			JCoField sf = (JCoField) it.next();
			if(sf.isStructure()){
				JSONObject mobj = new JSONObject();
				System.out.println(PARAM_TYPE+"#########"+sf.getName());
				mobj.put("NAME", sf.getName());
				mobj.put("PARAMS", getStructureTraveral(sf.getStructure()));
				
				arr.add(mobj);
			}
		}
		return arr;
	}

	@Override
	public JSONArray getOutputMeta(JCoFunction func) {
		JSONArray arr = new JSONArray();
		JCoParameterList  stlist =  func.getExportParameterList();
		Iterator it = stlist.iterator();
		for(;it.hasNext();){
			JCoField sf = (JCoField) it.next();
			if(sf.isStructure()){
				JSONObject mobj = new JSONObject();
				mobj.put("NAME", sf.getName());
				mobj.put("PARAMS", getStructureTraveral(sf.getStructure()));
				arr.add(mobj);
			}
		}
		return arr;
	}
	
	private JSONArray getStructureTraveral(JCoStructure js){		
		JSONArray arr = new JSONArray();
		for(int i=0 ;i< js.getMetaData().getFieldCount();i++){	
			JSONObject mobj = new JSONObject();
			mobj.put("NAME", js.getMetaData().getName(i));
			mobj.put("TYPE", js.getMetaData().getTypeAsString(i));
			arr.add(mobj);
		}
		return arr;
	}
	@Override
	public void setInputParams(JCoFunction func, JSONObject pjson) {		
		if(pjson==null)return;
		String value = pjson.getString("FORM_ITEM_VALUE");
		String type =  pjson.getString("TYPE");
		String func_param_name =  pjson.getString("FUNC_PARAM_NAME");
		String structure_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.STRUCTURE.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.STRUCTURE.[a-zA-Z]+.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		func.getImportParameterList().getStructure(structure_name).setValue(param_name, SAPDataWrapper.wrapperData(value, type, null));
	}
	@Override
	public void setOutputParams(JCoFunction func, JSONObject pjson) {
		if(pjson==null)return;
		//String value = pjson.getString("FORM_ITEM_VALUE");
		String type =  pjson.getString("TYPE");
		String func_param_name =  pjson.getString("FUNC_PARAM_NAME");
		String structure_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.STRUCTURE.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.STRUCTURE.[a-zA-Z]+.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
		String value="";
		try{	
			value  = SAPDataWrapper.wrapperData(func.getExportParameterList().getStructure(structure_name).getValue(param_name),java.lang.String.class,null);
		}catch(Exception e){logger.error(" Output "+func_param_name+", saptype:"+type+" error:"+e);}
		pjson.put("FORM_ITEM_VALUE", value);
	}
}
