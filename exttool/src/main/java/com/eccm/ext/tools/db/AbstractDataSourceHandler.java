package com.eccm.ext.tools.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Stone on 2015/9/9.
 */
public abstract class AbstractDataSourceHandler {
   
    static String CONFIG_PRE = "wfExtDb.";
    static String CONFIG_FILENAME = "trivial.properties";

   // DataSourceFactory dataSourceFactory;
    public AbstractDataSourceHandler() {
        //dataSourceFactory =new DataSourceFactory();
    }

    public BasicDataSource build() throws Exception {
        Properties dataSourcePros = getDefaultDsProperties();
        String dsConfigPrefix = CONFIG_PRE+getDatasourceName();

        Properties pros = getDefaultDsProperties();
        InputStream in	 = null;
        try{
            in = AbstractDataSourceHandler.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            pros.load(in);
            for(String proName : pros.stringPropertyNames()){
                if(proName.startsWith(dsConfigPrefix)){
                	if(proName.contains("dbType")){
                		String dt =  pros.getProperty(proName);
                		if(validationQueryMap.containsKey(dt))
                			dataSourcePros.put("validationQuery", validationQueryMap.get(dt));
                	}else
	                    dataSourcePros.put(
	                            proName.substring(dsConfigPrefix.length()+1),
	                            pros.getProperty(proName));
                }
            }
        }catch(Exception e){
            throw e;
        }finally{
            if(in!=null) try{in.close();}catch(Exception e){}
        }
        return ( BasicDataSource ) BasicDataSourceFactory.createDataSource( dataSourcePros );
    }
    public QueryRunner buildQueryRunner() throws Exception {
        return new QueryRunner(build(), true);
    }


    private Properties getDefaultDsProperties(){
        Properties pros = new Properties();
       // pros.setProperty("driverClassName",JDBC_DRIVER_NAME);
        pros.setProperty("maxActive", "40");
        pros.setProperty("maxIdle","20");
        pros.setProperty("minIdle","10");
        pros.setProperty("maxWait","3000");
        //pros.setProperty("validationQuery", getValidationQuery());
        pros.setProperty("testOnBorrow", "false");
        pros.setProperty("testWhileIdle", "true"); 
        /*pros.setProperty("username",params.get(dbAlias+".username"));
        pros.setProperty("password",params.get(dbAlias+".password"));
        pros.setProperty("url",params.get(dbAlias+".url"));*/
        return pros;
    }
   	private String dbType = "sqlserver";
    protected abstract String getDatasourceName();
    private static final HashMap<String ,String > validationQueryMap = new HashMap<String,String>();
    static  {
    	validationQueryMap.put("sqlserver", "SELECT 'x'");
    	validationQueryMap.put("oracle", "SELECT 'x' from dual");
    	validationQueryMap.put("mysql", "SELECT 'x' ");
    }


}
