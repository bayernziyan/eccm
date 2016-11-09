package com.eccm.ext.tools.sap;

import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;



public class SapService {
	private static MyDestinationDataProvider myProvider = null;
	private static SapService spsv = null;
	/**
     * @param context  json <br>
     * key: SAPSystem   sap系统对象名称 <br>
     * key: SAPFunc	      调用方法名称<br>
     * 
     */
	public RFCProvider createRFCProvider(String connectorId, String funcName) {
		RFCProvider rfc_pd = null;
		try {
			rfc_pd = new RFCProvider(connectorId, funcName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return rfc_pd;
		}
	}

	public synchronized static SapService getInstance(){
    	if(spsv==null){
    		spsv = new SapService();
    	}
    	return spsv;
    }
    private SapService(){
    	registerSAPConfig();
    } 
    
    /**
     * 重新注册SAP连接信息
     */
    public synchronized static void reRegisterSAPConfig(){
    	Environment.unregisterDestinationDataProvider(myProvider);
    	registerSAPConfig();
    }
    
	private static void registerSAPConfig() {
		myProvider = new MyDestinationDataProvider();

		ResourceBundle rb = ResourceBundle.getBundle("SAP_CONFIG",Locale.getDefault());
		try {
			String SAP_CONN_LIST = rb.getString("SAP_CONN_CONFIG");

			String[] sap_conn_v = SAP_CONN_LIST.split("////");

			for (String sap_conn : sap_conn_v) {
				SAPConfigBean currBean = loadSAPConfig(rb, sap_conn);
				Properties connectProperties;

				connectProperties = getProperties(currBean.getMshost(),
						currBean.getR3name(), currBean.getClient(),
						currBean.getGroup(), currBean.getUser(),
						currBean.getPasswd(), currBean.getLang());

				myProvider.addDestination(sap_conn, connectProperties);
			}

			Environment.registerDestinationDataProvider(myProvider);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static SAPConfigBean loadSAPConfig(ResourceBundle rb,String SAPSystem){
    	SAPConfigBean bean = new SAPConfigBean();
		bean.setMshost(rb.getString(SAPSystem+".mshost"));
		bean.setR3name(rb.getString(SAPSystem+".r3name"));
		//bean.setGroup(rb.getString(SAPSystem+".group"));
		bean.setClient(rb.getString(SAPSystem+".client"));
		bean.setUser(rb.getString(SAPSystem+".user"));
		bean.setPasswd(rb.getString(SAPSystem+".passwd"));
		bean.setLang(rb.getString(SAPSystem+".lang"));
		return bean;
	}
	private static SAPConfigBean loadSAPConfig(String json){
		JSONObject jsonObj = JSONObject.fromObject(json);
    	SAPConfigBean bean = new SAPConfigBean();
		bean.setMshost(jsonObj.getString("sapHost"));
		bean.setR3name(jsonObj.getString("sapR3name"));
		//bean.setGroup(rb.getString(SAPSystem+".group"));
		bean.setClient(jsonObj.getString("sapClient"));
		bean.setUser(jsonObj.getString("sapUser"));
		bean.setPasswd(jsonObj.getString("sapPassword"));
		bean.setLang(jsonObj.getString("sapLanguage"));
		return bean;
	}
	private static Properties getProperties(String mshost, String r3name, String client, String group, String user,
			String passwd, String lang) throws Exception {
		Properties connectProperties = new Properties();		
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, mshost);
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00"); 
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, client);
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, user);
		//connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, r3name);
		//connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, group);
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, passwd);
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, lang);
		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "100");
		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "50");
		connectProperties.setProperty(DestinationDataProvider.JCO_EXPIRATION_TIME, "10000");
		return connectProperties;
	}
	
	
	public static void main(String[] args) {
//		SapService sapsrv = SapService.getInstance();
//		RFCProvider rfc_pd = sapsrv.createRFCProvider("SAP-1","Z_REF_GETFI002");
//		System.out.println(rfc_pd.getAllRFCParams());
		JSONArray inputParamArray = new JSONArray();
		JSONArray outputParamArray = new JSONArray();
		
		/*
		 * 
		 * 
		 */
		JSONObject jobj = new JSONObject();
		
	}
}
