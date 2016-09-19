package com.eccm.ext.tools.db.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionResource {
	
	public DBConnectionResource( Connection connection, Statement statement, ResultSet resultSet ) {
		this.connection = connection;
		this.statement = statement;
		this.resultSet = resultSet;
	}
	
	public Connection connection;
	public Statement statement;
	public ResultSet	resultSet;
}