package com.run.whunlp.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import com.run.whunlp.model.TaggerPoint;
import com.run.whunlp.properties.CommonParam;

public class ObjectAnalyseByDict {
	private final int maxLength = 10;
	private static ObjectAnalyseByDict oad;
	private static HashMap<String, Integer> generalDict;

	private ObjectAnalyseByDict() throws NumberFormatException, IOException {
				
		CommonParam dict = new CommonParam("sentiment");
		String dir = dict.getString("resource");
		loadGeneralDict(dir + "//sentiment");
	}

	public static ObjectAnalyseByDict getInstance()
			throws NumberFormatException, IOException {

		if (oad == null) {
			synchronized (ObjectAnalyseByDict.class) {
				if (oad == null) {
					oad = new ObjectAnalyseByDict();
				}
			}
		}
		return oad;
	}

	public Vector<TaggerPoint> getTaggerPointByDict(String str) {
		str+=".";
		Vector<TaggerPoint> res = new Vector<TaggerPoint>();
		int length = str.length();
		int i = 0;
		while (i < length) {
			int isF = -1;
			for (int j = maxLength; j > 0; j--) {
				int s = i;
				int e = i + j;
				if (e < length) {
					String w = str.substring(s, e);
					if (generalDict.containsKey(w)) {
						TaggerPoint tem = new TaggerPoint();
						tem.setStart(s);
						tem.setEnd(e);
						int p = generalDict.get(w);
						if (p > 0) {
							tem.setPolarity(1);
						} else if (p < 0) {
							tem.setPolarity(-1);
						}else{
							tem.setPolarity(0);
						}
						res.add(tem);
						i += j;
						isF = 1;
						break;
					}
				}
			}
			
			if( isF == -1 ){
				i++;
			}
		}
		return res;
	}

	
	
	/*
	 * 载入普通情感词典
	 */
	private static void loadGeneralDict(String generalDictFile)
			throws NumberFormatException, IOException {
		File file = new File(generalDictFile);
		BufferedReader br = null;
		br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file),"UTF-8"));
		generalDict = new HashMap<String,Integer>();
		generalDict.clear();
		String str_line = null;
		while ((str_line = br.readLine()) != null) {
			String[] tem = str_line.trim().split("\t");
			if (tem.length == 2) {
				generalDict.put(tem[0], Integer.parseInt(tem[1]));
//				System.out.println(tem[0] + "\t" + tem[1]);
			}
		}
		br.close();
	}
}
