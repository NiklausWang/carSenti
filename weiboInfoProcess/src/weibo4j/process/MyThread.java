package weibo4j.process;

import java.io.IOException;

import weibo4j.develop.ProcessInfo;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

/**
 * 线程类，循环从账户资源池取账号并执行该账号的爬取
 * 
 * @author Niklaus
 */
public class MyThread implements Runnable {
	String name;
	UserPool res;
	Thread t;

	public MyThread(UserPool res, String name) {
		this.res = res;
		t = new Thread(this, name);
		t.start();
	}

	public void run() {
		Thread tn = Thread.currentThread();
		String ob = new String();
		try {
			while ((ob = res.getOne()) != null) {
				System.out.println(tn.getName() + " Get " + ob);
				@SuppressWarnings("unused")
				ProcessInfo pc = new ProcessInfo(ob);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
