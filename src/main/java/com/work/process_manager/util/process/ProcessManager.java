package com.work.process_manager.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 创建人：wanghaibo <br>
 * 创建时间：2015-4-8 下午2:57:07 <br>
 * 功能描述： <br>
 * 进程管理类
 * 版本： <br>
 * ====================================== <br>
 * 修改记录 <br>
 * ====================================== <br>
 * 序号 姓名 日期 版本 简单描述 <br>
 * 
 */  

public class ProcessManager {
	public static final int SUCCESS = 0; // 表示程序执行成功

	public static final String SUCCESS_MESSAGE = "程序执行完成！";

	public static final String ERROR_MESSAGE = "程序执行中断：";
	
	public volatile Map<String, Process> processes = new HashMap<>();
	
	/**
	 * 启动子进程
	 * @param id
	 * @param command
	 */
	public synchronized boolean start(final String pid,String command){
		//端口号被占
		if(processes.get(pid)!=null){
			return false;
		}
		try {
			//进程启动
			Process process = Runtime.getRuntime().exec(command);
//			Process process = new ProcessBuilder(command, "").start();
			processes.put(pid, process);
			//等待进程结束线程,结束后从map中移除
			new Thread(new ProcessThread(pid,process)).start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	class ProcessThread implements Runnable{
		String pid;
		Process process;
		
		public ProcessThread(String pid,Process process) {
			this.pid = pid;
			this.process = process;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 打印程序输出
			readProcessOutput(process);
			
			// 等待程序执行结束并输出状态
			int exitCode;
			try {
				exitCode = process.waitFor();
				if (exitCode == SUCCESS) {//正常进程退出
				    System.out.println(pid+"---"+SUCCESS_MESSAGE);
				} else {
				    System.err.println(pid+"---"+ERROR_MESSAGE + exitCode);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				destory(pid);
			}
		}
	}
	
	/**
	 * 关闭子进程
	 * @param id
	 */
	public synchronized void destory(String pid){
		if(processes.get(pid)!=null){
			processes.get(pid).destroy();
			processes.remove(pid);
		}
	}
	
	/**
	 * 打印进程输出
	 * 
	 * @param process
	 *            进程
	 */
	private void readProcessOutput(Process process) {
		// 将进程的正常输出在 System.out 中打印，进程的错误输出在 System.err 中打印
//		read(process.getInputStream(), System.out);
//		read(process.getErrorStream(), System.err);
		Thread infoThread = new Thread(new ProcessIOThread(process.getInputStream(), System.out));
		infoThread.setDaemon(true);
		infoThread.start();
		
		Thread errThread = new Thread(new ProcessIOThread(process.getErrorStream(), System.err));
		errThread.setDaemon(true);
		errThread.start();
	}
	
	class ProcessIOThread implements Runnable{
		InputStream in;
		PrintStream out;
		
		public ProcessIOThread(InputStream in, PrintStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			 read(in, out);
		}
	}

	// 读取输入流
	private void read(InputStream inputStream, PrintStream out) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

//			String line;
//			while ((line = reader.readLine()) != null) {
//				out.println(line);
//			}
			while (reader.readLine() != null) {
//				out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ProcessManager processManager = new ProcessManager();
		processManager.start("2020", "notepad");
		processManager.start("2021", "notepad");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processManager.destory("2020");
	}
}
