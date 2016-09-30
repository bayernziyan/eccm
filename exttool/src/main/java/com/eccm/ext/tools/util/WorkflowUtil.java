package com.eccm.ext.tools.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.eccm.jooq.newsoft.tables.EclRequestFormItem;
import com.eccm.jooq.newsoft.tables.EclRequestFormItemData;
import com.eccm.jooq.newsoft.tables.EclRequestSheet;

public class WorkflowUtil {

	public Result<Record> getWorkflowById(Connection conn, String wfId) {
		DSLContext ds = DSL.using(conn, SQLDialect.ORACLE);
		Result<Record> re = ds.select(EclRequestSheet.ECL_REQUEST_SHEET.fields())
				.from(EclRequestSheet.ECL_REQUEST_SHEET).where(EclRequestSheet.ECL_REQUEST_SHEET.REQUEST_ID.eq(wfId))
				.fetch();
		return re;
	}

	public static HashMap<String, Record> getMapFromItemId2ItemDefByItemDef(Connection conn, final List<String> list,
			int formId) {
		// select id,def_field_id from ecl_request_form_item where form_id=13844
		// and def_field_id in ('');
		DSLContext ds = DSL.using(conn, SQLDialect.ORACLE);
		EclRequestFormItem tb = EclRequestFormItem.ECL_REQUEST_FORM_ITEM;
		Result<Record> re = ds.select(tb.fields()).from(tb).where(tb.FORM_ID.eq(formId)).and(tb.DEF_FIELD_ID.in(list.toArray()))
				.fetch();
		if (!re.isEmpty()) {
			HashMap<String, Record> map = new HashMap<String, Record>();
			for (Record c : re) {
				String itemId = String.valueOf(c.getValue(tb.ID));
				map.put(itemId, c);
			}
			return map;
		}
		return null;
	}
	
	/**
	 * 获取符合条件的所有流程对应的表单数据，可分页
	 * @param conn
	 * @param list
	 * @param templateId
	 * @param where_itemv
	 * @param where_wf
	 * @param pagestart 记录的开始
	 * @param pagesize  page
	 * @return
	 */
	public static List<HashMap<String,Object>> getFormDataValueListByItemIdList(Connection conn,final List<String> list,int templateId,String where_itemv,String where_wf,int pagestart,int pagesize){
		DSLContext ds = DSL.using(conn,SQLDialect.ORACLE);
		Field[] f = new Field[list.size()+1];
		int itmiind = 0;
		for(String itemid : list){
			f[itmiind++] = EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.ITEM_ID.decode(itemid,EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.VALUE,"").max().as(itemid);
		}
		f[itmiind++] = EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID;
		
		SelectConditionStep  wfc = ds.select(EclRequestSheet.ECL_REQUEST_SHEET.FORM_DATA_ID).from(EclRequestSheet.ECL_REQUEST_SHEET)
		.where("1=1").and(EclRequestSheet.ECL_REQUEST_SHEET.TEMPLATE_ID.eq(templateId));
		if(!StringUtil.isBlank(where_wf))
			wfc.and(where_wf);
		
		Table nested =
				ds.select(f).from(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA).where(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID.in(
						wfc
						)).groupBy(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID).asTable("nested");
		
	
		SelectConditionStep fdc = 	ds.select(nested.fields()).from(nested).where("1=1");
		if(!StringUtil.isBlank(where_itemv))
			fdc.and(where_itemv);
		fdc.orderBy(nested.field("FORM_DATA_ID").desc());
		System.out.println(fdc.limit(pagestart, pagesize).getSQL());
		List<HashMap<String, Object>> re = fdc.limit(pagestart, pagesize).fetchMaps();
		return re;
	}
	
	
	/**
	 * 获取符合条件的所有流程对应的表单数据，可分页
	 * @param conn
	 * @param list
	 * @param templateId
	 * @param where_itemv
	 * @param where_wf
	 * @param pagestart 记录的开始
	 * @param pagesize  page
	 * @return
	 */
	public static List<HashMap<String,Object>> getFormDataValueListByItemDefList(Connection conn,final List<String> list,int templateId,int formId,String where_itemv,String where_wf,int pagestart,int pagesize){
		if (null == list || list.isEmpty())
			return null;
		HashMap<String, Record> id2def = getMapFromItemId2ItemDefByItemDef(conn, list, formId);
		if (null == id2def)
			return null;
		Iterator<String> it = id2def.keySet().iterator();
		ArrayList<String> itemIds = new ArrayList<String>();
		while(it.hasNext()){
			itemIds.add(it.next());
		}
		
		DSLContext ds = DSL.using(conn,SQLDialect.ORACLE);
		EclRequestFormItem tb = EclRequestFormItem.ECL_REQUEST_FORM_ITEM;
		Field[] f = new Field[itemIds.size()+1];
		int itmiind = 0;
		for(String itemid : itemIds){
			f[itmiind++] = EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.ITEM_ID.decode(itemid,EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.VALUE,"").max().as(id2def.get(itemid).getValue(tb.DEF_FIELD_ID).toString());
		}
		f[itmiind++] = EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID;
		
		SelectConditionStep  wfc = ds.select(EclRequestSheet.ECL_REQUEST_SHEET.FORM_DATA_ID).from(EclRequestSheet.ECL_REQUEST_SHEET)
		.where("1=1").and(EclRequestSheet.ECL_REQUEST_SHEET.TEMPLATE_ID.eq(templateId));
		if(!StringUtil.isBlank(where_wf))
			wfc.and(where_wf);
		
		Table nested =
				ds.select(f).from(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA).where(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID.in(
						wfc
						)).groupBy(EclRequestFormItemData.ECL_REQUEST_FORM_ITEM_DATA.FORM_DATA_ID).asTable("nested");
		
	
		SelectConditionStep fdc = 	ds.select(nested.fields()).from(nested).where("1=1");
		if(!StringUtil.isBlank(where_itemv))
			fdc.and(where_itemv);
		fdc.orderBy(nested.field("FORM_DATA_ID").desc());
		System.out.println(fdc.limit(pagestart, pagesize).getSQL());
		List<HashMap<String, Object>> re = fdc.limit(pagestart, pagesize).fetchMaps();
		return re;
	}
	
	/**
	 * 通过ItemDef自定义标识
	 * 获取单条form_data_id的表单数据
	 * @param conn
	 * @param list
	 * @param formDataId
	 * @param formId
	 * @return
	 */
	public static HashMap<String, String> getFormDataValueByItemDefList(Connection conn, final List<String> list,
			int formDataId, int formId) {
		if (null == list || list.isEmpty())
			return null;
		HashMap<String, Record> id2def = getMapFromItemId2ItemDefByItemDef(conn, list, formId);
		if (null == id2def)
			return null;
		Iterator<String> it = id2def.keySet().iterator();
		ArrayList<String> itemIds = new ArrayList<String>();
		while(it.hasNext()){
			itemIds.add(it.next());
		}
		HashMap<String ,String> fdvalues = getFormDataValueByItemIdList(conn, itemIds, formDataId);
		if( null == fdvalues ) return null;
		HashMap<String ,String > map = new HashMap<String, String>();
		Iterator<String> it1 = fdvalues.keySet().iterator();
		EclRequestFormItem tb = EclRequestFormItem.ECL_REQUEST_FORM_ITEM;
		while(it1.hasNext()){
			String itemid = it1.next();
			Record c = id2def.get(itemid);
			if(null == c)continue;
			String def  = String.valueOf(c.getValue(tb.DEF_FIELD_ID));
			String val = fdvalues.get(itemid);
			map.put(def, val);
		}
		return map;
	}

	public static HashMap<String, String> getFormDataValueByItemIdList(Connection conn, final List<String> list,
			final int formDataId) {
		if (null == list || list.isEmpty())
			return null;
		String sql = "select ";
		sql += DBUtil.columnStr("max(decode(item_id,", ",value)) as \"[this]\"", list, ",");
		sql += " from ecl_request_form_item_data where form_data_id=? and item_id in (";
		sql += DBUtil.columnStr("", "", list, ",") + ")";
		try {
			return new QueryRunner().query(conn, sql, new ResultSetHandler<HashMap<String, String>>() {

				@Override
				public HashMap<String, String> handle(ResultSet rs) throws SQLException {
					HashMap<String, String> map = new HashMap<String, String>();
					if (rs.next()) {
						for (String itemid : list) {
							map.put(itemid, rs.getString(itemid));
						}
						map.put("formDataId", String.valueOf(formDataId));
					}
					return map;
				}

			}, formDataId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
