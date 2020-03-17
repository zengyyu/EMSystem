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
 * 服务器端入口类
 * 
 * @author briup
 *
 */
public class ServerMain {

	public static void main(String[] args) throws Exception {
		Configuration con = new ConfigurationImpl();
		
		Log logger = con.getLogger();
		
		//1.从客户端接收传递的数据
		//Server server = new ServerImpl();
		Server server = con.getServer();
		Collection<Environment> list = server.reciver();
		logger.info("从客户端接收数据个数: " + list.size());
		
		//2.往数据库中写入
		logger.info("准备往数据库中插入数据...");
		//DBStore dbStore = new DBStoreImpl();
		DBStore dbStore = con.getDbStore();
		dbStore.saveDb(list);
	}
}
