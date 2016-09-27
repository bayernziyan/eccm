package com.eccm.ext.tools;


import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
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
import com.eccm.ext.tools.workflow.WorkflowAction;
import com.eccm.ext.tools.workflow.WorkflowActionHandler;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemDefSingly;
import com.eccm.ext.tools.workflow.handler.GetFormDataValuesByItemIdSingly;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.HttpUtil;

public class TestUtilTest {
	@Test
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
		System.out.println(new WorkflowActionHandler("12312") {
			
			@Override
			public void doHandler(WorkflowAction action, Connection conn) {
				// TODO Auto-generated method stub
				
			}
		}.getName());
		System.out.println(new GetFormDataValuesByItemIdSingly().getName());
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
