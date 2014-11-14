package weibo4j.develop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.util.WeiboConfig;
import weibo4j.util.Log;

/**
 * 
 * 信息爬取类，给定一个账户，完成以下信息爬取： 用户基本信息、用户所发微博、微博阅读量、用户高级信息（年龄，教育）
 * 
 * @author Niklaus
 */
public class ProcessInfo {
	String uid = new String(); // 所要获取信息的用户id
	String savePath = ""; // 存储路径，设为二级目录，目录名分别为账号名后两位

	public ProcessInfo(String uid) throws IOException, WeiboException,
			JSONException {
		this.uid = uid;
		getInfo();
	}

	private void getInfo() throws IOException, WeiboException, JSONException {
		String sinceTime = WeiboConfig.getValue("sinceTime");
		String maxTime = WeiboConfig.getValue("maxTime");
		Date sinceDate = new Date(); // 起始时间
		Date maxDate = new Date(); // 终止时间
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

		try {
			sinceDate = dateFormat.parse(sinceTime);
			maxDate = dateFormat.parse(maxTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int uidLen = uid.length();
		if (uidLen > 2) {
			savePath = ("\\" + uid.substring(uidLen - 2, uidLen - 1) + "\\"); // 用户名倒第二位为第一层文件夹
			savePath += (uid.substring(uidLen - 1, uidLen) + "\\" + uid + ".txt"); // 用户名到第一位为第二层文件夹
		} else {
			savePath = uid + ".txt";
		}
		String profile = "profile"; // 用户信息文件夹，包含每个用户的基本信息
//		String extendProfile = "extend_profile"; // 用户教育信息以及年龄信息文件夹
		String timelinePath = "status"; // 用户微博文件夹，包含每个用户所发、在指定时间段内的微博
//		String timelineCountPath = "readCount"; // 用户微博阅读量文件夹，存放每条微博的阅读量信息
		String downloadPath = "download.txt"; // 已下载用户列表文件

		// System.out.println(sinceDate.toString());
		// System.out.println(maxDate.toString());

		String accessToken = WeiboConfig.getValue("accessToken"); // 取出accessToken
		// System.out.println("accessToken " + accessToken);

		int statusCount = 0; // 记录该uid用户所发微博总数

		Log.logInfo("Get the basic information of " + uid);
		UserInfo ui = new UserInfo(uid);
		String userBaseInfo = ui.getUserInfo(accessToken);  // 取得用户基本信息字符串，该字符串直接由json的toString方法导出
		// 取得信息写入文件夹，为避免手动关闭带来的写入重复问题，写入方式为覆盖式
		WriteFile(userBaseInfo, profile + savePath, false);

//		Log.logInfo("Get the age and education of " + uid);
//		String userEduInfo = ui.getEdu(accessToken);
		// 取得信息写入文件夹，为避免手动关闭带来的写入重复问题，写入方式为覆盖式
//		WriteFile(userEduInfo, extendProfile + savePath, false);
		statusCount = ui.getStatusCount(); // 返回用户微博总数，以便进行取微博操作

//		Log.logInfo("Get the timeline and readcount of the user " + uid);
		UserTimelines utl = new UserTimelines(uid, sinceDate, maxDate);
		utl.setStatusCount(statusCount);
		utl.getTimeline(accessToken);
		WriteFile(utl.getUserTimes(), timelinePath + savePath, false);
//		WriteFile(utl.getUserTimelineCount(), timelineCountPath + savePath, false);

		WriteFile(uid + "\r\n", downloadPath, true); // 全部获取完成之后，将已下载的用户名加入download文件，写入方式为增加式
	}

	public void WriteFile(String text, String path, boolean append)
			throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream os = new FileOutputStream(f, append);
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(text);
		bw.close();
	}
}
