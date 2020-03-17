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
 * 配置模块
 * 	1.解析配置文件 根据配置文件信息 创建所有对象
 * 	添加到map集合中
 * 	2.将配置信息解析出来，给对象进行初始化
 * 
 */
public class ConfigurationImpl implements Configuration {
	Map<String, WossModule> map; 
	
	public static void main(String[] args) {
		Configuration c = new ConfigurationImpl();
		
	}
	
	//配置模块对象 
	//	1.构造所有对象 添加到map集合
	//	2.对所有对象进行配置初始化	
	public ConfigurationImpl() {
		map = new HashMap<>();
		
		try {
			//获得一个SAXReader对象
			SAXReader reader = new SAXReader();
			File file = new File("src/com/briup/environment/util/config.xml");
			//读取这个要解析的xml文件
			Document document = reader.read(file);
			
			Element rootElement = document.getRootElement();
			//获得根节点下面所有的子节点
			List<Element> elements = rootElement.elements();
			//System.out.println("length: " + elements.size());
			
			//遍历elements集合,拿到每一个子节点
			for(Element e : elements){
				String name = e.getName();
				String cStr = e.attributeValue("class");
				
				//由类的全包名实例化对象
				Class clazz = Class.forName(cStr);
				WossModule wm = (WossModule) clazz.newInstance();
				
				//获取 并 遍历配置信息子标签
				List<Element> elements2 = e.elements();
				//准备 配置类，用来存储 配置信息
				Properties prop = new Properties();
				//遍历elements2集合,拿到每一个子节点
				for(Element e2 : elements2){
					String key = e2.getName();
					String value = e2.getText();
					prop.setProperty(key, value);
				}
				
				//使用对象 进行初始化操作
				wm.init(prop);
				//将完整的对象 添加到 map集合中
				map.put(name, wm);
			}
			
			//反向配置 将 配置对象con 交给每一个类对象
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
