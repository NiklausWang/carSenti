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
import java.util.List;
import java.util.Scanner;

public class FeatureClass {
	public static List<String> loadTerms(String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}
		FileInputStream fs = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String temp = "";
		List<String> featureList = new ArrayList<String>();
		while ((temp = br.readLine()) != null) {
			String[] feaList = temp.split("\\|");
			for (String s : feaList) {
				featureList.add(s);
			}
		}
		br.close();

		return featureList;
	}

	public static void writeTofile(String text, String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream os = new FileOutputStream(f, true);
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(text);
		bw.close();
	}

	public static void main(String args[]) throws IOException {
		List<String> features = loadTerms("Feature");
		List<String> featuresdone = loadTerms("featureDone");
		// int i = 1;
		// for(String t:features){
		// System.out.println(i + " " + t);
		// i++;
		// }
		System.out.println("特征总数为： " + features.size() + "已完成： "
				+ featuresdone.size());
		Scanner sc = new Scanner(System.in);
		for (String s : features) {
			if (featuresdone.contains(s)) {
				continue;
			}
			System.out.println("类别：1.安全事故；2.厂商策略；3.车型配置；4.车型设计；5.车型质量；6.经销商服务");
			System.out.println("特征词： " + s);
			int feaclass = sc.nextInt();
			switch (feaclass) {
			case 1:
				writeTofile((s + "|"), "class1");
				break;
			case 2:
				writeTofile((s + "|"), "class2");
				break;
			case 3:
				writeTofile((s + "|"), "class3");
				break;
			case 4:
				writeTofile((s + "|"), "class4");
				break;
			case 5:
				writeTofile((s + "|"), "class5");
				break;
			case 6:
				writeTofile((s + "|"), "class6");
				break;
			}
			writeTofile((s + "|"), "featureDone");
		}

		sc.close();

	}
}
