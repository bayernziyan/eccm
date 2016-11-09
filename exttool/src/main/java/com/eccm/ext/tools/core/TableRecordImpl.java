package com.eccm.ext.tools.core;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
public class TableRecordImpl extends TableRecord {

	public TableRecordImpl(){}
	public TableRecordImpl(String name) {
		super(name);
	}
	
	public TableRecordImpl(String name, String schema, ArgumentFields<ColumnRecord> columnFields) {
		super(name, schema, columnFields);
	}
	
	public final void setColumnRecordValue(String code,Object obj){
		ColumnRecord c = (ColumnRecord) this.columns().argument(code);
		c.setObj(obj);
	}
	
	public final ColumnRecord createField(String code, SQLColumnRecordType columnTp){
		ColumnRecord column = new ColumnRecord(code, columnTp);
		this.columns().add(column);
		return column;
	}
	
	public int getColumnSize(){
		return this.columns().size();
	}
	
	public int insertRecord(Connection conn) throws SQLException{
		
			return new QueryRunner().update(conn, insertRecordSQL());
		
	}
	
	public String insertRecordSQL(){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(this.getName()).append("(");
		for(int i=0;i<getColumnSize();i++){
			
			ColumnRecord rpcolumn = (ColumnRecord)this.columns().argument(i);
			if(SQLColumnRecordType.ORACLE_DATE == rpcolumn.getColumnType())continue;
			if(i>0)sb.append(",");
			sb.append(rpcolumn.getCode());
			//if(i<getColumnSize()-1)
				
		}
		sb.append(") values(");
		for(int i=0;i<getColumnSize();i++){
			ColumnRecord rpcolumn = (ColumnRecord)this.columns().argument(i);
			if(SQLColumnRecordType.ORACLE_DATE == rpcolumn.getColumnType())continue;
			
			if(i>0)sb.append(",");
			if(SQLColumnRecordType.ORACLE_SEQ == rpcolumn.getColumnType())
				sb.append(rpcolumn.seq());
			else
				sb.append(rpcolumn.val());
			
		}
		sb.append(")");
		return sb.toString();
	}

	
	public final void createTable(){
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(this.getName()).append("(").append("\n");
		for(int i=0;i<this.columns().size();i++){
			ColumnRecord rpcolumn = (ColumnRecord)this.columns().argument(i);
			SQLColumnRecordType type =  rpcolumn.getColumnType();
			sb.append("\t").append(rpcolumn.getCode()).append("\t").append(rpcolumn.getColumnType().getType());
			if(type.equals(SQLColumnRecordType.ORACLE_NUMBER)){
				sb.append("(8,2)");
			}else if(type.equals(SQLColumnRecordType.ORACLE_INT)){
				sb.append("(8)");
			}
			else if(type.equals(SQLColumnRecordType.ORACLE_VARCHAR2)){
				sb.append("(200)");
			}else if(type.equals(SQLColumnRecordType.ORACLE_DATE)){
				sb.append(" default sysdate");
			}
			
			if(i<this.columns().size()-1)
				sb.append(",");
			sb.append("\n");
			
		}
		
		sb.append(");");
		System.out.println(sb);
	}
}
