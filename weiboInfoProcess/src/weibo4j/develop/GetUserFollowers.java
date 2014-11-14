package weibo4j.develop;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import weibo4j.Friendships;
import weibo4j.Users;
import weibo4j.model.WeiboException;

public class GetUserFollowers {
	private static String userFollowersPath;
	
	/***
	 * 在线爬取用户关注人信息并写入文件
	 * @param accessToken 
	 *        取得的access_token
	 * @param uid 
	 *        用户uid
	 */
	public static void writeToFile(String accessToken, String uid, int FollowersCount){
		Friendships fm = new Friendships(accessToken);
		String userFollowers = new String();
		
		try {
			//String[] ids = fm.getFriendsIdsByUid(uid, 10);   //在调用频次有限时，取10条测试
			List<String> ids = fm.getAllFollowersIdsByUid(uid, 5000);
			for(String s : ids){
				System.out.println(s);
				Users um = new Users(accessToken);
				userFollowers += um.showUserById(s).toString();
			}
			OutputStreamWriter osr = new OutputStreamWriter(
					new FileOutputStream(userFollowersPath),"UTF-8"); 
			osr.write(userFollowers);
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
	public static String getUserFollowersPath(){
		return userFollowersPath;
	}
	
	/***
	 * 设置用户关注信息存放路径
	 * @param path
	 *         用户关注信息存放路径
	 */
	public static void setUserFollowersPath(String path) {
		userFollowersPath = path;
	}
}
