package com.briup.environment.main;

import java.util.Collection;

import com.briup.environment.bean.Environment;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.DBStoreImpl;
import com.briup.environment.server.Server;
import com.briup.environment.server.ServerImpl;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;
import com.briup.environment.util.LogImpl;

/**
 * 
 * �������������
 * 
 * @author briup
 *
 */
public class ServerMain {

	public static void main(String[] args) throws Exception {
		Configuration con = new ConfigurationImpl();
		
		Log logger = con.getLogger();
		
		//1.�ӿͻ��˽��մ��ݵ�����
		//Server server = new ServerImpl();
		Server server = con.getServer();
		Collection<Environment> list = server.reciver();
		logger.info("�ӿͻ��˽������ݸ���: " + list.size());
		
		//2.�����ݿ���д��
		logger.info("׼�������ݿ��в�������...");
		//DBStore dbStore = new DBStoreImpl();
		DBStore dbStore = con.getDbStore();
		dbStore.saveDb(list);
	}
}
