package com.work.process_manager;

import com.work.process_manager.util.process.RemoteProcessManager;

public class AppClient {
	public static void main(String[] args) {

		// String host = "172.18.19.123";
		// int port = 30000;
		// String pid = "2303";
		// String cmd = "java -jar /data/ziyan/spiderMain.jar 2303 30 1";

		// String host = "172.18.19.123";
		// int port = 30000;
		// String pid = "2303";
		// String cmd = "end";

		String host = "127.0.0.1";
		int port = 30000;
		String pid = "2020";
		String cmd = "end";
		// String cmd = "notepad";
		try {
			RemoteProcessManager.execCmd(host, port, pid, cmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
