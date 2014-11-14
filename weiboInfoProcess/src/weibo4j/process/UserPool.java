package weibo4j.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPool {

	List<String> userIds = new ArrayList<String>(); // 用户列表
	List<String> downloadIds = new ArrayList<String>(); // 已下载用户列表
	int userCount = 0;
	String downloadPath = new String();
	String userListPath = new String();

	public UserPool(String uPath, String dPath) {
		if (uPath == null || dPath == null) {
			System.out.println("Neither of them can be null!");
			return;
		}
		userIds = loadUser(uPath);
		downloadIds = loadUser(dPath);

		for (String s : downloadIds) { // 排除已下载的用户
			int index = userIds.indexOf(s);
			if (index == -1) {
				continue;
			} else {
				userIds.remove(index);
			}
		}
		userCount = userIds.size();
	}

	public List<String> loadUser(String path) {
		List<String> usid = new ArrayList<String>();
		File listFile = new File(path);
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(listFile));
			String tempString = null;
			while ((tempString = bf.readLine()) != null) {
				// System.out.println("user " + line + ": " + tempString);
				usid.add(tempString);
			}

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
		return usid;
	}

	/**
	 * 同步的获取账户方法，一次只能有一个线程获取一个账号，并从资源池中删除
	 * 
	 * @return 一个账户字符串
	 * @throws IOException
	 */
	synchronized public String getOne() throws IOException {
		int index = 0;
		if (userIds.isEmpty()) {
			return null;
		} else {
			String temp = userIds.get(0);
			userIds.remove(index);
			return temp;
		}
	}

	public List<String> getDownloadIds() {
		return downloadIds;
	}

	public void setDownloadIds(List<String> downloadIds) {
		this.downloadIds = downloadIds;
	}

	public String getUserListPath() {
		return userListPath;
	}

	public void setUserListPath(String userListPath) {
		this.userListPath = userListPath;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

}
