package weibo4j.develop;

import weibo4j.Account;
import weibo4j.Users;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

/***
 * 取用户信息类 若要修改输出显示，参见user.toString()方法
 * 
 * @author Niklaus
 * 
 */
public class UserInfo {
	private String uid = "";
	private int statusCount;

	public UserInfo(String uid) {
		this.uid = uid;
	}

	public String getUserInfo(String accessToken) throws WeiboException {
		Users um = new Users(accessToken);
		// System.out.println("accessToken " + accessToken);

		User user = um.showUserById(uid); // 调用api获取用户类型信息
		statusCount = user.getStatusesCount(); // 取用户所发微博总数，为取微博做准备
		return user.getjsonString(); // 返回符合json格式的用户信息字符串

	}

	public String getEdu(String accessToken) throws WeiboException,
			JSONException {
		Account ac = new Account(accessToken);
		String userBirthday = ac.getAge(uid); // 取用户生日信息
		String userEdu = ac.getSchool(uid); // 取用户教育信息
		String extendInfo = userBirthday + "\r\n" + userEdu; // 拼接为高级信息
		return extendInfo;
	}

	public int getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(int statusCount) {
		this.statusCount = statusCount;
	}

}
