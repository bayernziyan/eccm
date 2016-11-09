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
import com.sap.conn.jco.JCoTable;

public class RFCParamTableImpl implements RFCBaseParam {
	private final static Logger logger = Logger.getLogger(RFCParamTableImpl.class);
	private final static RFC_FUNC_PARAM PARAM_TYPE = RFC_FUNC_PARAM.TABLE;
	public RFCParamTableImpl(RFC_FUNC_PARAM type) throws RuntimeException{
		if(type!=PARAM_TYPE)throw new RuntimeException("RFC参数类型错误");
	}
	@Override
	public JSONArray getInputMeta(JCoFunction func) {		
		JSONArray arr = new JSONArray();
		JCoParameterList tblist =  func.getTableParameterList();
		Iterator it = tblist.iterator();
		for(;it.hasNext();){
			JCoField pf =  (JCoField) it.next();
			if(pf.isTable()){
				JSONObject mobj = new JSONObject();
				//mobj.put(pf.getName(),getTableTraversal(pf.getTable()));
				mobj.put("NAME", pf.getName());
				mobj.put("PARAMS", getTableTraversal(pf.getTable()));
				arr.add(mobj);
			}
		}		
		return arr;
	}

	@Override
	public JSONArray getOutputMeta(JCoFunction func) {	
		JSONArray arr = new JSONArray();
		JCoParameterList tblist =  func.getTableParameterList();
		Iterator it = tblist.iterator();
		for(;it.hasNext();){
			JCoField pf =  (JCoField) it.next();
			if(pf.isTable()){
				JSONObject mobj = new JSONObject();
				//mobj.put(pf.getName(),getTableTraversal(pf.getTable()));
				mobj.put("NAME", pf.getName());
				mobj.put("PARAMS", getTableTraversal(pf.getTable()));
				arr.add(mobj);
			}
		}		
		return arr;
	}
	private JSONArray getTableTraversal(JCoTable ot){		
		 JSONArray arr = new JSONArray();
		 for(int i=0 ;i< ot.getMetaData().getFieldCount();i++){
			 JSONObject mobj = new JSONObject();
			 mobj.put("NAME", ot.getMetaData().getName(i));
			 mobj.put("TYPE", ot.getMetaData().getTypeAsString(i));
			 arr.add(mobj);
	     }		
		 return arr;
	}
	@Override
	public void setInputParams(JCoFunction func, JSONObject pjson) {
		if(pjson==null)return;
		JSONArray tablerowarr = pjson.getJSONArray("DATA_TABLE");
		if(tablerowarr!=null&&tablerowarr.size()>0){
			JCoParameterList tblist = func.getTableParameterList();
			JCoTable jtable = null;
			for(Object obj:tablerowarr){
				JSONArray colarr = JSONArray.fromObject(obj);
				if(colarr==null||colarr.size()==0)continue;
				if(jtable!=null){jtable.appendRow();jtable.nextRow();}
				for(Object colobj:colarr){
					JSONObject coljobj = JSONObject.fromObject(colobj);
					String value = coljobj.getString("FORM_ITEM_VALUE");
					String type =  coljobj.getString("TYPE");
					String func_param_name =  coljobj.getString("FUNC_PARAM_NAME");
					String table_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.TABLE.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
					String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.TABLE.[a-zA-Z]+.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
					if(jtable==null){jtable = tblist.getTable(table_name);
					jtable.appendRow();jtable.nextRow();
					}
					jtable.setValue(param_name, SAPDataWrapper.wrapperData(value, type, null));
				}
			}
		}
		
	}
	@Override
	public void setOutputParams(JCoFunction func, JSONObject pjson) {
		if(pjson==null)return;
		JSONArray tablerowarr = pjson.getJSONArray("DATA_TABLE");
		if(tablerowarr!=null&&tablerowarr.size()>0){
			JCoParameterList tblist = func.getTableParameterList();
			JCoTable jtable = null;
			int row=0;
			int i=0,j=0;
			for(;i<tablerowarr.size();i++){
				JSONArray colarr = (JSONArray)tablerowarr.get(i);
				if(colarr==null||colarr.size()==0)continue;
				//if(jtable!=null){jtable.setRow(++row);				
				for(j=0;j<colarr.size();j++){
					JSONObject coljobj = (JSONObject)colarr.get(j);
					//String value = coljobj.getString("FORM_ITEM_VALUE");
					String type =  coljobj.getString("TYPE");
					String func_param_name =  coljobj.getString("FUNC_PARAM_NAME");
					String table_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.TABLE.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
					String param_name = StringUtil.fatchMatchPattern("[a-zA-Z]+.TABLE.[a-zA-Z]+.([a-zA-Z]+)(.[a-zA-Z.]+)*", func_param_name, 1);
					if(jtable==null){jtable = tblist.getTable(table_name);					
					}
					jtable.setRow(row++);					
					String value="";
					try{	
						value  = SAPDataWrapper.wrapperData(jtable.getValue(param_name),java.lang.String.class,null);
					}catch(Exception e){logger.error(" Output "+func_param_name+", saptype:"+type+" error:"+e);}
					coljobj.put("FORM_ITEM_VALUE", value);
				}
			}
		}
		
	}
	
}
