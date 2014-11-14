package com.run.whunlp.develop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.run.rhea.core.IdentifyImpl;
import com.run.rhea.model.Feature;
import com.run.whunlp.dict.ObjectAnalyseByDict;
import com.run.whunlp.dict.OptiDict;
import com.run.whunlp.dict.Record;
import com.run.whunlp.model.FeatureSentimentMerge;
import com.run.whunlp.model.MergePoint;
import com.run.whunlp.model.TaggerPoint;

@SuppressWarnings("unused")
public class FeatureSentiAnalyisis {
	private String text; // 要处理的文本片段
	private List<Feature> featureList = new ArrayList<Feature>();// 特征词列表
	private List<Feature> sentimentList = new ArrayList<Feature>();// 情感词列表
	private Vector<TaggerPoint> featureVec = new Vector<TaggerPoint>();// 特征词向量，利于融合
	private Vector<TaggerPoint> sentimentVec = new Vector<TaggerPoint>();// 情感词向量，利于融合
	private final int FSdistance = 25;// 特征词、情感词之间距离的范围，如果情感词周围25个单位长度没有特征词，则去除该情感词
	private final int FSOdistance = 3;// 特征词、长度为1的情感词之间距离的范围，如果情感词周围3个单位长度没有特征词，则去除该情感词
	private final int FSTdistance = 10;// 特征词、长度为2的情感词之间距离的范围，如果情感词周围10个单位长度没有特征词，则去除该情感词

	public FeatureSentiAnalyisis(String text) throws NumberFormatException, IOException {
		this.text = text;
		IdentifyImpl idfea = new IdentifyImpl();
		featureList = idfea.getComplexFeatures(text); // 取所有的特征，相邻特征简单合并
		Collections.sort(featureList);

		sentimentVec = ObjectAnalyseByDict.getInstance().getTaggerPointByDict(text); // 依据词典取所有的情感词，基于最长匹配
		Collections.sort(sentimentVec);

		mergeNearSentiment();// 合并相邻的情感词，合并相邻以及顿号隔开的多个情感词

		for (Feature feature : featureList) {
			int start = feature.getPos();
			int end = feature.posEnd();

			TaggerPoint tp = new TaggerPoint();
			tp.setStart(start);
			tp.setEnd(end);
			tp.setPolarity(2);
			featureVec.add(tp);
		}
		Collections.sort(featureVec);

		selectNearFea(); // 保留特征词周围的情感词，去除其他情感词
		optiFeaSen(); // 对特征词、情感词去噪，噪音词默认极性为5

		for (TaggerPoint tp : sentimentVec) {
			Feature ft = new Feature();
			ft.setFeatureName(text.substring(tp.getStart(), tp.getEnd()));
			ft.setPos(tp.getStart());
			int polarity = tp.getPolarity();
			Feature polar = new Feature();
			polar.setFeatureName(Integer.toString(polarity));
			ft.setParentFeature(polar);
			sentimentList.add(ft);
		}
		Collections.sort(sentimentList);
		
		modifyFeaSen(); // 去除特征词和情感词有相交的现象

		// System.out.println("所有特征词");
		// for (TaggerPoint tp : featureVec) {
		// System.out.println(text.substring(tp.getStart(), tp.getEnd()));
		// }
		// System.out.println("************************************************\r\n"
		// + "所有情感词");
		// for (TaggerPoint tp : sentimentVec) {
		// System.out.println(text.substring(tp.getStart(), tp.getEnd()));
		// }
	}

	public static void main(String args[]) throws NumberFormatException, IOException {
		String text = "缤智的外形设计非常成功";
		FeatureSentiAnalyisis fa = new FeatureSentiAnalyisis(text);
	
		List<Record> pairList = fa.getFeaSenPairs(); //
	
		for (Record r : pairList) {
			System.out.println(r.getFeature() + "-->" + r.getSentiment() + " " + r.getPolarity());
		}
	
		System.out.println(fa.getFeatureClass());
	}

	/**
	 * 融合临近的情感词，暂时只处理两种情况： 
	 * 1.eg:外观漂亮大气，将识别的漂亮和大气融合为一个词
	 * 2.eg:使得雅阁看上去沉稳、扎实、低调。将沉稳、扎实、低调融合为一个词
	 */
	private void mergeNearSentiment() {
		int i = 0, sentiCount = sentimentVec.size();
		Vector<TaggerPoint> toRemove = new Vector<TaggerPoint>();
		for (i = 0; i < sentiCount - 1; i++) {
			if (sentimentVec.get(i).getEnd() == sentimentVec.get(i + 1).getStart()) {
				sentimentVec.get(i + 1).setStart(sentimentVec.get(i).getStart());
				toRemove.add(sentimentVec.get(i));
			} else if ((sentimentVec.get(i).getEnd() + 1) == sentimentVec.get(i + 1).getStart()) {
				if (text.substring(sentimentVec.get(i).getEnd(), sentimentVec.get(i + 1).getStart()).equals("、")) {
					sentimentVec.get(i + 1).setStart(sentimentVec.get(i).getStart());
					toRemove.add(sentimentVec.get(i));
				}
			}
		}

		sentimentVec.removeAll(toRemove);
	}

	/**
	 * 选择特征词特定范围内的情感词，去除孤立情感词：
	 * 1.如果情感词长度为1，则必须出现在某个特征词上下FSOdistance以内
	 * 2.如果情感词长度为2，则必须出现在某个特征词上下FSTdistance以内
	 * 3.如果情感词长度大于2，则必须出现在某个特征词上下FSdistance以内
	 */
	private void selectNearFea() {
		Vector<TaggerPoint> nearFeature = new Vector<TaggerPoint>();
		Vector<TaggerPoint> toRemove = new Vector<TaggerPoint>();

		for (TaggerPoint tp : sentimentVec) {
			boolean find = false;
			for (TaggerPoint t : featureVec) {
				int sentimentVecLength = tp.getEnd() - tp.getStart();
				if (sentimentVecLength > 2) {
					if (t.getEnd() <= tp.getStart() && (tp.getStart() - t.getEnd()) <= FSdistance) {
						find = true;
					} else if (t.getStart() >= tp.getEnd() && (t.getStart() - tp.getEnd()) <= FSdistance) {
						find = true;
					}
				} else if (sentimentVecLength == 2) {
					if (t.getEnd() <= tp.getStart() && (tp.getStart() - t.getEnd()) <= FSTdistance) {
						find = true;
					} else if (t.getStart() >= tp.getEnd() && (t.getStart() - tp.getEnd()) <= FSTdistance) {
						find = true;
					}
				} else if (sentimentVecLength == 1) {
					if (t.getEnd() <= tp.getStart() && (tp.getStart() - t.getEnd()) <= FSOdistance) {
						find = true;
					} else if (t.getStart() >= tp.getEnd() && (t.getStart() - tp.getEnd()) <= FSOdistance) {
						find = true;
					}
				}
			}
			if (!find) {
				toRemove.add(tp);
			}
		}

		sentimentVec.removeAll(toRemove);

	}

	/**
	 * 采用去噪词典，去除噪音情感词
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void optiFeaSen() throws NumberFormatException, IOException {
		OptiDict od = OptiDict.getInstance();
		Vector<TaggerPoint> noise = od.getTaggerPointByDict(text + ".");
		Vector<TaggerPoint> toRemove = new Vector<TaggerPoint>();
		Vector<TaggerPoint> fvtoRemove = new Vector<TaggerPoint>();
		List<Feature> fltoRemove = new ArrayList<Feature>();
		for (TaggerPoint no : noise) {
			for (TaggerPoint tp : sentimentVec) {
				if (tp.getStart() >= no.getStart() && tp.getEnd() <= no.getEnd()) {
					toRemove.add(tp);
				}
			}
			for(TaggerPoint tp : featureVec) {
				if (tp.getStart() >= no.getStart() && tp.getEnd() <= no.getEnd()) {
					fvtoRemove.add(tp);
				}
			}
			for(Feature ft: featureList){
				if(ft.getPos() >= no.getStart() && ft.posEnd() <= no.getEnd()){
					fltoRemove.add(ft);
				}
			}
		}

		sentimentVec.removeAll(toRemove);
		featureList.removeAll(fltoRemove);
		featureVec.removeAll(fvtoRemove);

	}

	/**
	 * 取得特征词、情感词的所有配对
	 * @return 特征词-情感词-极性三元组列表
	 */
	public List<Record> getFeaSenPairs() {
		List<Record> matchPairs = new ArrayList<Record>();
		Vector<TaggerPoint> taggerList = new Vector<TaggerPoint>();
		taggerList.addAll(featureVec);
		taggerList.addAll(sentimentVec);
		Collections.sort(taggerList);

		FeatureSentimentMerge mergetest = new FeatureSentimentMerge(text, taggerList);
		Vector<MergePoint> matches = mergetest.getMatch();

		for (MergePoint m : matches) {
			Record pr = new Record(text.substring(m.getFeature().getStart(), m.getFeature().getEnd()), text.substring(m.getSentiment()
					.getStart(), m.getSentiment().getEnd()), m.getSentiment().getPolarity());
			matchPairs.add(pr); //
		}
		return matchPairs;
	}

	/**
	 * 取特征词列表中出现最多的父类型
	 * @return 特征词列表中出现最多的类型
	 */
	public String getFeatureClass() {
		if (featureList.isEmpty()) {
			return "";
		}
		List<String> featureClass = new ArrayList<String>();
		List<Integer> featureCount = new ArrayList<Integer>();
		for (Feature ft : featureList) {
			String className = ft.getParentFeature().getFeatureName();
			if (featureClass.contains(className)) {
				int findex = featureClass.indexOf(className);
				featureCount.set(findex, featureCount.get(findex) + 1);
			} else {
				featureClass.add(className);
				featureCount.add(1);
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

	/**
	 * 去除情感词和特征词相交的情况
	 */
	private void modifyFeaSen() {
		List<Feature> features = new ArrayList<Feature>();
		features.addAll(featureList);
		features.addAll(sentimentList);

		Collections.sort(features);
		int startPos = 0; // 记录上一个情感词的结束位置
		for (int j = 0, feasize = features.size(); j < feasize; j++) {
//			System.out.println("当前： " + text.substring(features.get(j).getPos(),features.get(j).posEnd()));
			if (features.get(j).getPos() >= startPos) { // 如果下一个特征词或情感词开始位置比上一个词结束位置大，则无相交情况
				startPos = features.get(j).posEnd();
			} else if (features.get(j).getPos() < startPos 
					&& features.get(j).posEnd() > startPos) {// 下一个词开始位置小于startPos，结束位置大于startPos，则相交
				modifyFeature(features.get(j).getParentFeature().getFeatureName(), features.get(j).getPos(), features.get(j).posEnd(),
						startPos, features.get(j).posEnd());
				startPos = features.get(j).posEnd();
			} else if (j >= 1 // 如果下一个特征词或情感词出现在上一个词的前半部分，则取前半部分为特征词，后半部分为情感词,必须保证j-1为情感词
					&& features.get(j).getPos() == features.get(j - 1).getPos() 
					&& features.get(j).posEnd() < startPos
					&& features.get(j-1).getParentFeature().getFeatureName().matches("-*[012]")) {
				modifyFeature(features.get(j - 1).getParentFeature().getFeatureName(), features.get(j - 1).getPos(), features.get(j - 1)
						.posEnd(), features.get(j).posEnd(), startPos);
			} else if (j >= 1 // 如果下一个特征词或情感词出现在上一个词的后半部分或被包含，则去除后一个词
					&& features.get(j).getPos() >= features.get(j - 1).getPos() 
					&& features.get(j).posEnd() <= startPos) {
				remove(features.get(j));
//				features.get(j).getParentFeature().setFeatureName("drop");
			} else if (j >= 1 // 如果下一个特征词或情感词出现在上一个词的前半部分，如果j为情感词，则必须去除情感词j，例如平顺性，j-1为平顺性，j为平顺
					&& features.get(j).getPos() == features.get(j - 1).getPos() 
					&& features.get(j).posEnd() < startPos
					&& features.get(j).getParentFeature().getFeatureName().matches("-*[012]")) {
				remove(features.get(j));
			}
		}

		Collections.sort(features);
	}

	/**
	 * 修改特征词或情感，一般使之缩短
	 * 
	 * @param featureClass
	 *            要修改的词的类型，0、-1、1为情感词，2为特征词
	 * @param fstart
	 *            词原来的开始位置
	 * @param fend
	 *            词原来的结束位置
	 * @param startPos
	 *            词修改后的开始位置
	 * @param endPos
	 *            词修改后的结束位置
	 */
	private void modifyFeature(String featureClass, int fstart, int fend, int startPos, int endPos) {
		if (featureClass.matches("-*[01]")) {
			List<Feature> modiSentimentList = new ArrayList<Feature>();
			Vector<TaggerPoint> modiSentimentVec = new Vector<TaggerPoint>();

			for (Feature f : sentimentList) {
				if (f.getPos() == fstart && f.posEnd() == fend) {
					f.setPos(startPos);
					f.setFeatureName(text.substring(startPos, endPos));
				}
				modiSentimentList.add(f);
			}

			for (TaggerPoint tp : sentimentVec) {
				if (tp.getStart() == fstart && tp.getEnd() == fend) {
					tp.setStart(startPos);
					tp.setEnd(endPos);
				}
				modiSentimentVec.add(tp);
			}

			sentimentList = modiSentimentList;
			sentimentVec = modiSentimentVec;
		} else {
			List<Feature> modiFeatureList = new ArrayList<Feature>();
			Vector<TaggerPoint> modiFeatureVec = new Vector<TaggerPoint>();

			for (Feature f : featureList) {
				if (f.getPos() == fstart && f.posEnd() == fend) {
					f.setPos(startPos);
					f.setFeatureName(text.substring(startPos, endPos));
				}
				modiFeatureList.add(f);
			}

			for (TaggerPoint tp : featureVec) {
				if (tp.getStart() == fstart && tp.getEnd() == fend) {
					tp.setStart(startPos);
					tp.setEnd(endPos);
				}
				modiFeatureVec.add(tp);
			}

			featureList = modiFeatureList;
			featureVec = modiFeatureVec;
		}
	}

	/**
	 * 去除某个特定的特征词
	 * 
	 * @param feature
	 *            要去除的特征词
	 */
	private void remove(Feature feature) {
		int startPos = feature.getPos();
		int endPos = feature.posEnd();
		String parentClass = feature.getParentFeature().getFeatureName();
		if(parentClass.matches("-*[01]")){
			parentClass = "sentiment";
		}else{
			parentClass = "feature";
		}
		List<Feature> modiFeatureList = new ArrayList<Feature>();
		List<Feature> modiSentimentList = new ArrayList<Feature>();
		Vector<TaggerPoint> modiFeatureVec = new Vector<TaggerPoint>();
		Vector<TaggerPoint> modiSentimentVec = new Vector<TaggerPoint>();
		boolean fl = false;
		boolean sl = false;
		boolean fv = false;
		boolean sv = false;

		for (Feature f : featureList) {
			if (!(f.getPos() == startPos && f.posEnd() == endPos 
					&& parentClass.equals("feature"))) {
				modiFeatureList.add(f);
			}else{
				fl = true;
			}
		}

		for (Feature f : sentimentList) {
			if (!(f.getPos() == startPos && f.posEnd() == endPos
					&& parentClass.equals("sentiment"))) {
				modiSentimentList.add(f);
			}else{
				sl = true;
			}
		}

		for (TaggerPoint tp : featureVec) {
			if (!(tp.getStart() == startPos && tp.getEnd() == endPos
					&& parentClass.equals("feature"))) {
				modiFeatureVec.add(tp);
			}else{
				fv = true;
			}
		}

		for (TaggerPoint tp : sentimentVec) {
			if (!(tp.getStart() == startPos && tp.getEnd() == endPos
					&& parentClass.equals("sentiment"))) {
				modiSentimentVec.add(tp);
			}else{
				sv = true;
			}
		}
		if (fl) {
			featureList = modiFeatureList;
		}
		if (sl) {
			sentimentList = modiSentimentList;
		}
		if (fv) {
			featureVec = modiFeatureVec;
		}
		if (sv) {
			sentimentVec = modiSentimentVec;
		}

	}

	public List<Feature> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<Feature> featureList) {
		this.featureList = featureList;
	}

	public List<Feature> getSentimentList() {
		return sentimentList;
	}

	public void setSentimentList(List<Feature> sentimentList) {
		this.sentimentList = sentimentList;
	}

	public Vector<TaggerPoint> getFeatureVec() {
		return featureVec;
	}

	public void setFeatureVec(Vector<TaggerPoint> featureVec) {
		this.featureVec = featureVec;
	}

	public Vector<TaggerPoint> getSentimentVec() {
		return sentimentVec;
	}

	public void setSentimentVec(Vector<TaggerPoint> sentimentVec) {
		this.sentimentVec = sentimentVec;
	}
}
