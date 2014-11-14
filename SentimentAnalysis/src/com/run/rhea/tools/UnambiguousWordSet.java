package com.run.rhea.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class UnambiguousWordSet {

	public static HashSet<String> wordsSet = new HashSet<String>();
	private static final Logger logger = Logger
			.getLogger(UnambiguousWordSet.class);

	// 私有的默认构造子
	private UnambiguousWordSet() {

		BufferedReader reader = null;
		try {
			InputStreamReader isr = new InputStreamReader(this.getClass()
					.getResourceAsStream("/unAmbiguousWords"), "UTF-8");
			reader = new BufferedReader(isr);
			String negString = "";
			while ((negString = reader.readLine()) != null) {
				wordsSet.add(negString);
			}
			logger.info("非歧义词加载成功！");
		} catch (IOException e) {
			throw new RuntimeException("非歧义词获取失败！", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// 注意，这里没有final
	private static UnambiguousWordSet single;

	// 只实例化一次
	static {
		single = new UnambiguousWordSet();
	}

	// 静态工厂方法
	public synchronized static UnambiguousWordSet getInstance() {
		if (single == null) {
			single = new UnambiguousWordSet();
		}
		return single;
	}

	public static void main(String args[]) throws IOException {
		UnambiguousWordSet.getInstance();
		// StringBuffer sbBuffer = new StringBuffer();
		for (String string : wordsSet) {
			System.out.println(string);
			// sbBuffer.append(string+"\r\n");
		}
		/*
		 * File file = new File("unAmbiguousWords"); if(!file.exists()){
		 * file.createNewFile(); } FileWriter fileWritter = new
		 * FileWriter(file.getName(),true); BufferedWriter bufferWritter = new
		 * BufferedWriter(fileWritter);
		 * bufferWritter.write(sbBuffer.toString()); bufferWritter.close();
		 */
	}

}
