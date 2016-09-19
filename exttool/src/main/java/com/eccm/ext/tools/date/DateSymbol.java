package com.eccm.ext.tools.date;

public class DateSymbol {
	private String ymdSymbol = "-";
	private String dateTimeSymbol = " ";
	private String hmisSymbol = ":";
	private String endSymbol = "";
	
	public DateSymbol(){}
	public DateSymbol(String ymdSymbol){
		this.ymdSymbol = ymdSymbol;
	}	
	public DateSymbol(String ymdSymbol,String dateTimeSymbol,String hmisSymbol){
		this.ymdSymbol = ymdSymbol;
		this.dateTimeSymbol = dateTimeSymbol;
		this.hmisSymbol = hmisSymbol;
	}
	public DateSymbol(String ymdSymbol,String dateTimeSymbol,String hmisSymbol,String endSymbol){
		this(ymdSymbol,dateTimeSymbol,hmisSymbol);
		this.endSymbol = endSymbol;
	}
	
	public String getYmdSymbol() {
		return ymdSymbol;
	}
	public void setYmdSymbol(String ymdSymbol) {
		this.ymdSymbol = ymdSymbol;
	}
	public String getDateTimeSymbol() {
		return dateTimeSymbol;
	}
	public void setDateTimeSymbol(String dateTimeSymbol) {
		this.dateTimeSymbol = dateTimeSymbol;
	}
	public String getHmisSymbol() {
		return hmisSymbol;
	}
	public void setHmisSymbol(String hmisSymbol) {
		this.hmisSymbol = hmisSymbol;
	}
	public String getEndSymbol() {
		return endSymbol;
	}
	public void setEndSymbol(String endSymbol) {
		this.endSymbol = endSymbol;
	}

}
