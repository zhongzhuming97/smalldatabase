package mydatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//投影 select name from student
public class SelectT {
	// select grade from student
	public static void SelectT(String s) throws IOException, ClassNotFoundException {
		String table_name = "";
		String s_analys = "select (.+) from (\\w+)";// 分析出表名 属性名
		String property = "";// 属性名
		String line = "";
		String result = "";
		int weizhi = -1;
		String[] properties = null;
		Pattern p = Pattern.compile(s_analys);
		Matcher m = p.matcher(s);
		while (m.find()) {
			table_name = m.group(2).toString();
			property = m.group(1).toString();
		}
		if (property.contains(",")) {
			properties = property.split(",");
		}

		System.out.println("\t\t" + table_name);
		for (int i = 0; i < properties.length; i++) {
			System.out.print(properties[i] + "\t");
		}
		System.out.println();
		File file = new File(table_name + ".txt");
		if (file.exists()) {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			bf.readLine();// 读取第一行
			line = bf.readLine();// 读取第二行
			String[] x = line.split(" ");
			// 找到对应位置
			List<Integer> position = new LinkedList();
			for (int k = 0; k < properties.length; k++)
				for (int i = 0; i < x.length; i++) {
					if (properties[k].equals(x[i].replaceAll("\\(.*?\\)", ""))) {
						position.add(new Integer(i));
					}
				}
			if (position.size() == 0 || position.size() < properties.length) {
				System.out.println("没有对应的属性列,请检查语句");
				return;
			}
			while ((line = bf.readLine()) != null) {
				String[] y = line.split(" ");
				for (int i = 0; i < properties.length; i++) {
					result += y[position.get(i)] + "\t";
				}
				result += "\r\n";
			}
			System.out.println(result);
		} else {
			System.out.println("此表不存在");
		}

		/*
		 * 针对b+树
		 */
		if (file.exists()) {
			File treeFile = new File("BplusTreeOf" + table_name + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(treeFile));
			BplusTree bplusTree = (BplusTree) objectInputStream.readObject();
			List<Object> list = bplusTree.getProperties();
			String property2 = "";
			for (Object l : list) {
				property2 += (String) l;
			}
			String[] properties2 = property2.split(" ");// 得到原来的属性
			List<Integer> position = new LinkedList();
			for (int k = 0; k < properties.length; k++)
				for (int i = 0; i < properties2.length; i++) {
					if (properties[k].equals(properties2[i].replaceAll("\\(.*?\\)", ""))) {
						position.add(new Integer(i));
					}
				}
			if (position.size() == 0 || position.size() < properties.length) {
				System.out.println("没有对应的属性列,请检查语句");
				return;
			}
			System.out.println("\t\t" + table_name);
			for (int i = 0; i < properties.length; i++) {
				System.out.print(properties[i] + "\t");
			}
			System.out.println();
			Node next = bplusTree.getHead();
			while (true) {
				if (next == null)
					break;
				ArrayList<Entry<Comparable, Object>> arrayList = (ArrayList) next.getEntries();
				for (int i = 0; i < arrayList.size(); i++) {
					ArrayList<Object> aList = (ArrayList) arrayList.get(i).getValue();
					for (int k = 0; k < position.size(); k++) {
						System.out.print(aList.get(position.get(k)) + "\t");
					}
					System.out.println();
				}
				next = next.getNext();
			}
			objectInputStream.close();
		} else {
			System.out.println(table_name+"表不存在");
		}
	}

}
