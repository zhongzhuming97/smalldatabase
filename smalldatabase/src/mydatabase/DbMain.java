package mydatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class DbMain {
	// 创建
	static String sqlcreate = "create table \\w+ \\(.+\\)";
	// 插入
	static String sqlinsert = "insert into (\\w+) \\((.+)\\) values\\((.+)\\)";
	// 修改
	static String sqlupdate = "update .+set .+(where .+)?";
	// 显示数据库结构和内容
	static String sqlselect = "select \\* from (\\w+)";
	// 投影
	static String sqlselecT = "select (.+) from (\\w+)";
	// 选择
	static String sqlselecX = "select \\* from (.+) where (.+) (.+) (.+)";
	// 选择和投影
	static String sqlselecXT = "select (.+) from (.+) where (.+) (.+) (.+)";

	public static void main(String[] args) throws ClassNotFoundException {
		while (true) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String sql = br.readLine();
				if (sql.contains(";"))
					sql = sql.replace(';', ' ');
				sql = sql.trim();
				if (sql.matches("quit") || sql.matches("QUIT")) {
					System.out.println("欢迎下次使用，再见！\n");
					break;
				}
				solution(sql);
			} catch (IOException e) {

			}
		}
	}

	public static void solution(String sql) throws IOException, ClassNotFoundException {
		if (sql.matches(sqlcreate)) {
			Create.createTable(sql);
		} else if (sql.matches(sqlinsert)) {
			Insert.Insert(sql);
		} else if (sql.matches(sqlselect)) {
			SelectAll.SelectAll(sql);
		} else if (sql.matches(sqlupdate)) {
			Update.UpdateTable(sql);
		} else if (sql.matches(sqlselecT)) {
			SelectT.SelectT(sql);
		} else if (sql.matches(sqlselecX)) {
			SelectX.SelectX(sql);
		} else if (sql.matches(sqlselecXT)) {
			SelectXT.SelectXT(sql);
		} else {
			System.out.println("不支持该语句查询，请重新输入");
		}

	}
}
