package com.work.process_manager;

import org.apache.log4j.PropertyConfigurator;

import com.work.process_manager.constant.Constant;
import com.work.process_manager.util.process.RemoteProcessManager;
public class App {
	static {
		//设置log配置文件路径
		PropertyConfigurator.configure(Constant.LOG_PATH);
    }
	public static void main(String[] args) {
		RemoteProcessManager rpm = new RemoteProcessManager();
		rpm.startServer(Constant.port);
	}
}
