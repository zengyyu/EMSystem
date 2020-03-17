package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

/*
 * ������Ҫ�� ���ݼ��ϵ������ļ� 
 * 	�ӱ��ر����ļ�����ȡ���� �� ����
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

	//��ɱ��ݹ��ܣ���������ʣ������д�뱾���ļ���
	@Override
	public void backup(String fileName, Object data) throws Exception {
		FileOutputStream fos = 
				new FileOutputStream(fileName);
		ObjectOutputStream oos = 
				new ObjectOutputStream(fos);
		oos.writeObject(data);
		
		oos.close();
	}

	//�ӱ����ļ�����ȡ��Ч���ݵ������в�����
	@Override
	public Object load(String fileName) throws Exception {
		File file = new File(fileName);
		if(file.exists() && file.isFile()) {
			FileInputStream fis = 
					new FileInputStream(file);
			ObjectInputStream ois = 
					new ObjectInputStream(fis);
			//logger.info("����ģ�飬��ȡ��������...");
			Object object = ois.readObject();
			
			ois.close();
			//ɾ����ǰ�����ļ�
			//file.delete();
			logger.info("����ģ��������ȡ�ɹ���ɾ�������ļ�...");
			return object;
		}
		
		return null;
	}

	//ɾ�������ļ�
	@Override
	public void deleteBackup(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			boolean b = file.delete();
			if(b)
				logger.info("�����ļ�ɾ���ɹ�");
			else
				logger.info("�����ļ�ɾ��ʧ��");
		}
	}

	
}
