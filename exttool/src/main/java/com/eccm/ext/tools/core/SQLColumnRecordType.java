package com.eccm.ext.tools.core;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;

import com.eccm.ext.tools.date.DateFormat;
import com.eccm.ext.tools.util.DateUtil;
import com.eccm.ext.tools.util.StringUtil;

public enum SQLColumnRecordType {
	ORACLE_VARCHAR2("varchar2") {
		@Override
		public String eq(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			try{return record.getCode() + "= '" + String.valueOf(obj) +"'";
			}catch(Exception e){}
			return "";
		}

		@Override
		public String like(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			try{return record.getCode() + " like '%" + String.valueOf(obj) +"%'";
			}catch(Exception e){}
			return "";
		}

		@Override
		public String after(ColumnRecord record) {			
			return null;
		}

		@Override
		public String before(ColumnRecord record) {			
			return null;
		}

		@Override
		public String val(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "''";
			try{return  "'" + StringEscapeUtils.escapeSql(String.valueOf(obj)) + "'" ;
			}catch(Exception e){}
			return "''";
		}

		@Override
		public String toStr(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			try{return  String.valueOf(obj) ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String seq(ColumnRecord record) {
			// TODO Auto-generated method stub
			return null;
		}

	},	
	ORACLE_NUMBER("number"){
		@Override
		public String eq(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj ) return "";
			String v = String.valueOf(obj);
			try{Double.parseDouble(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " =" + v ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String like(ColumnRecord record) {
			return null;
		}

		@Override
		public String after(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Double.parseDouble(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " >=" + Double.parseDouble(v) ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String before(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Double.parseDouble(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " <=" + Double.parseDouble(v) ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String val(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "null";
			String v = String.valueOf(obj);
			try{Double.parseDouble(v);}catch(Exception e){e.printStackTrace(); return "null";}
			try{return v;
			}catch(Exception e){}
			return "null";
		}

		@Override
		public String toStr(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Double.parseDouble(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return v;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String seq(ColumnRecord record) {
			// TODO Auto-generated method stub
			return null;
		}
		
	},
	ORACLE_SEQ("number"){
		@Override
		public String eq(ColumnRecord record) {
			return ORACLE_INT.eq(record);
		}

		@Override
		public String like(ColumnRecord record) {
			return ORACLE_INT.like(record);
		}

		@Override
		public String after(ColumnRecord record) {
			return ORACLE_INT.after(record);
		}

		@Override
		public String before(ColumnRecord record) {
			return ORACLE_INT.before(record);
		}

		@Override
		public String val(ColumnRecord record) {
			return ORACLE_INT.val(record);
		}

		@Override
		public String toStr(ColumnRecord record) {
			return ORACLE_INT.toStr(record);
		}

		@Override
		public String seq(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "null";
			try{return  String.valueOf(obj) ;
			}catch(Exception e){}
			return "null";
		}
	},
	
	ORACLE_INT("number"){
		@Override
		public String eq(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj ) return "";
			String v = String.valueOf(obj);
			try{Long.parseLong(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " =" + v ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String like(ColumnRecord record) {
			return null;
		}

		@Override
		public String after(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Long.parseLong(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " >=" + Integer.parseInt(v) ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String before(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Long.parseLong(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return record.getCode() + " <=" + Integer.parseInt(v) ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String val(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "null";
			String v = String.valueOf(obj);
			try{Long.parseLong(v);}catch(Exception e){e.printStackTrace(); return "null";}
			try{return v;
			}catch(Exception e){}
			return "null";
		}

		@Override
		public String toStr(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String v = String.valueOf(obj);
			try{Long.parseLong(v);}catch(Exception e){e.printStackTrace(); return "";}
			try{return v;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String seq(ColumnRecord record) {
			return null;
		}
		
	},
	ORACLE_DATE("date"){

		@Override
		public String eq(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return null;
			String _date = "";
			if(obj instanceof Date)
				_date = DateUtil.convertDate2OracleToDate((Date)obj, DateFormat.YMD_HMIS);
			else if(obj instanceof String)
				_date = DateUtil.convertString2OracleToDate((String)obj, DateFormat.YMD_HMIS);
			else
				return null;
			if(StringUtil.isBlank(_date))return null;
			try{return record.getCode() + " =" + _date ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String like(ColumnRecord record) {
			return null;
		}

		@Override
		public String after(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return null;
			String _date = "";
			if(obj instanceof Date)
				_date = DateUtil.convertDate2OracleToDate((Date)obj, DateFormat.YMD_HMIS);
			else if(obj instanceof String)
				_date = DateUtil.convertString2OracleToDate((String)obj, DateFormat.YMD_HMIS);
			else
				return null;
			if(StringUtil.isBlank(_date))return null;
			try{return record.getCode() + " >" + _date ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String before(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return null;
			String _date = "";
			if(obj instanceof Date)
				_date = DateUtil.convertDate2OracleToDate((Date)obj, DateFormat.YMD_HMIS);
			else if(obj instanceof String)
				_date = DateUtil.convertString2OracleToDate((String)obj, DateFormat.YMD_HMIS);
			else
				return null;
			if(StringUtil.isBlank(_date))return null;
			try{return record.getCode() + " <" + _date ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String val(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "null";
			String _date = "";
			if(obj instanceof Date)
				_date = DateUtil.convertDate2OracleToDate((Date)obj, DateFormat.YMD_HMIS);
			else if(obj instanceof String)
				_date = DateUtil.convertString2OracleToDate((String)obj, DateFormat.YMD_HMIS);
			else
				return "null";
			if(StringUtil.isBlank(_date))return "null";
			try{return  _date ;
			}catch(Exception e){}
			return "null";
		}

		@Override
		public String toStr(ColumnRecord record) {
			Object obj = record.getObj();
			if(null == obj) return "";
			String _date = "";
			if(obj instanceof Date)
				_date = DateUtil.convertDate2String((Date)obj, DateFormat.YMD_HMIS);
			else if(obj instanceof String)
				_date = (String)obj;
			else
				return "";
			if(StringUtil.isBlank(_date))return "";
			try{return  _date ;
			}catch(Exception e){}
			return "";
		}

		@Override
		public String seq(ColumnRecord record) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	};
	
	
	SQLColumnRecordType(String type){
		this.type = type;
	} 
	public String getType(){
		return this.type;
	}
	
	
	private String type;
	abstract String val(ColumnRecord record);
	abstract String eq(ColumnRecord record);
	abstract String like( ColumnRecord record);
	abstract String after( ColumnRecord record);
	abstract String before( ColumnRecord record);
	abstract String toStr(ColumnRecord record);
	abstract String seq(ColumnRecord record);
}         
