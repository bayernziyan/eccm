package com.eccm.ext.tools.date;



public enum DateFormat {
	//yyyy-MM-dd
	YMD(new int[]{10,4,7}), 
	//yyyy-MM-dd HH:mm
	YMD_HMI(new int[]{16,4,7,10,13}), 
	//yyyy-MM-dd HH:mm:ss
	YMD_HMIS(new int[]{19,4,7,10,13,16}), 
	//yyyy-MM-ddTHH:mm:ssZ
	FULL(new int[]{20,4,7,10,13,16,20}),
	//yyyyMMdd
	//YMD_NOSB(new int[]{8,4,7}),
	Default(new int[]{});
	
	/*
	YMD_M1(new int[]{9,4,6}),YMD_M1D1(new int[]{8,4,6}),
	YMD_D1(new int[]{9,4,7}),
	
	//YMD_HMI_H1(new int[]{15,4,7,10,12}), YMD_HMIS_H1(new int[]{18,4,7,10,12,15}),
	YMD_HMI_M1(new int[]{15,4,6,9,12}), YMD_HMIS_M1(new int[]{18,4,6,9,12,15}),
	YMD_HMI_D1(new int[]{15,4,7,9,12}), YMD_HMIS_D1(new int[]{18,4,7,9,12,15}),
	YMD_HMI_H1(new int[]{15,4,7,10,12}), YMD_HMIS_H1(new int[]{18,4,7,10,12,15}),
	YMD_HMI_MI1(new int[]{15,4,7,10,13}), YMD_HMIS_MI1(new int[]{18,4,7,10,13,15}),
										  YMD_HMIS_S1(new int[]{18,4,7,10,13,16}),
	
	
	YMD_HMI_M1D1(new int[]{14,4,6,8,11}), 
	YMD_HMI_M1H1(new int[]{14,4,6,9,11}),
	YMD_HMI_M1MI1(new int[]{14,4,6,9,12}), 	
	
	YMD_HMIS_M1D1(new int[]{17,4,6,8,11,14}),*/
	;
	
	private int[] _points;
	
	private DateFormat(int[] points){
		this._points = points;
	}
	public int[] getStructPoint(){
		return this._points;
	}
	
	public String getDateFormat(DateSymbol dateSymbol){
		if(_points.length>0)
			return getDateFormat(_points,dateSymbol);
		throw new NullPointerException("日期结构参数 points 为空");
	}
	
	public String getDateFormat(int[] points, DateSymbol dateSymbol) {
		if (null == dateSymbol)
			dateSymbol = new DateSymbol();
		String format = "";
		String[] symbolArr = new String[]{"",dateSymbol.getYmdSymbol(),dateSymbol.getYmdSymbol(),
				dateSymbol.getDateTimeSymbol(),dateSymbol.getHmisSymbol(),dateSymbol.getHmisSymbol(),dateSymbol.getEndSymbol()}; 
		String[] cArr = new String[]{"y","M","d","H","m","s"};
		StringBuilder sb = new StringBuilder();
		int len = points[0];
		String c = cArr[0];
		int ind = 1;
		for(int i=0;i<len;i++){
			String nextc = c;
			if (ind<points.length && i == points[ind]){
				c = symbolArr[ind];
				nextc = cArr[ind];
				ind++;
			}
			sb.append(c);
			c = nextc;
		}
		if(sb.length()>0) format = sb.toString();		
		return format;
	}
	
	public String getOraleDateFormat(DateSymbol dateSymbol){
		//yyyy-mm-dd hh24:mi:ss
		if (null == dateSymbol)
			dateSymbol = new DateSymbol();
		String format = "";
		switch (this) {
		case YMD:
			format = "yyyy" + dateSymbol.getYmdSymbol() + "mm" + dateSymbol.getYmdSymbol() + "dd";break;
		case YMD_HMI:
			format = "yyyy" + dateSymbol.getYmdSymbol() + "mm" + dateSymbol.getYmdSymbol() + "dd";
			format += dateSymbol.getDateTimeSymbol() + "hh24" + dateSymbol.getHmisSymbol() + "mi";
			break;
		case YMD_HMIS:
			format = "yyyy" + dateSymbol.getYmdSymbol() + "mm" + dateSymbol.getYmdSymbol() + "dd";
			format += dateSymbol.getDateTimeSymbol() + "hh24" + dateSymbol.getHmisSymbol() + "mi";
			format += dateSymbol.getHmisSymbol() + "ss";
			break;		
		default:
			format = "yyyy" + dateSymbol.getYmdSymbol() + "mm" + dateSymbol.getYmdSymbol() + "dd";
			format += dateSymbol.getDateTimeSymbol() + "hh24" + dateSymbol.getHmisSymbol() + "mi";
			format += dateSymbol.getHmisSymbol() + "ss";
			format += dateSymbol.getEndSymbol();
		}
		return format;
	}
	

	/*public static void main(String[] args) {
		System.out.println(Character.toString((char) 2));
		System.out.println(Integer.toBinaryString(13));
		System.out.println(DateUtil.getDateStringStruct("2016-9-01 01:3:01"));
		
		System.out.println(YMD_HMIS.getDateFormat(new DateSymbol()));
	}*/
}
