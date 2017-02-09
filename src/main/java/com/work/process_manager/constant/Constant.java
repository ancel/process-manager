package com.work.process_manager.constant;

import java.io.FileInputStream;
import java.util.Properties;


public class Constant {
	// 服务器配置
	public static final String CONF_PATH = "conf/config.properties";
	public static final String LOG_PATH = "conf/log4j.properties";
	
	public static int port;
	
	static {
		Properties prop = new Properties();//属性集合对象   
		FileInputStream fis;
		try {
			fis = new FileInputStream(CONF_PATH);
			prop.load(fis);//将属性文件流装载到Properties对象中  
			//properties默认按照iso8859-1读取
			port = Integer.valueOf(new String(prop.getProperty("port").getBytes(CharSet.ISO8859_1), CharSet.UTF_8));
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
