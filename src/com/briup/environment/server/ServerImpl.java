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
 * ������Ҫ���� ���մӿͻ��˷��͹����� ���϶���
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
		
		//1.ʵ����server����
		ServerSocket server = new ServerSocket(port);
		logger.info("�������������˿�9999...");
		
		//2.���տͻ��˵�����
		logger.info("���������ڵȴ��ͻ�������...");
		Socket socket = server.accept();
		logger.info("�ͻ������ӳɹ�!");
		
		//3.��ȡ��������ʹ�ö�������
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = 
				new ObjectInputStream(is);

		//4.��ȡ�ͻ��˷��͵ļ��϶���
		Collection<Environment> coll = 
				(Collection<Environment>) ois.readObject();
		
		//5.�ͷ������Դ
		ois.close();
		socket.close();
		server.close();
		logger.info("�����������������...");
		
		//���ض���
		return coll;
	}
	
}
