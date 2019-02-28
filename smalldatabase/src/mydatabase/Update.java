package mydatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Update {
	public static void UpdateTable(String s) throws IOException, ClassNotFoundException {
		// update student set name=houwei,grade=3 where id=6666;
		String result = "";
		String s_analysis = "(?<=set ).+(?=;*)";
		String s_property = "(.+)( where )(.+)";
		String s_update_values = ".+,*";
		String table_name = "";// 表名
		String find = "";
		String values = "";// where前面的属性
		String values1 = "";// where后面的属性
		String[] x = s.split(" ");
		table_name = x[1];// 表名
//        System.out.println(table_name);     //student

		// 此版块实现判断是否有此表
		File file = new File(table_name + ".txt");
		if (file.exists()) {
			// 将表中b+树读取进来以便修改
			File treeFile = new File("BplusTreeOf" + table_name + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(treeFile));
			BplusTree bplusTree = (BplusTree) objectInputStream.readObject();
			objectInputStream.close();
			// 此版块实现将set后语句识别出来
			Pattern p = Pattern.compile(s_analysis);
			Matcher m = p.matcher(s);
			m.find();
			find = m.group().toString();
//            System.out.println(find);       //set id=6666,grade=3  where name=houwei
			// 实现将where语句前后属性分解出来
			Pattern p1 = Pattern.compile(s_property);
			Matcher m1 = p1.matcher(find);
			while (m1.find()) {

				values = m1.group(1); // 存要设置的新值
				values1 = m1.group(3); // 存定位到修改行的依据值
			}
//            System.out.println(values);      //id=6666,grade=3   (下标为1的那部分)
//            System.out.println(values1);     //where name=houwei (下标为3的那部分)
			// 此版块实现将需要修改的属性及其值得到
			int modifyPosition = -1;//
			String line = "", attr = "";
			String[] y = values1.split("=");
			String y_alter_property = y[0];// 需要修改的属性
			String y_alter_values = y[1];// 需要修改的属性的值
//            System.out.println(y_alter_values+"需要修改");  // houwei
			// 此版块实现获取更改的属性和值
			BufferedReader br = new BufferedReader(new FileReader(file));
			line = br.readLine();// 读取第一行
			result += line + "\r\n";
			// list.add(line+"\r\n");//添加第一行
			attr = line = br.readLine();// 读取第二行 属性
			// ===================================================
			String[] sAttr = attr.split(" "); // 保存每个属性的值
			String tmp[];
			for (int i = 0; i < sAttr.length; i++) {
				tmp = sAttr[i].split("\\(");
				sAttr[i] = tmp[0];
			}

			// 用来保存每一行的值
			ArrayList<String> list = new ArrayList<String>();
			result += line + "";
			// list.add(line+"\r\n");//添加第二行
			// 将第二行数据用空格分开
			String[] h = line.split(" ");
			for (int j = 0; j < h.length; j++) {
				// 得到定位属性的位置
				if (y_alter_property.equals(h[j].replaceAll("\\(.*?\\)", ""))) {
					modifyPosition = j;
					break;
				}
			}
			if (modifyPosition < 0) {
				System.out.println("表中无属性列 " + y_alter_property);
				br.close();
				return;
			}
			String updateValue[] = values.split(",");
			// 属性与值一一对应
			List<String> properties = new ArrayList<String>();
			List<String> vals = new ArrayList<String>();
			for (int i = 0; i < updateValue.length; i++) {
				String temp[] = updateValue[i].split("=");
				properties.add(temp[0]);
				vals.add(temp[1]);
			}
//            System.out.println("要修改的位置是："+modifyPosition);  //0  根据哪个属性要修改值得下标

			/*
			 * 针对b+树所做的
			 */
			// 筛选出修改的值的内容
			List<Object> proper = bplusTree.getProperties();
			List<Object> proper2 = new ArrayList();
			for (int i = 0; i < proper.size(); i++) {
				String[] te = ((String) proper.get(i)).split("\\(");
				// 得到树中属性名称,树中所有属性集合
				proper2.add(te[0]);
			}
			if (y_alter_property.equals((String) bplusTree.getKeyProperty())) {
				// 如果要修改where语句对应主键
				// 得到原数据
				ArrayList<Object> formerData = (ArrayList<Object>) bplusTree.get(y_alter_values);
				for (int i = 0; i < properties.size(); i++)
					for (int k = 0; k < proper2.size(); k++) {
						if (properties.get(i).equals(proper2.get(k))) {
							formerData.set(k, vals.get(i));
						}
					}
				bplusTree.insertOrUpdate(y_alter_values, formerData);
			} else {
				// 如果不是，重新建树
				Integer modifyPosition2 = -1;

				for (int i = 0; i < proper2.size(); i++) {
					if (y_alter_property.equals(proper2.get(i))) {
						modifyPosition2 = i;
					}
				}
				if (modifyPosition2 < 0) {
					System.out.println("表中无属性列 " + y_alter_property);
				}
				BplusTree secondaryKeyTree = new BplusTree(6);
				Node next = bplusTree.getHead();
				while (true) {
					if (next == null)
						break;
					List<Entry<Comparable, Object>> l = next.getEntries();
					for (int i = 0; i < l.size(); i++) {
						/*
						 * 提出value中name属性的值以及他的主键（索引值），重新以查询属性为索引建树查询
						 */
						ArrayList<Object> arrayList = (ArrayList<Object>) l.get(i).getValue();
						secondaryKeyTree.insertOrUpdate((String) arrayList.get(modifyPosition2), l.get(i).getKey());
					}
					next = next.getNext();
				}
				// 回表查询主键值，再利用主键值进行查找
				String key = (String) secondaryKeyTree.get(y_alter_values);
				// 回原来树查询，进而进行修改
				if (key == null) {
					// 如果值不存在，则退出
					System.out.println(y_alter_property + "属性列不存在值为" + y_alter_values);
					return;
				}
				ArrayList<Object> formerData = (ArrayList<Object>) bplusTree.get(key);
				// 弄反了
				for (int i = 0; i < proper2.size(); i++) {
					for (int k = 0; k < properties.size(); k++) {
						if (((String) proper2.get(i)).equals(properties.get(k))) {
							// 查询到对应属性列
							formerData.set(i, vals.get(k));// formerData此时为修改后的数据
						}
					}
				}
				// 主键值被修改
				if (formerData.get(bplusTree.getKeyPositon()) != key) {
					bplusTree.remove(key);// 先去除原数据
					bplusTree.insertOrUpdate((String) formerData.get(bplusTree.getKeyPositon()), formerData);
				} else {
					// 主键值没被修改
					bplusTree.insertOrUpdate(key, formerData);
				}
			}
			boolean modify = false;// 未更改
			while ((line = br.readLine()) != null) {
				String[] k = line.split(" ");
				if (k == null) {
				}
				// 读到的那一行的属性值为 where的属性
				else if (k[modifyPosition].equals(y_alter_values)) {
					modify = true;
					result += "\r\n";
					// 建立一个动态数组
					// 1. 将修改的值变成新的值 填入动态数组对应的位置
					// 2. 将未修改的值也存入对应位置
					// 3. 将该动态数组拼接成一个字符串（最后加\r\n)
					// 4. 接着读取下面的行，重复以上操作

					// 先将原一行原数据放入数组中
					for (int i = 0; i < k.length; i++) {
						list.add(k[i]);
					}
					// 匹配应该修改的列
					for (int i = 0; i < properties.size(); i++)
						for (int count = 0; count < sAttr.length; count++) {
							if (sAttr[count].equals(properties.get(i))) {
								list.set(count, vals.get(i));
								break;
							}
						}

					for (int i = 0; i < k.length; i++) {
						result += list.get(i) + " ";
					}
				} else {
					// 没有则按照原来情况正常写入
					result += "\r\n" + line;
				}
			}
			// 将字符串写入文件中
			if (modify) {
				System.out.println("成功执行");
				FileWriter fileWriter = new FileWriter(table_name + ".txt", false);
				fileWriter.write(result + "");
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(treeFile));
				objectOutputStream.writeObject(bplusTree);
				objectOutputStream.close();
				fileWriter.close();
			} else {
				System.out.println(y_alter_property + "属性列不存在值为" + y_alter_values);
			}

		} else {
			System.out.println("此表不存在");
		}

	}
}
