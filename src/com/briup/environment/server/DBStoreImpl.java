package com.briup.environment.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Backup;
import com.briup.environment.util.BackupImpl;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationAWare;
import com.briup.environment.util.Log;
import com.briup.environment.util.LogImpl;

/*
 * ������Ҫ ���� �������
 */
public class DBStoreImpl implements DBStore,ConfigurationAWare {

	//�����ļ�·��
	private String backUpPath;
	private Log logger;
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}
	
	//���ݿ���Ҫ��
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	//*** ���ӹ���1: �����ļ�������Ҫ�� ***
	//��̬�����
	static { 
		Properties prop = null; 
		InputStream is = null;
		try {
			prop = new Properties();
			is = new FileInputStream("src/jdbc.properties");
			prop.load(is);
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null)
					is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void init(Properties properties) throws Exception {
		backUpPath = properties.getProperty("backUpPath");
	}

	//���ݿ����
	@Override
	public void saveDb(Collection<Environment> coll) throws Exception {
		//1.�жϱ����ļ��Ƿ����
		Backup b = new BackupImpl();
		List<Environment> bList = 
				(List<Environment>) b.load(backUpPath);
		
		if(bList != null) {
			//��obj���� �� coll�ϲ�
			bList.addAll(coll);
			coll = bList;
			
			//ɾ�������ļ�
			b.deleteBackup(backUpPath);
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		//����һ�� ������
		int count = 0;
		
		try {
			//1.ע������
			Class.forName(driver);
			
			//2.�������ݿ�����
			conn = DriverManager.getConnection(url, user, password);
			logger.info("�������ݿ����ӳɹ�,conn: " + conn);
			
			//�޸�����Ϊ�ֶ��ύ
			conn.setAutoCommit(false);
			logger.info("�޸��������Ϊ�ֶ�...");
			
			//3.��ȡps����
			String sql = "insert into e_detail_6 values(?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			
			//4.����?ֵ Ȼ��ִ��sql���
			logger.info("��ʼ��������...");
			logger.info("coll.size: " + coll.size());
			
			for (Environment en : coll) {
				//��������1
				count++;
				
				//��ÿ����������ֵ ����?��
				ps.setString(1, en.getName());
				ps.setString(2, en.getSrcId());
				ps.setString(3, en.getDstId());
				ps.setString(4, en.getSersorAddress());
				ps.setInt(5, en.getCount());
				ps.setString(6, en.getCmd());
				ps.setInt(7, en.getStatus());
				ps.setDouble(8, en.getData());
				long t = en.getGather_date().getTime();
				ps.setDate(9, new java.sql.Date(t));
				
				//��ӵ���������
				ps.addBatch();
				
//				if(count == 245) {
//					//����Ϊ0�����Զ��׳��쳣
//					int b = count/0;
//				}
				
				//ÿ200�������һ�� ִ�������� �ύ����
				if(count % 200 == 0 || count == coll.size()) {
					//ִ��������
					ps.executeBatch();
					//�ύ����
					conn.commit();
					logger.info("Ŀǰ�� " + count + " �����ݲ���ɹ�");
				}
			}
			
			logger.info("����������ɣ�һ������ " + count + "������");
			logger.info("�ύ����ɹ�������ȫ���ɹ�д��!");
		} catch (Exception e) {
			logger.error("�������е�"+count+"�������쳣");
			//����쳣����ϸ��Ϣ
			//e.printStackTrace();
			//logger.error(e.getMessage());
			logger.error(e.toString());
			
			//����ع�
			conn.rollback();
			logger.info("����ع��ɹ�!");
			
			//a.��ȡ�������ݸ���
			int size;//200   [0,199] [200,400)
			if(count % 200 == 0) {
				size = count - 200;
			}else {
				size = count - count % 200;
			}
			logger.info("�Ѿ�����������: " + size);
			
			//b.��ȡ��δ������������
			List<Environment> allList = 
					(List<Environment>)coll;
			List<Environment> noList = 
					allList.subList(size, coll.size());
			logger.info("��δ����������: " + noList.size());
			
			ArrayList<Environment> noList2 = new ArrayList<>();
			noList2.addAll(noList);
			
			//c.����δ�������д�뱾���ļ�
			logger.info("��ʼ����...");
			Backup bu = new BackupImpl();
			bu.backup(backUpPath, noList2);
			logger.info("���ݳɹ�!");
			
		} finally {
			if(ps != null)
				ps.close();
			
			if(conn != null)
				conn.close();
		}
	}
	
}
