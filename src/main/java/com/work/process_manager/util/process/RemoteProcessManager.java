package com.work.process_manager.util.process;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
*    
* 项目名称：spider-backstage   
* 类名称：RemoteProcessManager   
* 类描述：   远程进程管理
* 创建人：admin   
* 创建时间：2015年5月28日 下午2:43:11   
* 修改人：admin   
* 修改时间：2015年5月28日 下午2:43:11   
* 修改备注：   
* @version    
*    
*/
public class RemoteProcessManager {
	public static final Logger LOGGER = LoggerFactory.getLogger(RemoteProcessManager.class);
	
	ProcessManager processManager = new ProcessManager();

	/**
	 * 启动进程监听
	 * @param port
	 */
	public void startServer(int port) {
		// 定义一个ServerSocket监听在某端口上
		ServerSocket server = null;
		Socket socket = null;
		Reader reader = null;
		Writer writer = null;
		try {
			server = new ServerSocket(port);
			while(true){
				// server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
				socket = server.accept();
				// 跟客户端建立好连接之后，我们就可以获取socket的InputStream，并从中读取客户端发过来的信息了。
				reader = new InputStreamReader(socket.getInputStream());
				char chars[] = new char[64];
				int len;
				StringBuilder sb = new StringBuilder();
				String temp;
				int index;
				while ((len = reader.read(chars)) != -1) {
					temp = new String(chars, 0, len);
					if ((index = temp.indexOf("eof")) != -1) {// 遇到eof时就结束接收
						sb.append(temp.substring(0, index));
						break;
					}
					sb.append(temp);
				}
//				System.out.println("from client: " + sb);
				LOGGER.info("from client: " + sb);
				JSONObject json = JSONObject.fromObject(sb.toString());
				// 读完后写一句
				writer = new OutputStreamWriter(socket.getOutputStream());
				if(json.containsKey("pid")&&json.containsKey("cmd")){
					if("end".equals(json.getString("cmd"))){//关闭
						processManager.destory(json.getString("pid"));
						json.put("result", "0");//关闭成功
						LOGGER.info(json.getString("pid")+"关闭成功");
					}else{//启动
						boolean flag = processManager.start(
								json.getString("pid"), json.getString("cmd"));
						if (flag) {
							json.put("result", "0");//启动成功
							LOGGER.info(json.getString("pid")+"启动成功");
						} else {
							json.put("result", "1");//启动失败
							LOGGER.info(json.getString("pid")+"启动失败");
						}
					}
				}
				writer.write(json.toString());
				writer.write("eof");
				writer.flush();
				writer.close();
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				reader.close();
				socket.close();
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 发送命令
	 * @param ip
	 * @param port
	 * @param pid
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public static boolean execCmd(String host, int port, String pid, String cmd)  throws Exception {
		// 与服务端建立连接
		Socket client = new Socket(host, port);
		// 建立连接后就可以往服务端写数据了
		Writer writer = new OutputStreamWriter(client.getOutputStream());
		JSONObject cmdJson = new JSONObject();
		cmdJson.put("pid", pid);
		cmdJson.put("cmd", cmd);
		writer.write(cmdJson.toString());
		writer.write("eof");
		writer.flush();
		// 写完以后进行读操作
		Reader reader = new InputStreamReader(client.getInputStream());
		char chars[] = new char[64];
		int len;
		StringBuffer sb = new StringBuffer();
		String temp;
		int index;
		while ((len = reader.read(chars)) != -1) {
			temp = new String(chars, 0, len);
			if ((index = temp.indexOf("eof")) != -1) {
				sb.append(temp.substring(0, index));
				break;
			}
			sb.append(new String(chars, 0, len));
		}
		writer.close();
		reader.close();
		client.close();
		
		LOGGER.info("from server: " + sb);
		JSONObject json = JSONObject.fromObject(sb.toString());
		if ("0".equals(json.getString("result"))) {
			LOGGER.info(json.getString("pid") + "操作成功");
			return true;
		} else {
			LOGGER.info(json.getString("pid") + "操作失败");
			return false;
		}
	}
}
