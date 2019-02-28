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

//select * from student where grade > '1' 
public class SelectX {
	public static void SelectX(String s) throws IOException, ClassNotFoundException {
		// select * from student where grade > '1'
		String table_name = "";
		String s_analys = "select \\* from (.+) where (.+) (.+) (.+)";
		String property = "";// 属性
		String operator = "";// 运算符
		String prop_value = "";// 属性值
		String line = "";
		String result = "";
		int weizhi = -1, ssn;

		Pattern p = Pattern.compile(s_analys);
		Matcher m = p.matcher(s);
		// select * from student where grade > 1
		while (m.find()) {
			table_name = m.group(1).toString();
			property = m.group(2).toString();
			operator = m.group(3).toString();
			prop_value = m.group(4).substring(1, m.group(4).length() - 1);
		}
		ssn = Integer.parseInt(prop_value);
		// System.out.println(table_name + " " + property + " " + operator + " " +
		// prop_value);
		File file = new File(table_name + ".txt");
		/*
		 * 针对b+树
		 */
		if (file.exists()) {
			File treeFile = new File("BplusTreeOf" + table_name + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(treeFile));
			BplusTree bplusTree = (BplusTree) objectInputStream.readObject();
			objectInputStream.close();
			List<Object> list = bplusTree.getProperties();
			String property2 = "";
			for (Object l : list) {
				property2 += (String) l;
			}
			String[] properties2 = property2.split(" ");// 得到原来的属性
			Integer position = -1;
			for (int i = 0; i < properties2.length; i++) {
				if (property.equals(properties2[i].replaceAll("\\(.*?\\)", ""))) {
					position = i;
				}
			}
			// where后属性与主键不一样
			if (!property.equals((String) bplusTree.getKeyProperty())) {
				// 新建树，以grade为key，考虑grade重复，value值为list，list装原来的key
				BplusTree bplusTree2 = new BplusTree(6);
				Node next = bplusTree.getHead();
				System.out.println(list);
				while (true) {
					if (next == null)
						break;
					ArrayList<Entry<Comparable, Object>> arr = (ArrayList) next.getEntries();
					for (int i = 0; i < arr.size(); i++) {
						ArrayList<Object> arr2 = (ArrayList<Object>) arr.get(i).getValue();
						Integer value=Integer.parseInt((String) arr2.get(position));
						if (operator.equals("=")) {
							if (value == ssn) {//ssn为对比
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else if (operator.equals(">=")) {
							if (value >= ssn) {
								//System.out.println(arr2);
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else if (operator.equals("<=")) {
							if (value <= ssn) {
								//System.out.println(arr2);
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else if (operator.equals("<")) {
							if (value < ssn) {
								//System.out.println(arr2);
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else if (operator.equals(">")) {
							if (value > ssn) {
								//System.out.println(arr2);
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else if (operator.equals("!=")) {
							if (value != ssn) {
								//System.out.println(arr2);
								String key=(String)arr2.get(bplusTree.getKeyPositon());
								ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
								if(temp==null)temp=new ArrayList<>();
								temp.add(key);
								bplusTree2.insertOrUpdate(prop_value, temp);
							}
						} else {
							//System.out.println("暂时不支持此比较符的查询");
							String key=(String)arr2.get(bplusTree.getKeyPositon());
							ArrayList<Object>temp=(ArrayList<Object>)bplusTree2.get(prop_value);
							if(temp==null)temp=new ArrayList<>();
							temp.add(key);
							bplusTree2.insertOrUpdate(prop_value, temp);
						}
					}
					next = next.getNext();
				}
				Node next2=bplusTree2.getHead();
				//提取主键值
				LinkedList<Object>linkedList=new LinkedList<>();
				while(true) {
					if(next2==null)break;
					ArrayList<Entry<Comparable, Object>> arr = (ArrayList) next2.getEntries();
					for (int i = 0; i < arr.size(); i++) {
						ArrayList<Object> arr2 = (ArrayList<Object>) arr.get(i).getValue();
						for(int k=0;k<arr2.size();k++) {
							//获取所有主键后存于linkedlist
							linkedList.add(arr2.get(k));
						}
					}
					next2=next2.getNext();
				}
				for(Object o:linkedList) {
					System.out.println(bplusTree.get((String)o));
				}
				//System.out.println(list);
			} else {
				// 与主键属性值相同
				Node next = bplusTree.getHead();
				System.out.println(list);
				while (true) {
					if (next == null)
						break;
					ArrayList<Entry<Comparable, Object>> arr = (ArrayList) next.getEntries();
					for (int i = 0; i < arr.size(); i++) {
						ArrayList<Object> arr2 = (ArrayList<Object>) arr.get(i).getValue();
						if (operator.equals("=")) {
							if (Integer.parseInt((String) arr2.get(position)) == ssn) {
								System.out.println(arr2);
							}
						} else if (operator.equals(">=")) {
							if (Integer.parseInt((String) arr2.get(position)) >= ssn) {
								System.out.println(arr2);
							}
						} else if (operator.equals("<=")) {
							if (Integer.parseInt((String) arr2.get(position)) <= ssn) {
								System.out.println(arr2);
							}
						} else if (operator.equals("<")) {
							if (Integer.parseInt((String) arr2.get(position)) < ssn) {
								System.out.println(arr2);
							}
						} else if (operator.equals(">")) {
							if (Integer.parseInt((String) arr2.get(position)) > ssn) {
								System.out.println(arr2);
							}
						} else if (operator.equals("!=")) {
							if (Integer.parseInt((String) arr2.get(position)) != ssn) {
								System.out.println(arr2);
							}
						} else {
							System.out.println("暂时不支持此比较符的查询");
						}
					}
					next = next.getNext();
				}
			}
		}
		/*
		 * 普通文件
		 */
		if (file.exists())

		{
			BufferedReader bf = new BufferedReader(new FileReader(file));
			bf.readLine();// 读取第一行
			line = bf.readLine();
			String[] x = line.split(" ");
			System.out.println(line);
			for (int i = 0; i < x.length; i++) {
				if (property.equals(x[i].replaceAll("\\(.*?\\)", ""))) {
					weizhi = i;
				}
			}

			while ((line = bf.readLine()) != null) {
				String[] y = line.split(" ");
				if (operator.equals("=")) {
					if (Integer.parseInt(y[weizhi]) == ssn) {
						result += line + "\r\n";
					}
				} else if (operator.equals(">=")) {
					if (Integer.parseInt(y[weizhi]) >= ssn) {
						result += line + "\r\n";
					}
				} else if (operator.equals("<=")) {
					if (Integer.parseInt(y[weizhi]) <= ssn) {
						result += line + "\r\n";
					}
				} else if (operator.equals("<")) {
					if (Integer.parseInt(y[weizhi]) < ssn) {
						result += line + "\r\n";
					}
				} else if (operator.equals(">")) {
					if (Integer.parseInt(y[weizhi]) > ssn) {
						result += line + "\r\n";
					}
				} else if (operator.equals("!=")) {
					if (Integer.parseInt(y[weizhi]) != ssn) {
						result += line + "\r\n";
					}
				} else {
					System.out.println("暂时不支持此比较符的查询");
				}
			}
			System.out.println(result);
		} else {
			System.out.println("该表不存在");
		}
	}
}
