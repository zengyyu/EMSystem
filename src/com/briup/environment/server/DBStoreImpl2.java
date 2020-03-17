package com.briup.environment.server;

import java.io.File;
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

public class DBStoreImpl2 implements DBStore {

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
		// TODO Auto-generated method stub
		
	}

	//���ݿ���� 
	//*** ���ӹ���2: ������ ***
	@Override
	public void saveDb(Collection<Environment> coll) throws Exception {
		//�ȴӱ����ļ��л�ȡ����
		String backFile = "src/backUp/dbBackUp";
		//����ļ����ڣ���Ϊ��ͨ�ļ�������
		Backup bu = new BackupImpl();
		List<Environment> list = (List<Environment>) bu.load(backFile);
		//�������������ϳ�һ�����ϣ�Ȼ�����
		if(list == null) {
			list = (List<Environment>) coll;
		}else {
			list.addAll(coll);
			System.out.println("�ϲ��������ϣ�׼������Ҫ���������...");
		}
		//ɾ�������ļ�
		bu.deleteBackup(backFile);
		
		Connection conn = null;
		PreparedStatement ps = null;
		//�������������¼�������ݵ�����
		int count = 0;
		boolean cflag = false;
		try {
			//1.ע������
			Class.forName(driver);
			
			//2.�������ݿ�����
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			System.out.println("�������ݿ����ӳɹ�,conn: " + conn);
			
			//3.��ȡps����
			String sql = "insert into e_detail_3 values(?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			
			//4.����?ֵ Ȼ��ִ��sql���
			System.out.println("��ʼ��������...");
			System.out.println("list.size: " + list.size());
			
			for (Environment en : list) {
				//��������һ
				count++;
				cflag = false;
				
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
				//ִ��sql���
				//ps.execute();
				
				//��ӵ�������
				ps.addBatch();
				
				//д��400���������쳣
//				if(count == 340) {
//					int b = count / 0;
//				}
				
				if(count % 200 == 0 || count == coll.size()) {
					//ִ��������
					ps.executeBatch();
					//ÿ200���ύһ������
					conn.commit();
					cflag = true;
					System.out.println("Ŀǰ�� "+count+" �����ݲ���ɹ�!");
				}
			}
			System.out.println("psִ�д���Ϊ: " + count + "��");
			//conn.commit();
			System.out.println("�ύ����ɹ���ȫ�����ݳɹ�д��!");
		} catch (Exception e) {
			System.out.println("������ " + count + "������ʱ�������쳣");
			e.printStackTrace();
		
			//�ع�����
			conn.rollback();
			//��������ʣ������� ���ݵ������ļ���
			//1.��ȡʵ��������ݵ�����
			int num = count % 200;
			if(num != 0) {
				//ʵ��д�����ݿ����Ŀ
				count = count - num;
			}else {
				count = count - 200;
			}
			System.out.println("ʵ����������� " + count + " ��!");
			
			//b.���������ݷ����¼���
			List<Environment> list2 = new ArrayList<>();
			for(int i = 0;i < count;i++) {
				Environment en = list.get(i);
				list2.add(en);
			}
			//c.�ϼ�����ɾ���Ѿ���������
			list.removeAll(list2);
			System.out.println("��δ��������� " + list.size() + " ��!");
			System.out.println("��ʼ����...");

			//d.���ݵ������ļ���
			bu.backup("src/backUp/dbBackUp", list);
			System.out.println("�������");
		} finally {
			if(ps != null)
				ps.close();
			
			if(conn != null)
				conn.close();
		}
	}
}
