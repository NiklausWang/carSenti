package weibo4j.develop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

public class UserTimelines {
	private String uid;
	private int statusCount;
	private Date sinceTime;
	private Date maxTime;
//	private List<String> statusIdList = new ArrayList<String>();
	private String userTimes = "";
	private String userTimelineCount = "";

	public UserTimelines(String uid, Date sinceTime, Date maxTime) {
		this.uid = uid;
		this.sinceTime = sinceTime;
		this.maxTime = maxTime;
	}

	public void getTimeline(String accessToken) throws WeiboException {
		Timeline tm = new Timeline(accessToken);
		// String timeline = "";
		if (statusCount > 0) {
			userTimes = userTimes.concat("[");
			List<String> statusList = tm.getAllUserTimelineIdsByUid(uid,
					statusCount); // 自定义的方法，取所有微博id
			List<String> noRepeatList = new ArrayList<String>();
			for(String sid:statusList){
				if(!noRepeatList.contains(sid)){
					noRepeatList.add(sid);
				}
			}
			for (String statusid : noRepeatList) {
				Status st = tm.showStatus(statusid);
				Date creatDate = st.getCreatedAt(); // 取出每条微博的创建时间
				// System.out.println(st);
				// 如果微博创建时间符合设定的时间段，则加入
				if (creatDate != null && creatDate.after(sinceTime)
						&& creatDate.before(maxTime)) {
//					statusIdList.add(statusid);
					userTimes = userTimes.concat(st.toString() + ","); // 用户微博字符串
//					String readInfo = tm.getReadCount(statusid).toString();
//					userTimelineCount = userTimelineCount + "readCount{\r\n"
//							+ readInfo + "}\r\n";// 用户微博阅读量字符串
				}
			}

			if (userTimes.endsWith(",")) {
				userTimes = userTimes.substring(0, userTimes.length() - 1);
			}

			userTimes = userTimes.concat("]");
		}

	}

	public int getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(int statusCount) {
		this.statusCount = statusCount;
	}

	public Date getSinceTime() {
		return sinceTime;
	}

	public void setSinceTime(Date sinceTime) {
		this.sinceTime = sinceTime;
	}

	public Date getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(Date maxTime) {
		this.maxTime = maxTime;
	}

	public String getUserTimes() {
		return userTimes;
	}

	public void setUserTimes(String userTimes) {
		this.userTimes = userTimes;
	}

	public String getUserTimelineCount() {
		return userTimelineCount;
	}

	public void setUserTimelineCount(String userTimelineCount) {
		this.userTimelineCount = userTimelineCount;
	}

}
