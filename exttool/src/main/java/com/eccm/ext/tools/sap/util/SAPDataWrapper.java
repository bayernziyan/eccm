package com.eccm.ext.tools.sap.util;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class SAPDataWrapper  {
	private final static Logger logger = Logger.getLogger(SAPDataWrapper.class);
	private static HashMap<String,String> typemap = new HashMap<String, String>();
	static{
		typemap.put("INT1", java.lang.Integer.class.getName());
		typemap.put("INT2", java.lang.Integer.class.getName());
		typemap.put("INT",  java.lang.Integer.class.getName());
		typemap.put("CHAR", java.lang.String.class.getName());
		typemap.put("NUM", java.lang.String.class.getName());
		typemap.put("BCD", java.math.BigDecimal.class.getName());
		typemap.put("DATE", java.util.Date.class.getName());
		typemap.put("TIME", java.util.Date.class.getName());
		typemap.put("FLOAT", java.lang.Double.class.getName());
		typemap.put("BYTE", byte[].class.getName());
		typemap.put("STRING", java.lang.String.class.getName());
		typemap.put("XSTRING",  byte[].class.getName());
	}
	  public static <T> T wrapperData(Object obj,Class<T> c ,String format) {
		  T t = null;
		try{
			 t= (T) DataWrapper.wrapperData(obj, c, format);
		 }catch(Exception e){logger.error(e);}finally{
			 return t;
		 }
	  }
	  public static <T> T wrapperData(Object obj,String inc,String format) {
		  Class wpcls = null;
		  try{
		  if(typemap.containsKey(inc))wpcls = Class.forName(typemap.get(inc));
		  if(wpcls ==null)
			 wpcls = Class.forName(inc);
		  }catch(ClassNotFoundException e){logger.error(e);}
		  if(wpcls==null)return null;
		 T t = null;
		 try{
		 t= (T) DataWrapper.wrapperData(obj, wpcls, format);
		 }catch(Exception e){logger.error(e);}
		 if(t==null)
			 logger.info("[wrapperData] cant convert object from "+inc+" to "+obj.getClass().getName()+".");
		 return t;
	  }
	  public static void main(String[] args) {
		Object obj =  wrapperData("2015-09-11 22:34:22","DATE","yyyy-MM-dd HH:mm");
		System.out.println(obj.getClass().getName());
		System.out.println(obj.toString());
		String s =  wrapperData(obj,java.lang.String.class,null);
		System.out.println(s);
	}
}
