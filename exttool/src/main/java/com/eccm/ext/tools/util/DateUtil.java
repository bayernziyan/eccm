package com.eccm.ext.tools.util;

import static com.eccm.ext.tools.constant.EmptyObjectConstant.*;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

import com.eccm.ext.tools.date.DateFormat;
import com.eccm.ext.tools.date.DateSymbol;
import com.eccm.ext.tools.db.DataSourceHandler;
import com.eccm.ext.tools.db.ExtDBProvider;
import com.eccm.ext.tools.db.exception.DatabaseRequestException;
import com.eccm.ext.tools.db.pojo.DBConnectionResource;




public class DateUtil {
	private static final Logger logger = Logger.getLogger(DateUtil.class);
	
	
	public static Calendar  getCalendar(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		cal.setTime(date);
		return cal;
	}
	
	public static String convertDate2String( Date date, DateFormat dateFormat ,DateSymbol dateSymbol) {
		if ( null == date )
			return EMPTY_STRING;
		long seconds = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat.getDateFormat(dateSymbol) );
		Date dt = new Date( seconds );
		String dateString = sdf.format( dt );
		return dateString;
	}
	
	public static String convertDate2String( Date date, String formatString) {
		if ( null == date )
			return EMPTY_STRING;
		long seconds = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(formatString);
		Date dt = new Date( seconds );
		String dateString = sdf.format( dt );
		return dateString;
	}
	
	public static String convertDate2String(Date date, DateFormat dateFormat){
		return convertDate2String(date,dateFormat,null);
	}
	
	/**
	 * long转换为指定格式日期字符串<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertLong2String( Long longFormatDateTime, DateFormat dateFormat ,DateSymbol dateSymbol){
		if( null == dateSymbol  ) dateSymbol = new DateSymbol();
		String dateFormatString = dateFormat.getDateFormat(dateSymbol);
		if( "T".equals(dateSymbol.getDateTimeSymbol()) || "Z".equals(dateSymbol.getEndSymbol())){
			dateFormatString = dateFormatString.replace("T", "+").replace("Z", "#");
		}
			  
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormatString );
		Date dt = new Date( longFormatDateTime );
		String dateString = sdf.format( dt );
		if(  "T".equals(dateSymbol.getDateTimeSymbol()) || "Z".equals(dateSymbol.getEndSymbol()) ){
			dateString = dateString.replace( "+", "T" ).replace( "#", "Z" );
		}
		return dateString;
	} 
	
	public static String convertLong2String( Long longFormatDateTime, DateFormat dateFormat ){
		return convertLong2String(longFormatDateTime,dateFormat,null);
	}
	
	
	public static DateSymbol fatchDateSymbol(String dateString){
		if(StringUtil.isBlank(dateString))throw new NullPointerException("日期为空");
		DateSymbol ds = new DateSymbol();
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);
		
		return fatchDateSymbol(dateString,struct);
	}
	private static DateSymbol fatchDateSymbol(String dateString,LinkedHashMap<Integer, String> struct){
		if(StringUtil.isBlank(dateString))throw new NullPointerException("日期为空");
		DateSymbol ds = new DateSymbol();
		if(null == struct || struct.isEmpty())throw new IllegalArgumentException("日期格式不存在"+dateString);
		
		Iterator<Integer> it = struct.keySet().iterator();
		int ind = 0;String pric = "";
		while(it.hasNext()){
			int n = it.next();
			if(n==-1)break;
			String c = struct.get(n);
			if(pric.equals(c))continue;
			switch(ind){
				case 0 : ds.setYmdSymbol(c);
				case 1 : ds.setDateTimeSymbol(c);
				case 2 : ds.setHmisSymbol(c);
				case 3 : ds.setEndSymbol(c);
			}
			ind++;
			pric = c;
		}
		return ds;
	}
	
	private static int[]  getDateFormatPoints(String dateString){
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);
		int[] points = new int[]{}; 
		if ( null == struct || struct.isEmpty()) return points;
		points = getDateFormatPoints(struct);
		return points;
	}
	
	private static int[]  getDateFormatPoints(LinkedHashMap<Integer, String> struct){
		int[] points = new int[struct.size()];
		points[0] = Integer.parseInt(struct.get(-1));
		Iterator<Integer> it = struct.keySet().iterator();
		int ind = 1;
		while(it.hasNext()){
			int nt = 0;
			if(( nt = it.next())!=-1)
				points[ind++] = nt;
			else
				break;
		}
		return points;
	}
	
	public static DateFormat fatchDateFormat(String dateString) throws IllegalArgumentException {
		if(StringUtil.isBlank(dateString))throw new NullPointerException("日期为空");
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);
		return fatchDateFormat(dateString,struct);
	}	
	private static DateFormat fatchDateFormat(String dateString,LinkedHashMap<Integer, String> struct) throws IllegalArgumentException{
		if(StringUtil.isBlank(dateString))throw new NullPointerException("日期为空");
		if(null == struct || struct.isEmpty())throw new IllegalArgumentException("日期格式不存在"+dateString);
		
		int len = Integer.parseInt(struct.get(-1));
		
		for(DateFormat df : DateFormat.values()){
			if(checkDateFormatStruct(struct, df) && len == df.getStructPoint()[0])
				return df;
		}
		int[] points = new int[struct.size()];
		points[0] = Integer.parseInt(struct.get(-1));
		Iterator<Integer> it = struct.keySet().iterator();
		int ind = 1;
		while(it.hasNext()){
			int nt = 0;
			if(( nt = it.next())!=-1)
				points[ind++] = nt;
			else
				break;
		}		
		throw new  IllegalArgumentException("日期格式不存在,"+dateString+"");		
	}
	
	
	
	private static boolean checkDateFormatStruct(HashMap<Integer, String> struct,DateFormat dateFormat){
		if( null == struct || struct.isEmpty() )return false;
		int points[] = dateFormat.getStructPoint();
		if(points.length <= 1)return false;
		for(int i=1;i<points.length ;i++){
			if(!struct.containsKey(points[i]))return false;
		}
		return true;
	}
	
	public static Date convertString2Date(String dateString) throws ParseException{
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);		
		return convertString2Date(dateString,struct);
	}
	
	public static Date convertString2Date(String dateString,DateFormat dateFormat) throws ParseException{
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);		
		Date date =  convertString2Date(dateString,struct);
		String formatDateString = convertDate2String(date, dateFormat);
		return convertString2Date(formatDateString);
	}
	
	private static Date convertString2Date(String dateString,LinkedHashMap<Integer, String> struct) throws ParseException{
		DateFormat dateFormat = null;
		
		try{dateFormat	= fatchDateFormat(dateString,struct);}catch(IllegalArgumentException e){
			logger.warn(e.getMessage());
		}
		DateSymbol dateSymbol = fatchDateSymbol(dateString,struct);
		String dateFormatString  = "";
		if( null != dateFormat)
			dateFormatString =  dateFormat.getDateFormat(dateSymbol);
		else
			dateFormatString = DateFormat.Default.getDateFormat(getDateFormatPoints(struct), dateSymbol);
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormatString );
		return sdf.parse(dateString);
	}
	
	
		
	public static LinkedHashMap<Integer ,String> getDateStringStruct(String dateString){
		if(StringUtil.isBlank(dateString))return null;
		char[] carr =dateString.toCharArray();
		LinkedHashMap<Integer ,String> map = new LinkedHashMap<Integer ,String>();
		int ind = 0;
		for(char c : carr){
			if(!StringUtil.isIntChar(c)){
				String cstr = String.valueOf(c);			
				map.put(ind, cstr);
			}
			ind++;
		}
		map.put(Integer.valueOf(-1), String.valueOf(dateString.length()));
		return map;
	}
	
	/**
	 * 日期 转换为指定格式
	 * @param dateString
	 * @param dateFormat
	 * @return
	 */
	public static String convertString2FormatString(String dateString,DateFormat dateFormat){
		DateFormat _dateFormat = null;
		LinkedHashMap<Integer, String> struct = getDateStringStruct(dateString);
		try{_dateFormat = fatchDateFormat(dateString,struct);}catch(IllegalArgumentException e){
			logger.warn(e.getMessage());
		}	
		if(_dateFormat == dateFormat)return dateString;		
		try {			
			if( null != _dateFormat){
				Date date = convertString2Date(dateString,struct);
				String newDateString = convertDate2String(date, dateFormat);
				return newDateString;
			} else {
				
				DateSymbol dateSymbol = new DateSymbol();
				int[] points = getDateFormatPoints(struct);
				String formatString = DateFormat.Default.getDateFormat(points, dateSymbol);
				Date newDate = convertString2Date(dateString,struct);
				return convertDate2String(newDate, dateFormat.getDateFormat(dateSymbol));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*int[] df_points = dateFormat.getStructPoint();
		if(_dateFormat.getStructPoint()[0] > df_points[0]){
			return dateString.substring(0,df_points[0]);
		}else{
			if(_dateFormat == DateFormat.YMD){
				int coffset = df_points[0] - _dateFormat.getStructPoint()[0];
				switch(coffset){
				case 9:return dateString+=" 00:00:00";
				case 6:return dateString+=" 00:00";
				}
			}else if(_dateFormat == DateFormat.YMD_HMI){
				return dateString+":00";
			}
		}*/
		return EMPTY_STRING;
	}
	
	/**
	 * 2016-09-13  转换为 to_date('2016-09-13','yyyy-MM-dd')
	 * 2016-09-13 转换为 to_date('2016-09-13 13:20','yyyy-MM-dd hh24:mi');
	 * @param dateString
	 * @param dateFormat
	 * @return
	 */
	public static String convertString2OracleToDate(String dateString,DateFormat dateFormat){
		try{
			String newDateString = convertString2FormatString(dateString, dateFormat);
			Date newDate = convertString2Date(newDateString);
			String formatDateString = convertDate2String(newDate,DateFormat.YMD_HMIS);
			if(!StringUtil.isBlank(formatDateString)){
				DateSymbol dateSymbol = fatchDateSymbol(formatDateString);	
				return "to_date('"+formatDateString+"','"+DateFormat.YMD_HMIS.getOraleDateFormat(dateSymbol)+"')";
			}

		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public static String convertDate2OracleToDate(Date date,DateFormat dateFormat){
		try{
			DateSymbol dateSymbol = new DateSymbol();
			String newDateString  = convertDate2String(date, dateFormat);		
			if(!StringUtil.isBlank(newDateString))
				return "to_date('"+newDateString+"','"+dateFormat.getOraleDateFormat(dateSymbol)+"')";
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public static String convertString2SqlserverDate(String dateString,DateFormat dateFormat){
		try{
			String newDateString = convertString2FormatString(dateString, dateFormat);
			Date newDate = convertString2Date(newDateString);
			String formatDateString = convertDate2String(newDate,DateFormat.YMD_HMIS);
			if(!StringUtil.isBlank(formatDateString)){
				//120 CONVERT(datetime,'11/1/2003',101)
				return "CONVERT(datetime,'"+formatDateString+"',120)";
			}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	public static String convertDate2SqlserverDate(Date date,DateFormat dateFormat){
		try{
			DateSymbol dateSymbol = new DateSymbol();
			String newDateString  = convertDate2String(date, dateFormat);	
			String formatDateString = dateFormat == DateFormat.YMD_HMIS?newDateString:convertString2FormatString(newDateString, DateFormat.YMD_HMIS);
			if(!StringUtil.isBlank(formatDateString)){
				return "CONVERT(datetime,'"+formatDateString+"',120)";
			}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getNowTime( DateFormat dateFormat ,DateSymbol dateSymbol) {
		if( null == dateFormat) dateFormat = DateFormat.YMD_HMIS;
		if( null == dateSymbol ) dateSymbol = new DateSymbol();
		String dateFormatString = dateFormat.getDateFormat(dateSymbol);
		if( "T".equals(dateSymbol.getDateTimeSymbol()) || "Z".equals(dateSymbol.getEndSymbol())){
			dateFormatString = dateFormatString.replace("T", "+").replace("Z", "#");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
		Date dt = new Date();
		String dateString = sdf.format( dt );
		return dateString;
	}
	public static String getNowTime( DateFormat dateFormat){
		return getNowTime(dateFormat,null);
	}
	public static String getNowTime(){
		return getNowTime(null,null);
	}
	
	/**
	 * 根据dateformat 比较日期大小
	 * return 1 : date1>date2
	 * return 0 : date1<date2
	 * return 2 : date1 == date2 
	 */
	public static int compareDateString(String dateString1,String dateString2,DateFormat dateFormat){
		Date date1 = null,date2 = null;
		try {
			date1 = convertString2Date(dateString1,dateFormat);
			date2 = convertString2Date(dateString2,dateFormat);			
			return date1.after(date2)?1:(date1.before(date2)?0:2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static void main1(String[] args) {
		System.out.println(convertString2FormatString("2016-9-13 15:49",DateFormat.YMD));
	}
	public static void main(String[] args) {
		
		
	}
	public static void main2(String[] args) throws ParseException {
		System.out.println(convertString2OracleToDate("2016-9-13 16:38",DateFormat.YMD_HMI));
		System.out.println(getNowTime(DateFormat.YMD_HMI));
		System.out.println((int)'是');
		System.out.println((int)('0'));
		System.out.println((int)('9'));
		
		System.out.println(convertString2Date("2016-9-13 15:49"));
		
		System.out.println(convertString2FormatString("2016-9-13 15:49",DateFormat.YMD));
		System.out.println(convertString2FormatString("2016-09-13",DateFormat.YMD_HMI));
		
		//System.out.println(convertString2FormatString("",DateFormat.YMD_HMI));
		
		System.out.println(getDateStringStruct("2016-09-01 1:38:11"));
		
		System.out.println(convertString2OracleToDate("2016/09/13 16:38",DateFormat.YMD_HMIS));
		System.out.println(convertString2OracleToDate("2016-09-13 16:38",DateFormat.YMD_HMIS));
		
		System.out.println(convertDate2OracleToDate(new Date(), DateFormat.YMD));
		System.out.println(compareDateString("2016/09/12 18:38", "2016-09-12 16:38", DateFormat.YMD));
	}
}
