package com.run.whunlp.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.run.rhea.core.IdentifyImpl;
import com.run.rhea.model.Feature;

public class DictCheck {
	public static void main(String args[]) throws IOException {
		checkContain("dict\\sentiment");
		// test();
		// checkRepeat("beijingDict",true);
	}

	public static void test() {
		System.out.println("测试****************");
		IdentifyImpl idfea = new IdentifyImpl();
		List<Feature> featureList = idfea.getFeatures("节温器的外观经典");
		Collections.sort(featureList);
		if (featureList.size() > 0) {
			for (Feature ft : featureList) {
				System.out.println("匹配特征词 : " + ft.getFeatureName());
			}
		}
	}

	public static void checkRepeat(String path, boolean useUTF8)
			throws IOException {
		File generalDict = new File(path);
		FileInputStream fis = new FileInputStream(generalDict);
		InputStreamReader isr;
		if (useUTF8) {
			isr = new InputStreamReader(fis, "UTF-8");
		} else {
			isr = new InputStreamReader(fis);
		}
		BufferedReader br = new BufferedReader(isr);

		String temp = "";
		List<String> SentimentWordsList = new ArrayList<String>();
		List<String> polarityList = new ArrayList<String>();
		while ((temp = br.readLine()) != null) {
			// System.out.println(temp);
			String[] tmp = temp.split("\t");
			if (!SentimentWordsList.contains(tmp[0])) {
				SentimentWordsList.add(tmp[0]);
				polarityList.add(tmp[1]);
			}
		}
		br.close();

		int j = 0;
		for (j = 0; j < SentimentWordsList.size(); j++) {
			System.out.println((j + 1) + " " + SentimentWordsList.get(j) + "\t"
					+ polarityList.get(j));
		}

		String noRepeatResult = "";
		for (int i = 0; i < SentimentWordsList.size(); i++) {
			noRepeatResult += (SentimentWordsList.get(i) + "\t"
					+ polarityList.get(i) + "\r\n");
		}
		// useUTF8 = true;
		if (useUTF8) {
			writeUTF8(noRepeatResult.trim(), path, false);
		} else {
			writeTofile(noRepeatResult.trim(), path, false);
		}

	}

	public static void checkContain(String path) throws IOException {
		File generalDict = new File(path);
		FileInputStream fis = new FileInputStream(generalDict);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		String temp = "";
		while ((temp = br.readLine()) != null) {
			String[] stmp = temp.split("\t");
			// System.out.println(stmp[0]);
			IdentifyImpl idfea = new IdentifyImpl();
			List<Feature> featureList = idfea.getComplexFeatures("节温器的"
					+ stmp[0]);
			Collections.sort(featureList);
			if (featureList.size() > 1) {
				System.out.println("找到与特征词有相交的情感词 : " + stmp[0]);
				for (int j = 1; j < featureList.size(); j++) {
					System.out.println("匹配特征词 : "
							+ featureList.get(j).getFeatureName());
				}
			}
		}

		br.close();
	}

	public static void writeUTF8(String text, String path, boolean append)
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

	public static void writeTofile(String text, String path, boolean append)
			throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream os = new FileOutputStream(f, append);
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(text);
		bw.close();
	}

}
