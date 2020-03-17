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
 *	Gather�ɼ��ӿڵľ���ʵ����
 *		ʵ��gather() �ɼ����� 
 */
public class GatherImpl implements Gather,ConfigurationAWare {

	//�ɼ��ļ�·��
	private String filePath;
	
	//�滻��־�������
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
		//1.����IO������ �����������ļ�
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		
		//2.���ж�ȡ�ļ�����
		Collection<Environment> list = new ArrayList<>();
		
		//
		//System.out.println("��ʼ���ж�ȡ�ļ�����");
		logger.info("��ʼ���ж�ȡ�ļ�����");
		String line;
		while((line = br.readLine()) != null) {
			//3.���ÿһ�����ݣ�������ȡ�����������
			String[] arr = line.split("[|]");
			
			//a. �������� ����ԭʼֵ
			String type = arr[3];
			String date = arr[6];
			
			//b. ���� ����󣬴洢����ֵ
			Environment en = new Environment();
			//en.setName("������");
			en.setSrcId(arr[0]);
			en.setDstId(arr[1]);
			en.setDevId(arr[2]);
			en.setSersorAddress(type);
			int count = Integer.parseInt(arr[4]);
			en.setCount(count);
			en.setCmd(arr[5]);
			en.setStatus(Integer.parseInt(arr[7]));
			//�������ջ���ֵ
			//en.setData(fw);
			//����ʱ���
			Timestamp t = new Timestamp(Long.parseLong(arr[8]));
			en.setGather_date(t);
			
			//4.���ݵ�������������õ�ʵ�� �ɼ�ֵ
			if("16".equals(type)) {
				//��ʪ��
				String w = date.substring(0, 4);
				String s = date.substring(4,8);
				
				//����ʪ��ת���� ������
				int iw = Integer.valueOf(w, 16);
				int is = Integer.parseInt(s, 16);
				
				//��ȡ����float���Ƶ���ʪ��ֵ
				float fw = (float)(iw*0.00268127 - 46.85);
				float fs = (float)(is*0.00190735 - 6);
				
				//c. ���¶����е�ֵ ��ӵ�������
				en.setName("�¶�");
				en.setData(fw);
				
				//�����⡿��ʪ�ȷ�װ�ɶ���Ҳ��ӵ�������
				Environment en2 = new Environment();
				en2.setName("ʪ��");
				en2.setSrcId(arr[0]);
				en2.setDstId(arr[1]);
				en2.setDevId(arr[2]);
				en2.setSersorAddress(type);
				en2.setCount(Integer.parseInt(arr[4]));
				en2.setCmd(arr[5]);
				en2.setStatus(Integer.parseInt(arr[7]));
				//��������ʪ��ֵ
				en2.setData(fs);
				//����ʱ���
				en2.setGather_date(new Timestamp(Long.parseLong(arr[8])));
				list.add(en2);
			}else if("256".equals(type)) {
				//����ǿ��
				//��ȡǰ�ĸ��ַ� 
				String g = date.substring(0, 4);
				//ת����int -->  float
				float fg = Integer.parseInt(g,16);
				
				//���ù���nameֵ �� ���廷��ֵ
				en.setName("����ǿ��");
				en.setData(fg);
			}else if("1280".equals(type)) {
				//������̼
				String e = date.substring(0, 4);
				float fe = Integer.parseInt(e,16);
				
				en.setName("������̼");
				en.setData(fe);
			}
			//������ӵ�����
			list.add(en);
		}
		logger.info("�����ļ��ɹ����ɼ����Ķ�����: " + list.size());
		
		//6.���ؼ���
		return list;
	}
	
}




