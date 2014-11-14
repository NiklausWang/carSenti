package weibo4j.process;

import weibo4j.util.WeiboConfig;

/**
 * 
 * 主类，创建账户资源池并创建多个线程
 * 
 * @author Niklaus
 * 
 */
public class Main {
	public static void main(String args[]) throws InterruptedException {
		double beginTime = System.currentTimeMillis();
		int ThreadCount = Integer.parseInt(WeiboConfig.getValue("threads"));
		if (ThreadCount < 1) {
			ThreadCount = 40; // 默认开40个线程
		}
		String userListPath = "userlist.txt";
		String downloadPath = "download.txt";
		// 从userListPath和downloadPath创建账户资源池，从userList里面排除download.txt文件记录的已下载账号，剩下的
		// 账号为未下载账号，放入资源池
		UserPool up = new UserPool(userListPath, downloadPath);
		int userCount = up.getUserCount();
		MyThread[] thr = new MyThread[ThreadCount];
		for (int i = 0; i < ThreadCount; i++) {
			thr[i] = new MyThread(up, "T" + i);
		}

		for (int i = 0; i < ThreadCount; i++) {
			thr[i].t.join();
		}
		double endTime = System.currentTimeMillis();
		double downloadTime = (endTime - beginTime) / 1000;
		System.out.println(ThreadCount + "个线程取" + userCount + "个用户总用时为："
				+ downloadTime + "秒");
		System.out.println("平均每个用户用时" + downloadTime / userCount + "秒");
	}
}