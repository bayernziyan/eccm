package com.eccm.ext.tools;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eccm.ext.tools.core.SQLColumnRecordType;
import com.eccm.ext.tools.core.TableRecordImpl;

public class DbUtilTest {
	
	@Test
	public void table(){
		TableRecordImpl usertable = new TableRecordImpl("user_table","newsoft",null);
		usertable.createField("mi", SQLColumnRecordType.ORACLE_VARCHAR2);		
		usertable.createField("user_skey", SQLColumnRecordType.ORACLE_NUMBER);
		usertable.createField("user_id", SQLColumnRecordType.ORACLE_VARCHAR2);
		
		System.out.println(usertable.columns());
		
	}
}
