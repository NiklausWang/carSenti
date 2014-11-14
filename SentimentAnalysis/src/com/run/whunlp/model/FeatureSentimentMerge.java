package com.run.whunlp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class FeatureSentimentMerge {

	private Vector<TaggerPoint> allTagger = new Vector<TaggerPoint>();
	private String str;
	private List<Integer> preLink = new ArrayList<Integer>(); // 保存特征词-情感词或情感词-特征词的连接对
	private HashMap<Integer, Double> distances = new HashMap<Integer, Double>();

	private static HashMap<String, Double> punctuationCost;
	private static final double maxLen = 30;

	static {
		punctuationCost = new HashMap<String, Double>();
		punctuationCost.put("。", 30.0);
		punctuationCost.put("！", 30.0);
		punctuationCost.put("!", 30.0);
		punctuationCost.put("？", 20.0);
		punctuationCost.put("?", 20.0);
		punctuationCost.put(";", 20.0);
		punctuationCost.put("；", 20.0);
		punctuationCost.put("：", 15.0);
		punctuationCost.put(":", 15.0);
		punctuationCost.put("，", 10.0);
		punctuationCost.put(",", 10.0);
		punctuationCost.put("、", 5.0);
	}

	public FeatureSentimentMerge() {

	}

	public FeatureSentimentMerge(String str, Vector<TaggerPoint> tagger) {

		allTagger.addAll(tagger);
		Collections.sort(allTagger);
		this.str = str;

		int i, taggerNum;
		// 添加所有的T-S,S-T连接到preLink
		for (i = 0, taggerNum = allTagger.size(); i < taggerNum - 1; i++) {
			if ((allTagger.get(i).getPolarity() == 2 && allTagger.get(i + 1).getPolarity() != 2)
					|| (allTagger.get(i).getPolarity() != 2 && allTagger.get(i + 1).getPolarity() == 2)) {
				preLink.add(i);
				distances.put(i, distanceCount(i));
			}
		}
	}

	/**
	 * 计算连接的距离
	 * 
	 * @param i
	 *            连接点
	 * @return
	 */
	private double distanceCount(int i) {
		List<Character> charFlags = new ArrayList<Character>();
		double dis = 0.0;
		int endpo = allTagger.get(i + 1).getStart();
		int startpo = allTagger.get(i).getEnd();
		if(endpo < startpo){
			return 40.0;
		}
		dis = endpo - startpo;
		String temp = str.substring(startpo, endpo);
		for (Character c : temp.toCharArray()) {
			if (punctuationCost.containsKey(c.toString())) {
				if (charFlags.contains(c)) { // 如果已经出现过一次该标点，则下一次距离乘以5，基本排除连接的可能性
					dis += punctuationCost.get(c.toString()) * 5;
				} else { // 如果没有包含，则赋予标点距离值
					dis += punctuationCost.get(c.toString());
					charFlags.add(c);
				}
			}
		}
		return dis;
	}

	public Vector<MergePoint> getMatch() {
		Vector<MergePoint> matches = new Vector<MergePoint>();
		int maxkeys = allTagger.size();
		Set<Integer> diskeySet = distances.keySet();
		Set<Integer> toRemove = new HashSet<Integer>();
		for (int i : diskeySet) { // 去掉大于长度上限的特征词-情感词连接
		// System.out.println("distance: " + i + " " + distances.get(i));
			if (distances.get(i) > maxLen) {
				toRemove.add(i);
			}
		}
		for (int r : toRemove) {
			distances.remove(r);
		}
		for (int i = 0; i < maxkeys; i++) {
			if (i == 0 && distances.containsKey(i) // 连接i为0，且单独，则加入
					&& !distances.containsKey(i + 1)) {
				// System.out.println(">>>>" + i);
				matches.add(linkMatch(i));
			} else if (i > 0 && distances.containsKey(i) // 连接i不为零，且单独，则加入
					&& !distances.containsKey(i - 1) && !distances.containsKey(i + 1)) {
				// System.out.println(">>>>" + i);
				matches.add(linkMatch(i));
			} else if (i == 0 && distances.containsKey(i) && distances.containsKey(i + 1)) { // 连接i为0，且为连续连接的开始
				// System.out.println("crossLink: " + i);
				matches.addAll(crossLinkMatch(i));
			} else if (i > 0 && distances.containsKey(i + 1) && !distances.containsKey(i - 1)) {// 连接i不为0，且为连续连接的开始
				// System.out.println("crossLink: " + i);
				matches.addAll(crossLinkMatch(i));
			}
		}
		return matches;
	}

	/**
	 * 输入为特征词情感词交替的一条连接开始位置，输出最优的匹配 例如：T1-1-S1-33-T2-3-S2,输出T1-S1，T2-S2
	 * 
	 * @param begin
	 * @return
	 */
	private Vector<MergePoint> crossLinkMatch(int begin) {
		Vector<MergePoint> crossMatches = new Vector<MergePoint>();
		int endIndex = 0, i = begin;
		while (distances.containsKey(i)) {
			i++;
		}
		endIndex = i;
		List<Integer> linkOne = new ArrayList<Integer>();
		List<Integer> linkTwo = new ArrayList<Integer>();
		double sumOne = 0;
		double sumTwo = 0;

		for (i = begin; i < endIndex; i++) {
			if (i % 2 == 0) { // 偶数存入linkOne
				linkOne.add(i);
				sumOne += distances.get(i);
			} else if (i % 2 == 1) {
				linkTwo.add(i);
				sumTwo += distances.get(i);
			}
		}

		if (linkOne.size() > 0 && linkTwo.size() > 0) {
			sumOne = sumOne / linkOne.size();
			sumTwo = sumTwo / linkTwo.size();
			// System.out.println(linkOne.size() + " " + linkTwo.size());
			// System.out.println(sumOne + " " + sumTwo);
			if (sumOne > sumTwo) { //link1均值大于link2均值的情况下，如果link1节点多，则设定阈值；如果link1节点不多于link2，则选择link2
				if (linkOne.size() > linkTwo.size() && (sumOne*linkOne.size() - sumTwo*linkTwo.size()) <= 13) {
					for (int j : linkOne) {
						crossMatches.add(linkMatch(j));
					}
				} else {
					for (int j : linkTwo) {
						crossMatches.add(linkMatch(j));
					}
				}
			} else if (sumOne < sumTwo) {
				if (linkTwo.size() > linkOne.size() && (sumTwo*linkTwo.size() - sumOne*linkOne.size()) <= 13) {
					for (int j : linkTwo) {
						crossMatches.add(linkMatch(j));
					}
				} else {
					for (int j : linkOne) {
						crossMatches.add(linkMatch(j));
					}
				}
			} else if (sumOne == sumTwo) {
				if (linkOne.size() >= linkTwo.size()) {
					for (int j : linkOne) {
						crossMatches.add(linkMatch(j));
					}
				} else {
					for (int j : linkTwo) {
						crossMatches.add(linkMatch(j));
					}
				}
			}
		} else if (linkOne.size() > 0 && linkTwo.size() == 0) {
			for (int j : linkOne) {
				crossMatches.add(linkMatch(j));
			}
		} else if (linkTwo.size() > 0 && linkOne.size() == 0) {
			for (int j : linkTwo) {
				crossMatches.add(linkMatch(j));
			}
		}
		return crossMatches;
	}

	/**
	 * 将特征词和情感词的一个连接加入匹配
	 * 
	 * @param i
	 * @return
	 */
	private MergePoint linkMatch(int i) {
		MergePoint mp = new MergePoint();
		TaggerPoint feaTagger = new TaggerPoint();
		TaggerPoint senTagger = new TaggerPoint();
		if (allTagger.get(i).getPolarity() == 2) {
			feaTagger = allTagger.get(i);
			senTagger = allTagger.get(i + 1);
		} else if (allTagger.get(i + 1).getPolarity() == 2) {
			feaTagger = allTagger.get(i + 1);
			senTagger = allTagger.get(i);
		}
		mp.setFeature(feaTagger);
		mp.setSentiment(senTagger);
		mp.setCost(distances.get(i));

		return mp;
	}

}
