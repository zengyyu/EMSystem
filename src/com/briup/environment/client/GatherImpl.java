package com.briup.environment.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationAWare;
import com.briup.environment.util.Log;
import com.briup.environment.util.LogImpl;

/*
 *	Gather采集接口的具体实现类
 *		实现gather() 采集方法 
 */
public class GatherImpl implements Gather,ConfigurationAWare {

	//采集文件路径
	private String filePath;
	
	//替换日志输出代码
	//private Log logger = new LogImpl();
	private Log logger;
	
	@Override
	public void setConfiguration(Configuration con) throws Exception {
		logger = con.getLogger();
	}
	
	@Override
	public void init(Properties properties) throws Exception {
		filePath = properties.getProperty("filePath");
	}
	
	@Override
	public Collection<Environment> gather() throws Exception {
		//1.建立IO流对象 ，关联本地文件
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		
		//2.逐行读取文件内容
		Collection<Environment> list = new ArrayList<>();
		
		//
		//System.out.println("开始逐行读取文件内容");
		logger.info("开始逐行读取文件内容");
		String line;
		while((line = br.readLine()) != null) {
			//3.拆分每一行数据，额外提取第四项，第七项
			String[] arr = line.split("[|]");
			
			//a. 数据类型 数据原始值
			String type = arr[3];
			String date = arr[6];
			
			//b. 创建 类对象，存储公共值
			Environment en = new Environment();
			//en.setName("环境名");
			en.setSrcId(arr[0]);
			en.setDstId(arr[1]);
			en.setDevId(arr[2]);
			en.setSersorAddress(type);
			int count = Integer.parseInt(arr[4]);
			en.setCount(count);
			en.setCmd(arr[5]);
			en.setStatus(Integer.parseInt(arr[7]));
			//设置最终环境值
			//en.setData(fw);
			//设置时间戳
			Timestamp t = new Timestamp(Long.parseLong(arr[8]));
			en.setGather_date(t);
			
			//4.根据第四项，处理第七项，得到实际 采集值
			if("16".equals(type)) {
				//温湿度
				String w = date.substring(0, 4);
				String s = date.substring(4,8);
				
				//将温湿度转换成 整形数
				int iw = Integer.valueOf(w, 16);
				int is = Integer.parseInt(s, 16);
				
				//获取最终float类似的温湿度值
				float fw = (float)(iw*0.00268127 - 46.85);
				float fs = (float)(is*0.00190735 - 6);
				
				//c. 将温度特有的值 添加到对象中
				en.setName("温度");
				en.setData(fw);
				
				//【特殊】将湿度封装成对象也添加到集合中
				Environment en2 = new Environment();
				en2.setName("湿度");
				en2.setSrcId(arr[0]);
				en2.setDstId(arr[1]);
				en2.setDevId(arr[2]);
				en2.setSersorAddress(type);
				en2.setCount(Integer.parseInt(arr[4]));
				en2.setCmd(arr[5]);
				en2.setStatus(Integer.parseInt(arr[7]));
				//设置最终湿度值
				en2.setData(fs);
				//设置时间戳
				en2.setGather_date(new Timestamp(Long.parseLong(arr[8])));
				list.add(en2);
			}else if("256".equals(type)) {
				//光照强度
				//提取前四个字符 
				String g = date.substring(0, 4);
				//转换成int -->  float
				float fg = Integer.parseInt(g,16);
				
				//设置光照name值 和 具体环境值
				en.setName("光照强度");
				en.setData(fg);
			}else if("1280".equals(type)) {
				//二氧化碳
				String e = date.substring(0, 4);
				float fe = Integer.parseInt(e,16);
				
				en.setName("二氧化碳");
				en.setData(fe);
			}
			//最终添加到集合
			list.add(en);
		}
		logger.info("解析文件成功，采集到的对象有: " + list.size());
		
		//6.返回集合
		return list;
	}
	
}




