package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

/*
 * 该类主要做 备份集合到本地文件 
 * 	从本地备份文件中提取数据 到 集合
 */
public class BackupImpl implements Backup,ConfigurationAWare {
	
	private Log logger;
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}
	
	@Override
	public void init(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	//完成备份功能，将集合中剩余数据写入本地文件中
	@Override
	public void backup(String fileName, Object data) throws Exception {
		FileOutputStream fos = 
				new FileOutputStream(fileName);
		ObjectOutputStream oos = 
				new ObjectOutputStream(fos);
		oos.writeObject(data);
		
		oos.close();
	}

	//从备份文件中提取有效数据到集合中并返回
	@Override
	public Object load(String fileName) throws Exception {
		File file = new File(fileName);
		if(file.exists() && file.isFile()) {
			FileInputStream fis = 
					new FileInputStream(file);
			ObjectInputStream ois = 
					new ObjectInputStream(fis);
			//logger.info("备份模块，获取备份数据...");
			Object object = ois.readObject();
			
			ois.close();
			//删除当前备份文件
			//file.delete();
			logger.info("备份模块数据提取成功，删除备份文件...");
			return object;
		}
		
		return null;
	}

	//删除备份文件
	@Override
	public void deleteBackup(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			boolean b = file.delete();
			if(b)
				logger.info("备份文件删除成功");
			else
				logger.info("备份文件删除失败");
		}
	}

	
}
