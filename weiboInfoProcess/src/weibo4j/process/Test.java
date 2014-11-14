package weibo4j.process;

import java.io.IOException;
import java.util.List;

import weibo4j.develop.ProcessInfo;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

public class Test {

	public static void main(String[] args) throws WeiboException, IOException,
			JSONException {
		UserPool up = new UserPool("userlist.txt", "download.txt");
		List<String> userList = up.getUserIds();

		String testuser = userList.get(0);
		@SuppressWarnings("unused")
		ProcessInfo pi = new ProcessInfo(testuser);
	}

}
