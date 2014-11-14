package weibo4j.develop;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import weibo4j.Friendships;
import weibo4j.Users;
import weibo4j.model.WeiboException;

public class GetUserFriends {
	private static String userFriendsPath;
	
	/***
	 * 在线爬取用户关注人信息并写入文件
	 * @param accessToken 
	 *        取得的access_token
	 * @param uid 
	 *        用户uid
	 */
	public static void writeToFile(String accessToken, String uid, int friendsCount){
		Friendships fm = new Friendships(accessToken);
		String userFriends = new String();
		
		try {
			//String[] ids = fm.getFriendsIdsByUid(uid, 10);   //在调用频次有限时，取10条测试
			List<String> ids = fm.getAllFriendsIdsByUid(uid, 5000);
			for(String s : ids){
				System.out.println(s);
				Users um = new Users(accessToken);
				userFriends += um.showUserById(s).toString();
			}
			OutputStreamWriter osr = new OutputStreamWriter(
					new FileOutputStream(userFriendsPath),"UTF-8"); 
			osr.write(userFriends);
			osr.flush();
			osr.close();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
    
	/***
	 * 取用户关注信息存放路径
	 * @return String-用户关注信息存放路径
	 */
	public static String getUserFriendsPath(){
		return userFriendsPath;
	}
	
	/***
	 * 设置用户关注信息存放路径
	 * @param path
	 *         用户关注信息存放路径
	 */
	public static void setUserFriendsPath(String path) {
		userFriendsPath = path;
	}
}
