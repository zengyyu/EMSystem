package com.briup.environment.main;

import java.util.Collection;

import com.briup.environment.bean.Environment;
import com.briup.environment.client.Client;
import com.briup.environment.client.ClientImpl;
import com.briup.environment.client.Gather;
import com.briup.environment.client.GatherImpl;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;
import com.briup.environment.util.LogImpl;

/**
 * 
 * �ͻ��˶������
 * 
 * @author briup
 *
 */
public class ClientMain {

	public static void main(String[] args) throws Exception {
		Configuration con = new ConfigurationImpl();
		
		Log logger = con.getLogger();
		
		//1.�ͻ�����ȥ�ɼ�����
		//Gather g = new GatherImpl();
		Gather g = con.getGather();
		Collection<Environment> list = g.gather();
		logger.info("�ɼ������ݸ���: " + list.size());
		
		//2.Ȼ��ͨ�����紫���������
		//Client client = new ClientImpl();
		Client client = con.getClient();
		client.send(list);
		logger.info("�ͻ��˷��ͼ��������!");
	}
}
