package com.eccm.ext.tools.core;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class Argument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3155381950377374789L;
	private String code;
	private Object obj;
	private String desc;
	private HashMap<String,String> _ext = new HashMap<String, String>();
	
	
	public Argument(String code){
		this(code,null);
	}
	
	public Argument(String code,Object obj){
		this(code,obj,null);
	}
	public Argument(String code,Object obj,String desc){
		this.code = code;
		this.obj = obj;
		this.desc = desc;
	}
	public Argument argTo(ArgumentAdaptor<? extends Argument> adaptor){
		if(null == adaptor) return this;
		return adaptor.adapt(this);
	}
	
	public void setObj(Object obj){
		this.obj = obj;
	}
	
	public String getCode() {
		return code;
	}
	public Object getObj() {
		return obj;
	}
	public String getDesc() {
		return desc;
	}
	
	public void putExtValue(String key, String value){
		_ext.put(key, value);
	}
	
	public String getExtValue(String key){
		return _ext.containsKey(key)?_ext.get(key):"";
	}
	
	@Override
	public boolean equals(Object obj) {
		Argument ar = (Argument)obj;
		return code.equals(ar.getCode());
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
