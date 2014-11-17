package com.run.whunlp.docSentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.run.rhea.model.Feature;
import com.run.whunlp.dict.Record;

public final class DocSentiment {
	private String document; // 要处理的文档
	private String brand; // 对应的汽车品牌
	private int length; // 文档长度
	private final int SENLEN = 100; // 控制切分片段长度
	private List<String> segSet = new ArrayList<String>();
	private List<String> htmlSet = new ArrayList<String>();
	private List<Record> matchPairs = new ArrayList<Record>();
	private List<Feature> featureList = new ArrayList<Feature>();
	private final boolean cutPieces = true;

	/**
	 * @param document
	 *            文档字符串
	 * @param brand
	 *            品牌字符串
	 */
	public DocSentiment(String document, String brand) {

		document = document.replaceAll("\\n+", "").replaceAll("\\s[\u4e00-\u9fa5]*?：", "")
				.replaceAll("\\s+", " ").replaceAll("&nbsp", "");
		// Pattern pt = Pattern.compile("[;��:������!?��\"����={}.*%$��@+-]{3,}");
		Pattern pt = Pattern.compile("([;；]\\s*){2,}");
		Matcher mc = pt.matcher(document);
		document = mc.replaceAll(";");
		this.document = document;
		this.brand = brand;
		this.length = this.document.length();
	}

	/**
	 * 
	 * @return 切分的带标签文本片段
	 * @throws SentimentException
	 * @throws IOException
	 */
	public List<String> SentiAnalyse() throws SentimentException, IOException {

		if (this.length == 0 || this.brand.length() == 0) {
			throw new SentimentException("文档或品牌为空");
		} else if (!this.document.contains(this.brand)) {
			System.out.println("文档不包含该品牌");
			return new ArrayList<String>();
		} else if (cutPieces == false || this.length <= 120) {
			this.segSet.add(this.document);
		} else {
			Pattern pt = Pattern.compile("[!?;！？；。\\s]+");
			int cstart = 0;
			int start = 0;
			String dcopy = this.document.substring(cstart);
			while ((start = dcopy.indexOf(this.brand)) > 0) {
				int segStart = 0;
				int segEnd = 0;
				String segMent = new String();
				String temp = dcopy.substring(0, start);
				Matcher mt = pt.matcher(temp);
				int mfind = -1;
				while (mt.find()) {
					mfind = mt.end();
				}
				if (mfind > 0 && (start - mfind) < 40) {
					segStart = mfind;
				} else if (mfind == -1 && temp.length() < 40) {
					segStart = 0;
				} else {
					segStart = start;
				}

				temp = dcopy.substring(start);
				if (temp.length() > SENLEN) {
					String stemp = temp.substring(0, SENLEN);
					Matcher mp = pt.matcher(stemp);
					int mpfind = -1;
					while (mp.find()) {
						mpfind = mp.end();
					}
					if (mpfind > 0) {
						segEnd = start + mpfind;
					} else {
						segEnd = segStart + SENLEN;
					}

				} else {
					segEnd = start + temp.length();
				}
				segMent = dcopy.substring(segStart, segEnd);
				this.segSet.add(segMent);
				cstart += segEnd;
				dcopy = this.document.substring(cstart);
			}
		}

		for (String seg : this.segSet) {
			if (seg.trim().length() == 0) {
				continue;
			}
			GetFeatureWords gfw = new GetFeatureWords(seg);

			String htmlresult = gfw.getWords();

			matchPairs.addAll(gfw.getPairs());

			featureList.addAll(gfw.getFeatureList());

			htmlresult = htmlresult.replaceAll(brand, ("<span class=\"sentiment-brand\">" + brand + "</span>"));
			if (htmlresult.endsWith(".")) {
				htmlresult = htmlresult.substring(0, htmlresult.length() - 1);
			}
			this.htmlSet.add(htmlresult);
		}

		return this.htmlSet;
	}

	/*
	 * public List<Record> getFSwords() throws IOException { Pattern pt =
	 * Pattern.compile("<[^<]+>"); for (Feature e : featureList) { String
	 * featureName = e.getFeatureName(); Matcher mt = pt.matcher(featureName);
	 * String noTags = mt.replaceAll(""); Record rc = new Record(noTags, "", 0);
	 * matchPairs.add(rc); } return matchPairs; }
	 */

	public List<Record> getFSwords() {
		return matchPairs;
	}

	/**
	 * @return String 获取文档类型
	 */
	public String getFeatureClass() {
		if (featureList.isEmpty()) {
			return "";
		}
		List<String> featureClass = new ArrayList<String>();
		List<Integer> featureCount = new ArrayList<Integer>();
		for (Feature ft : featureList) {
			String className = ft.getParentFeature().getFeatureName();
			if (!className.matches("-*[01]")) {
				if (featureClass.contains(className)) {
					int findex = featureClass.indexOf(className);
					featureCount.set(findex, featureCount.get(findex) + 1);
				} else {
					featureClass.add(className);
					featureCount.add(1);
				}
			}
		}

		int max = 1;
		for (int i : featureCount) {
			if (i > max) {
				max = i;
			}
		}
		int index = featureCount.indexOf(max);
		return featureClass.get(index);
	}
}
