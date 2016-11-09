package com.eccm.ext.tools.sap.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;






import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.econage.eccm.ext.util.StringUtil;
import com.econage.eccm.util.StringHelper;





public class DataWrapper {
  private final static Logger logger = Logger.getLogger(DataWrapper.class);
  private final static String thisclsname = DataWrapper.class.getName();
 // private static ConcurrentHashMap<String, String> methodmap = new ConcurrentHashMap<String, String>();
  private static ConcurrentHashMap<String, Method> methodmap = new ConcurrentHashMap<String, Method>();
  private static HashMap<String, String> wpFromMap = new HashMap<String, String>();

  static{	
	  wpFromMap.put("boolean", java.lang.Boolean.class.getName());
	  wpFromMap.put("double",java.lang.Double.class.getName());
	  wpFromMap.put("float",java.lang.Float.class.getName());
	  wpFromMap.put("int",java.lang.Integer.class.getName());
	  wpFromMap.put("long",java.lang.Long.class.getName());	 
  }
  
  public static <T> T wrapperData(Object obj,Class<T> c,String format) {
	 if(obj==null)return null;
	 boolean isformat =   !com.econage.eccm.util.StringHelper.isEmpty(format);
	 String cname = obj.getClass().getName();
	 if(cname.equals(c.getName()))return (T)obj;
	// if(wpFromApiMap!=null&&wpFromApiMap.containsKey(cname)&&wpFromApiMap.get(cname).equals(c.getName()))return (T)obj;
	 if(wpFromMap.containsKey(cname)&&wpFromMap.get(cname).equals(c.getName()))return (T)obj;
	 if(c.isAssignableFrom(obj.getClass()))return (T)obj;
	 else{		
		Method[] bms = DataWrapper.class.getDeclaredMethods();
		String key =cname+"#"+c.getName()+"#"+!StringHelper.isEmpty(format);
		try{
			if(methodmap.containsKey(key)){
				logger.info("[wrapperData] key : "+key+" is hit ");				
				//String cls_mth = methodmap.get(key);
				Method md = methodmap.get(key);
				logger.info( "[wrapperData] hit method:"+md.getName());
				 if(isformat)
					return  (T)md.invoke(md.getDeclaringClass(), obj,format);
				 else
					 return  (T)md.invoke(md.getDeclaringClass(), obj); 
			/*	String hitcls = cls_mth.split("#")[0];
				String hitmth = cls_mth.split("#")[1];
				Class  hitc = Class.forName(hitcls);				
				 if(isformat&&hitcls.equals(thisclsname)){				
					 Method md=	hitc.getDeclaredMethod(hitmth,new Class[]{obj.getClass(),java.lang.String.class});
					 return  (T)md.invoke(hitc, obj,format);
				 }else{
					 Method md=	hitc.getDeclaredMethod(hitmth,new Class[]{obj.getClass()});
					 return  (T)md.invoke(hitc, obj);
				 }*/
			}
			for(int i=0;i<bms.length;i++){			
				bms[i].setAccessible(true);
				boolean anflag = bms[i].isAnnotationPresent(DataTypeSwitchPolicy.class);
				if(anflag){
					DataTypeSwitchPolicy ds = bms[i].getAnnotation(DataTypeSwitchPolicy.class);				
					if(ds.sourceType().getName().equalsIgnoreCase(cname)&&ds.returnType().getName().equalsIgnoreCase(c.getName())&&ds.format()==!StringHelper.isEmpty(format)){
						logger.info( "[wrapperData] hit method:"+thisclsname+"#"+bms[i].getName());
						//methodmap.put(ds.sourceType().getName()+"#"+ds.returnType().getName()+"#"+ds.format(), thisclsname+"#"+bms[i].getName());
						methodmap.put(ds.sourceType().getName()+"#"+ds.returnType().getName()+"#"+ds.format(), bms[i]);
						if(isformat)
						 return (T)bms[i].invoke(DataWrapper.class, obj,format);
						else
						 return (T)bms[i].invoke(DataWrapper.class, obj);
					}
				}
			}
				
		}catch( Exception e){
			e.printStackTrace();
			logger.error("[wrapperData] format: "+format+",  "+cname+" convert to "+c.getName()+".   "+e);
		}
		
		Method m1 = null;
		
		m1= getMatchedMethod(cname,"valueOf", c);
		if(m1==null)
			m1= getMatchedMethod(cname,"parse", c);			
		try{
		if(m1!=null){
			String hit = c.getName()+"#"+m1.getName();
			logger.info( "[wrapperData] hit method:"+hit);
			methodmap.put(key, m1);
			return  (T)m1.invoke(c, obj);}
		}catch(Exception e1){logger.error("[wrapperData] format: "+format+",  "+cname+" convert to "+c.getName()+".   "+e1);}
		
		T t = null;
		try{t = (T)obj;return t;}catch(Exception e){logger.error("[wrapperData] "+cname+" cant convert to "+c.getName()+".");}
		return null;
	 }
  }

 
  public static <T> T wrapperData(Object obj,Class<T> c) {
	  return wrapperData(obj,c,null);
  }
  
  private static Method getMatchedMethod(String cname,String mname,Class c){
	  Method[] m1s= c.getDeclaredMethods();
		for(Method md:m1s){		
			Class[] pc =md.getParameterTypes();		
			if(md.getName().contains(mname)&&pc.length==1&&isParamTypeInArray(cname,pc)){
				return md;
			}
		}
	return null;
  }
  private static boolean isParamTypeInArray(String type,Class[] arr){
	  for(Class nc:arr){
		  String pname = nc.getName();		
		  if(nc.getName().equals(type))
			  return true;		
		  else if(wpFromMap.containsKey(nc.getName())&&wpFromMap.get(nc.getName()).equals(type))
			  return true;
	  }
	  return false;
  }
  
  private final static int BIGDECIMAL_PRECISION=3;
  private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.util.Date.class)
  private static java.util.Date toDate(String val) throws ParseException{
	  return toDate(val,DATE_FORMAT);
  } 
  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.util.Date.class,format=true)
  private static java.util.Date toDate(String val,String format) throws ParseException{
	  if(StringHelper.isEmpty(val))return null;
	  return new SimpleDateFormat(format).parse(val);
  }
  
  
  @DataTypeSwitchPolicy(sourceType=net.sf.json.JSONArray.class,returnType=java.lang.String.class)
  private static java.lang.String toString(net.sf.json.JSONArray val) throws ParseException{
	  if(val==null)return null;
	  return val.toString();
  }
  @DataTypeSwitchPolicy(sourceType=net.sf.json.JSONObject.class,returnType=java.lang.String.class)
  private static java.lang.String toString(net.sf.json.JSONObject val) throws ParseException{
	  if(val==null)return null;
	  return val.toString();
  }
  @DataTypeSwitchPolicy(sourceType=java.util.Date.class,returnType=java.lang.String.class)
  private static java.lang.String toString(java.util.Date val) throws ParseException{
	  if(val==null)return null;
	  return new SimpleDateFormat(DATE_FORMAT).format(val);
  }
  @DataTypeSwitchPolicy(sourceType=java.util.Date.class,returnType=java.lang.String.class,format=true)
  private static java.lang.String toString(java.util.Date val,String format) throws ParseException{
	  if(val==null)return null;
	  return new SimpleDateFormat(format).format(val);
  }
  
  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.math.BigDecimal.class,format=true)
  private static java.math.BigDecimal toBigDecimal(java.lang.String val,String format){
	    BigDecimal b = new BigDecimal(val);   
	    BigDecimal one = new BigDecimal("1"); 
	    int scale = Integer.parseInt(format);
	    if(scale<0){   
          throw new IllegalArgumentException(   
              "The scale must be a positive integer or zero");   
      }  
	  return  b.divide(one,scale,BigDecimal.ROUND_HALF_UP);
  }
  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.math.BigDecimal.class)
  private static java.math.BigDecimal toBigDecimal(java.lang.String val){
	    BigDecimal b = new BigDecimal(val);   
	    BigDecimal one = new BigDecimal("1"); 
	    int scale = BIGDECIMAL_PRECISION;
	    if(scale<0){   
          throw new IllegalArgumentException(   
              "The scale must be a positive integer or zero");   
      }  
	  return  b.divide(one,scale,BigDecimal.ROUND_HALF_UP);
  }
  
  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.lang.Double.class,format=true)
  private static java.lang.Double toDouble(java.lang.String val,String format){				
		return Double.valueOf(new DecimalFormat(format).format(Double.valueOf(val)));
  }
 /*
  *  @DataTypeSwitchPolicy(sourceType=java.lang.String.class,returnType=java.lang.Double.class)
  private static java.lang.Double toDouble(java.lang.String val){				
		return Double.valueOf(val);
  }*/
  public static void main(String[] args) {

		String s =	DataWrapper.wrapperData(new java.util.Date(), java.lang.String.class, "yyyy-MM-dd HH:mm:ss (E)");
		String s1 =	DataWrapper.wrapperData(new java.util.Date(), java.lang.String.class, "yyyy-MM-dd HH:mm:ss (E)");
		System.out.println(s);
		System.out.println("s1:"+s1);
	
		String s2 =	DataWrapper.wrapperData(1, java.lang.String.class);
		System.out.println("s2:"+s2);
		
		Integer i3 =	DataWrapper.wrapperData("1", java.lang.Integer.class);
		System.out.println("i3:"+i3);
		
		String s4 =	DataWrapper.wrapperData(1, java.lang.String.class);
		System.out.println("s4:"+s4);
		
		Integer i5 =	DataWrapper.wrapperData("1", java.lang.Integer.class);
		System.out.println("i5:"+i5);
	  
		Double d6 =DataWrapper.wrapperData("1.222", java.lang.Double.class);
		System.out.println("d6:"+d6);
		
		Double d7 =DataWrapper.wrapperData(1.222, java.lang.Double.class);
		System.out.println("d7:"+d7);
		
		JSONObject j = new JSONObject();
		j.put("sss", "sss");
		System.out.println(DataWrapper.wrapperData(j,java.lang.String.class));
  }
}
