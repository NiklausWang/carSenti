package weibo4j.develop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

/***
 * 输入用户id列表文件名初始化用户id列表
 * 
 * @author fans
 *
 */
public class ReadUserList {
	private int userCount = 0;
	private List<String> userIds = new LinkedList<String>();

	public ReadUserList(String userListFile) {
		File listFile = new File(userListFile);
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(listFile));
			String tempString = null;
			int line = 0;
			while ((tempString = bf.readLine()) != null) {
				line++;
				System.out.println("user " + line + ": " + tempString);
				userIds.add(tempString);
			}
			userCount = line;
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e1) {
				}
			}
		}

	}

	public void addUser(String uid) {
		userIds.add(uid);
	}

	public boolean inList(String uid) {
		return userIds.contains(uid);
	}

	public List<String> getUserIdList() {
		return userIds;
	}

	public int getUserCount() {
		return userCount;
	}

	public void writeToFile(String path) throws IOException {
		String updateUserList = new String();
		for (String uid : userIds) {
			updateUserList += new String(uid + "\r\n");
		}
		OutputStreamWriter opr = new OutputStreamWriter(new FileOutputStream(
				path), "UTF-8");
		opr.write(updateUserList);
		opr.flush();
		opr.close();

	}

	public void removeOne(String uid) {
		userIds.remove(uid);
		userCount--;
	}

	public void add(String uid) {
		userIds.add(uid);

	}
}
