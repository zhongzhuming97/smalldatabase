package mydatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SelectAll {
	public static void SelectAll(String s) throws IOException, ClassNotFoundException {
		// 表名
		String name = "";
		// 暂存每一行的数据
		String line = "";

		String[] attribute = s.split(" ");
		// 从字符串中取出名字
		name = attribute[3];
		File file = new File(name + ".txt");
		// 文件存在
		if (file.exists()) {
			// 读数据
//			BufferedReader br = new BufferedReader(new FileReader(file));
//			// 先读一行
//			line = br.readLine();
//			// 显示一行
//			System.out.println(line);
//			while ((line = br.readLine()) != null) {
//				String[] y = line.split(" ");
//
//				for (int i = 0; i < y.length; i++) {
//					System.out.print(y[i] + "\t\t");
//				}
//				System.out.println();
//			}
			File treeFile=new File("BplusTreeOf"+name+".txt");
			ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(treeFile));
			BplusTree bplusTree=(BplusTree)objectInputStream.readObject();
			Node next=bplusTree.getHead();
			List<Object>properties=bplusTree.getProperties();
			System.out.println(properties);
			int count=0;
			while(true) {
				if(next==null)break;
				ArrayList<Entry<Comparable, Object>>list=(ArrayList)next.getEntries();
				for(int i=0;i<list.size();i++) {
					ArrayList<Object>temp=(ArrayList<Object>)list.get(i).getValue();
					System.out.println(list.get(i).getValue());
					for(int k=0;k<temp.size();k++) {
					String length=(String)temp.get(k);
					count+=length.length();
					}
					System.out.println(count);
				}
				next=next.getNext();
			}
			objectInputStream.close();
			System.out.println();
			System.out.println("Order Complete！");
		} else {
			System.out.println("此表不存在");
		}

	}

}
