package com.eccm.ext.tools;


import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpURL;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import com.eccm.ext.tools.constant.CRC32;
import com.eccm.ext.tools.db.DataSourceHandler;
import com.eccm.ext.tools.db.DbEventThread;
import com.eccm.ext.tools.db.ExtDBProvider;
import com.eccm.ext.tools.db.exception.DatabaseRequestException;
import com.eccm.ext.tools.db.pojo.DBConnectionResource;
import com.eccm.ext.tools.util.StringUtil;
import com.eccm.ext.tools.workflow.ActionType;
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemDefMulti;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemDefSingly;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemIdMulti;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemIdSingly;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.HttpUtil;

public class TestUtilTest {
	
	@Test
	public void testWfFormDateMulti(){
		DataSourceHandler db  = null;
		Connection conn = null;
		try {
			 db =  ExtDBProvider.getInstance().getDataSourceHandler("jlxy");
			 conn = db.getConnection();
			 WorkflowAction action = new WorkflowAction(ActionType.WF_ED, null, conn);
			 action.init(ActionType.WF_ED, conn, null, "100658", "57688", "0", "test", "");
			 
			 ArrayList<String> list = new ArrayList<String>();
			/* list.add("36444");
			 list.add("36010");*/
			 list.add("wjbt"); list.add("seq_no");
			 /*action.argIn(GetFormDataValuesByItemIdMulti.param_in_list, list);	
			 
			 action.argIn(GetFormDataValuesByItemIdMulti.param_in_pagesize_int,20);
			 action.argIn(GetFormDataValuesByItemIdMulti.param_in_pagestart_int,0);
			 
			 action.argIn(GetFormDataValuesByItemIdMulti.param_in_whereitem_string,"\"36444\" is not null and \"36010\" is not null");
			 action.addHandler(new GetFormDataValuesByItemIdMulti()).execute();
			 List<HashMap<String, Object>> listmap = (List<HashMap<String, Object>>) action.argOut(GetFormDataValuesByItemIdMulti.param_out_list_map);
			 System.out.println(listmap);*/
			 
			 action.argIn(GetFormDataValuesByItemDefMulti.param_in_list, list);	
			 
			 action.argIn(GetFormDataValuesByItemDefMulti.param_in_pagesize_int,20);
			 action.argIn(GetFormDataValuesByItemDefMulti.param_in_pagestart_int,0);
			 
			 action.argIn(GetFormDataValuesByItemDefMulti.param_in_whereitem_string,"\"wjbt\" is not null and \"seq_no\" is not null");
			 action.addHandler(new GetFormDataValuesByItemDefMulti()).execute();
			 List<HashMap<String, Object>> listmap = (List<HashMap<String, Object>>) action.argOut(GetFormDataValuesByItemDefMulti.param_out_list_map);
			 System.out.println(listmap);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.returnBackConnectionToPool(conn);
		}
		
	}
	//@Test
	public void testWfFormDataSingle(){
		DataSourceHandler db  = null;
		Connection conn = null;
		try {
			 db =  ExtDBProvider.getInstance().getDataSourceHandler("jlxy");
			 conn = db.getConnection();
			 WorkflowAction action = new WorkflowAction(ActionType.WF_ED, null, conn);
			 action.init(ActionType.WF_ED, conn, null, "100658", "57688", "0", "test", "");
			 ArrayList<String> list = new ArrayList<String>();
			// list.add("36444");
			 //list.add("36010");
			 list.add("wjbt"); list.add("seq_no");
			 //wjbt seq_no
			 
			 /* action.argIn(GetFormDataValuesByItemIdSingly.param_in_list, list);			 
			 action.addHandler(new GetFormDataValuesByItemIdSingly()).execute();
			 HashMap<String,String> map = (HashMap<String, String>) action.argOut(GetFormDataValuesByItemIdSingly.param_out_map);
			 System.out.println(map);*/
			 
			 //action.argIn(GetFormDataValuesByItemDefSingly.param_in_list, list);	
			 action.argIn("test1", list);
			 
			 HashMap<String,ArrayList<String>> argRelationShip = new HashMap<String, ArrayList<String>>();
			 ArrayList<String> paramlist = new ArrayList<String>();paramlist.add("test1");
			 argRelationShip.put(GetFormDataValuesByItemDefSingly.param_in_list, paramlist);
			 
			 
			 action.addHandler(new GetFormDataValuesByItemDefSingly(argRelationShip)).execute();
			 HashMap<String,String> map = (HashMap<String, String>) action.argOut(GetFormDataValuesByItemDefSingly.param_out_map);
			 System.out.println(map);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.returnBackConnectionToPool(conn);
		}
		
	}
	
	//@Test
	public void testCRC(){
		String aaa= "{\"username\":\"bill\"}";
		String bbb= "{\"username\":\"econage\"}";
		String ccc = new String("{\"username\":\"bill\"}");
		System.out.println(StringUtil.CRC32(aaa));	
		System.out.println(StringUtil.CRC32(bbb));
		System.out.println(StringUtil.CRC32(ccc));
		
		System.out.println(StringUtil.CRC32("1"));	
		
		System.out.println(StringUtil.MD5(aaa));
		System.out.println(StringUtil.MD5(bbb));
		
	}
	
	public void testJWT() throws UnsupportedEncodingException{
		Key key = MacProvider.generateKey();
	    final String JWT_URL = "";
		
		final String pubkey = "";
		String usermail = "";
		
		Calendar cal =Calendar.getInstance();
		cal.add(Calendar.MINUTE, 10);
		String token = Jwts.builder()
		  .setSubject(usermail).setExpiration(cal.getTime())
		  .setIssuedAt(new Date())
		  .signWith(SignatureAlgorithm.HS256, pubkey)
		  .compact();
		//"localhost:8002/jwt"
		String redirectUrl = "http://" + JWT_URL + "?jwt=" + token;
		HttpResponse response = new HttpRequest().set(redirectUrl).send();
		Map<String,Object[]> re = response.form();
		if(null != re){
			Object[] obj = re.get("return_to");
			if(null != obj && obj.length>0)
				redirectUrl += "&return_to=" + URLEncoder.encode(String.valueOf(obj[0]),"UTF-8");
			else
				redirectUrl = "";
		}else
			redirectUrl = "";
		
	}
	
	public void test1(){
		//ThreadPoolManager.init();
		DataSourceHandler db  = null;
		try {
			 db =  ExtDBProvider.getInstance().getDataSourceHandler("whe8");
			
			 db.executeInsert("insert into ext_test (1,'a','val_a')", true);
			 //System.out.println(Thread.currentThread().getId());
			
			 try {
					Thread.sleep(TimeUnit.DAYS.toDays(10000000));
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			
		}  catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}


}
