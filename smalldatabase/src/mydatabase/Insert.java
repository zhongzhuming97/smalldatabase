package mydatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Insert {
	public static void Insert(String s) throws IOException, ClassNotFoundException {

		/*
		 * 测试语句：insert into student (name,id,grasw) values('houwei','2015','1')
		 *
		 */
		// 提取（）中属性的值
		String reg = "(?<=').+?(?=')";
		// 存属性的值
		String attribute = "";
		// 表名
		String name = "";
		// 按空格分割，存属性
		String[] attr = s.split(" ");
		name = attr[2];
		
		File file = new File(name + ".txt");

		// 表存在时才能插入数据
		if (file.exists()) {
			FileWriter data_dictionary = new FileWriter("data_dictionary.txt", true);
			data_dictionary.write("表" + name + "插入数据 ");
			File treeFile=new File("BplusTreeOf"+name+".txt");
			ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(treeFile));
			BplusTree bplusTree=(BplusTree)objectInputStream.readObject();
			List<Object>value=new ArrayList<>();
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(s);
			while (m.find()) {
				//得到values()括号内的内容
				attribute += m.group().toString();
				// System.out.println(attribute);
			}

			// 分割出最后插入的数据 e.g houwei 2015
			String[] result = attribute.split(",");
			FileWriter fileWriter = new FileWriter(file, true);
			fileWriter.write("\r\n");

			// 将值加入到文件中
			for (int i = 0; i < result.length; i++) {
				value.add(result[i]);
				fileWriter.write(result[i] + " ");
				data_dictionary.write(result[i] + " ");
			}
			Integer keyPosition=bplusTree.getKeyPositon();
			if(keyPosition<0) {
				keyPosition=0;//默认第一个属性为主键
				String temp=attr[3];
				String []x=temp.split(",");
				x[0]=x[0].replaceAll("\\(", "");
				bplusTree.setKeyProperty(x[0]);
				bplusTree.setKeyPositon(keyPosition);
			}
			bplusTree.insertOrUpdate(result[keyPosition], value);
			fileWriter.close();
			data_dictionary.write("\r\n");
			data_dictionary.close();
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(treeFile));
			objectOutputStream.writeObject(bplusTree);
			objectInputStream.close();
			objectOutputStream.close();
			System.out.println("插入数据成功！");
		} else {
			System.out.println("不存在此表");
		}
	}
}
