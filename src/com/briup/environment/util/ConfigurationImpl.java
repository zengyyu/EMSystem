package com.briup.environment.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;

/*
 * ����ģ��
 * 	1.���������ļ� ���������ļ���Ϣ �������ж���
 * 	��ӵ�map������
 * 	2.��������Ϣ������������������г�ʼ��
 * 
 */
public class ConfigurationImpl implements Configuration {
	Map<String, WossModule> map; 
	
	public static void main(String[] args) {
		Configuration c = new ConfigurationImpl();
		
	}
	
	//����ģ����� 
	//	1.�������ж��� ��ӵ�map����
	//	2.�����ж���������ó�ʼ��	
	public ConfigurationImpl() {
		map = new HashMap<>();
		
		try {
			//���һ��SAXReader����
			SAXReader reader = new SAXReader();
			File file = new File("src/com/briup/environment/util/config.xml");
			//��ȡ���Ҫ������xml�ļ�
			Document document = reader.read(file);
			
			Element rootElement = document.getRootElement();
			//��ø��ڵ��������е��ӽڵ�
			List<Element> elements = rootElement.elements();
			//System.out.println("length: " + elements.size());
			
			//����elements����,�õ�ÿһ���ӽڵ�
			for(Element e : elements){
				String name = e.getName();
				String cStr = e.attributeValue("class");
				
				//�����ȫ����ʵ��������
				Class clazz = Class.forName(cStr);
				WossModule wm = (WossModule) clazz.newInstance();
				
				//��ȡ �� ����������Ϣ�ӱ�ǩ
				List<Element> elements2 = e.elements();
				//׼�� �����࣬�����洢 ������Ϣ
				Properties prop = new Properties();
				//����elements2����,�õ�ÿһ���ӽڵ�
				for(Element e2 : elements2){
					String key = e2.getName();
					String value = e2.getText();
					prop.setProperty(key, value);
				}
				
				//ʹ�ö��� ���г�ʼ������
				wm.init(prop);
				//�������Ķ��� ��ӵ� map������
				map.put(name, wm);
			}
			
			//�������� �� ���ö���con ����ÿһ�������
			for (String name : map.keySet()) {
				WossModule woss = map.get(name);
				if(woss instanceof ConfigurationAWare) {
					ConfigurationAWare w = (ConfigurationAWare) woss;
					w.setConfiguration(this);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Log getLogger() throws Exception {
		Log log = (Log) map.get("logger");
		return log;
	}

	@Override
	public Server getServer() throws Exception {
		Server server = (Server) map.get("server");
		return server;
	}

	@Override
	public Client getClient() throws Exception {
		Client client = (Client) map.get("client");
		return client;
	}

	@Override
	public DBStore getDbStore() throws Exception {
		DBStore dbStore = (DBStore) map.get("dbstore");
		return dbStore;
	}

	@Override
	public Gather getGather() throws Exception {
		Gather gather = (Gather) map.get("gather");
		return gather;
	}

	@Override
	public Backup getBackup() throws Exception {
		Backup backUp = (Backup) map.get("backup");
		return backUp;
	}
	
}
