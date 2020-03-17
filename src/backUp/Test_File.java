package backUp;

import java.util.ArrayList;
import java.util.List;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Backup;
import com.briup.environment.util.BackupImpl;

public class Test_File {
	
	public static void main(String[] args) throws Exception {
		Backup bu = new BackupImpl();
		List<Environment> list = 
				(List<Environment>) bu.load("src/backUp/dbBackUp");
		System.out.println("list.size: " + list.size());
	}
	
	public static void main3(String[] args) {
		List<String> list = new ArrayList<>();
		list.add("hello");
		list.add("hello");
		list.add("world");
		list.add("a");
		list.add("b");
		list.add("c");
		
		//�Ӵ�ļ����У���ȡһ��������һ���¼���[0,3)
		List<String> subList = list.subList(0, 3);
		System.out.println(subList);
	}
	
	public static void main2(String[] args) {
		/*File file = new File("src/backUp/dbBackUp");
		if(file.exists())
			System.out.println("�ļ�����");
		boolean b = file.delete();
		System.out.println("ɾ���ļ�: " + b);*/
		
		Backup b = new BackupImpl();
		b.deleteBackup("src/backUp/dbBackUp");
	}
}
