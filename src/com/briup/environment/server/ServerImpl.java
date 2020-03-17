package com.briup.environment.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationAWare;
import com.briup.environment.util.Log;

/*
 * 该类主要用于 接收从客户端发送过来的 集合对象
 */
public class ServerImpl implements Server,ConfigurationAWare {

	private int port;
	private Log logger;
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}
	
	@Override
	public void init(Properties properties) throws Exception {
		String pStr = properties.getProperty("port");
		port = Integer.parseInt(pStr);
	}

	@Override
	public Collection<Environment> reciver() throws Exception {
		
		//1.实例化server对象
		ServerSocket server = new ServerSocket(port);
		logger.info("服务器启动，端口9999...");
		
		//2.接收客户端的连接
		logger.info("服务器正在等待客户端连接...");
		Socket socket = server.accept();
		logger.info("客户端连接成功!");
		
		//3.获取操作流【使用对象流】
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = 
				new ObjectInputStream(is);

		//4.获取客户端发送的集合对象
		Collection<Environment> coll = 
				(Collection<Environment>) ois.readObject();
		
		//5.释放相关资源
		ois.close();
		socket.close();
		server.close();
		logger.info("服务器接收数据完成...");
		
		//返回对象
		return coll;
	}
	
}
