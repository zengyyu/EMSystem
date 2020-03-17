package com.briup.environment.client;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationAWare;
import com.briup.environment.util.Log;
import com.briup.environment.util.LogImpl;

/*
 * 客户端实现类，主要用来给服务器 发送 集合对象
 */
public class ClientImpl implements Client,ConfigurationAWare {

	private String serverIp;
	private int serverPort;
	
	private Log logger;
	//private Configuration con;
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
		//con = configuration;
		//logger = con.getLogger();
	}
	
	@Override
	public void init(Properties properties) throws Exception {
		serverIp = properties.getProperty("serverIp");
		String sPort = properties.getProperty("serverPort");
		serverPort = Integer.parseInt(sPort);
	}

	//重写send方法，将coll集合对象 发送到服务器端
	@Override
	public void send(Collection<Environment> coll) throws Exception {
		
		
		//1.搭建一个客户端
		Socket client = 
				new Socket(InetAddress.getByName(serverIp), serverPort);
		logger.info("客户端连接到服务器成功...");
		
		//2.获取数据传输的流对象
		ObjectOutputStream oos = 
				new ObjectOutputStream(client.getOutputStream());
		
		//3.发送coll到服务器
		oos.writeObject(coll);
		
		//4.关闭流 释放资源
		oos.close();
		client.close();
		logger.info("客户端发送数据成功!");
	}

}





