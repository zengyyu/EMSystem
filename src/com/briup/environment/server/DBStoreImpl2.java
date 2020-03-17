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

	//数据库四要素
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	//*** 附加功能1: 配置文件加载四要素 ***
	//静态代码块
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

	//数据库入库 
	//*** 附加功能2: 批处理 ***
	@Override
	public void saveDb(Collection<Environment> coll) throws Exception {
		//先从备份文件中获取数据
		String backFile = "src/backUp/dbBackUp";
		//如果文件存在，且为普通文件，则导入
		Backup bu = new BackupImpl();
		List<Environment> list = (List<Environment>) bu.load(backFile);
		//将两个集合整合成一个集合，然后入库
		if(list == null) {
			list = (List<Environment>) coll;
		}else {
			list.addAll(coll);
			System.out.println("合并两个集合，准备好需要插入的数据...");
		}
		//删除备份文件
		bu.deleteBackup(backFile);
		
		Connection conn = null;
		PreparedStatement ps = null;
		//定义计数器，记录插入数据的条数
		int count = 0;
		boolean cflag = false;
		try {
			//1.注册驱动
			Class.forName(driver);
			
			//2.建立数据库连接
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			System.out.println("建立数据库连接成功,conn: " + conn);
			
			//3.获取ps对象
			String sql = "insert into e_detail_3 values(?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			
			//4.填入?值 然后执行sql语句
			System.out.println("开始插入数据...");
			System.out.println("list.size: " + list.size());
			
			for (Environment en : list) {
				//计数器加一
				count++;
				cflag = false;
				
				//将每个对象属性值 填入?中
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
				//执行sql语句
				//ps.execute();
				
				//添加到批处理
				ps.addBatch();
				
				//写入400条，发生异常
//				if(count == 340) {
//					int b = count / 0;
//				}
				
				if(count % 200 == 0 || count == coll.size()) {
					//执行批处理
					ps.executeBatch();
					//每200条提交一次事务
					conn.commit();
					cflag = true;
					System.out.println("目前有 "+count+" 条数据插入成功!");
				}
			}
			System.out.println("ps执行次数为: " + count + "次");
			//conn.commit();
			System.out.println("提交事务成功，全部数据成功写入!");
		} catch (Exception e) {
			System.out.println("当插入 " + count + "条数据时，出现异常");
			e.printStackTrace();
		
			//回滚事务
			conn.rollback();
			//将集合中剩余的数据 备份到本地文件中
			//1.获取实际入库数据的条数
			int num = count % 200;
			if(num != 0) {
				//实际写入数据库的条目
				count = count - num;
			}else {
				count = count - 200;
			}
			System.out.println("实际入库数据有 " + count + " 条!");
			
			//b.将入库的数据放入新集合
			List<Environment> list2 = new ArrayList<>();
			for(int i = 0;i < count;i++) {
				Environment en = list.get(i);
				list2.add(en);
			}
			//c.老集合中删除已经入库的数据
			list.removeAll(list2);
			System.out.println("尚未入库数据有 " + list.size() + " 条!");
			System.out.println("开始备份...");

			//d.备份到本地文件中
			bu.backup("src/backUp/dbBackUp", list);
			System.out.println("备份完成");
		} finally {
			if(ps != null)
				ps.close();
			
			if(conn != null)
				conn.close();
		}
	}
}
