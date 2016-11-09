package com.eccm.ext.tools.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ColumnRecord extends Argument implements SQLColumnRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3163564269354060292L;
	private SQLColumnRecordType recordType;

	
	
	public ColumnRecord(String code, SQLColumnRecordType recordType) {		
		super(code);
		this.recordType =recordType;
	}

	public ColumnRecord(String code,  String desc, SQLColumnRecordType recordType) {
		super(code, null,desc);
		this.recordType =recordType;
	}
	public ColumnRecord(String code,  String desc, SQLColumnRecordType recordType, Object obj) {
		super(code, obj,desc);
		this.recordType =recordType;
	}
	
	public SQLColumnRecordType getColumnType(){
		return this.recordType;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public String eq() {
		return recordType.eq(this);
	}

	@Override
	public String like() {		
		return recordType.like(this);
	}

	@Override
	public String after() {		
		return recordType.after(this);
	}

	@Override
	public String before() {		
		return recordType.before(this);
	}

	@Override
	public String val() {
		return recordType.val(this);
	}

	@Override
	public String toStr() {
		return recordType.toStr(this);
	}

	@Override
	public String seq() {
		return recordType.seq(this);	
	}
}
