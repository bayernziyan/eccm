package com.eccm.ext.tools.core;

public class TableRecord extends Object {

	private String schema;
	private String name;
	private ArgumentFields<ColumnRecord> columnFields;

	public TableRecord(){}
	public TableRecord(String name) {
		this(name, null, null);
	}

	public TableRecord(String name, String schema) {
		this(name, schema, null);
	}

	public TableRecord(String name, String schema, ArgumentFields<ColumnRecord> columnFields) {
		this.name = name;
		this.schema = schema;
		if( null == columnFields)
		this.columnFields = new ArgumentFields<ColumnRecord>();
		else this.columnFields = columnFields;
	}
	public String getName(){
		return name;
	}
	public String getSchema(){
		return schema;
	}
	public final ArgumentFields<ColumnRecord> columns() {
        return columnFields;
    }
	
	
}
