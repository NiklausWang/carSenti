package com.run.whunlp.develop;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.run.rhea.core.IdentifyImpl;
import com.run.rhea.model.Feature;
import com.run.whunlp.model.FeatureSentimentMerge;
import com.run.whunlp.model.FeatureSentimentMergeSimple;
import com.run.whunlp.model.MergePoint;
import com.run.whunlp.model.TaggerPoint;
import com.run.whunlp.dict.ObjectAnalyseByDict;

public class wordsExtraTest {
	public static void main(String args[]) {
		test1();
	}

	public static void test() {
		String text = "四驱不到位，缺憾，不值得买，不中肯，不喜欢，档难挂，安定性较差的GS版百公里加速11.3秒缺憾，前驱的GL版明显不给力，为10.8秒。对80后来说，数据并不醒目。实际驾驶中的动力表现则令人满意。市区中颇为轻松，短距离加速挺利落。你甚至需要小心对待油门才能保证按照法定限速行驶。6挡自动变速箱与当前的君威等一样都是升级过的版本，换挡流畅许多。喜爱尽快升入高挡位的特点依旧，这是保证油耗经济性的必要手段。这意味着在大力加速时经常需要较长的降挡时间不厚道，也意味着不提供“运动”变速模式是一个缺憾。";
		IdentifyImpl idfea = new IdentifyImpl();
		List<Feature> fea = idfea.getComplexFeatures(text);
		Collections.sort(fea);

		for (Feature f : fea) {
			System.out.println(f.getFeatureName());
		}

		System.out.println("****************************");
		Vector<TaggerPoint> sentimentWordList = new Vector<TaggerPoint>();
		try {
			sentimentWordList = ObjectAnalyseByDict.getInstance()
					.getTaggerPointByDict(text);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (TaggerPoint tp : sentimentWordList) {
			System.out.println(text.substring(tp.getStart(), tp.getEnd()));
		}

	}

	public static void test1() {
		String text = "雅阁>>荣威550车型售价：9.98万-25.98万6月销量：1103台轴距尺寸：2705mm车型优点：空间较大，内饰科技感强，保养便宜车型缺点：1.8L动力偏弱，手动变速箱设计需改进销量点评：曾经的荣威主力车型，但性价比优势不如竞争对手";

		Vector<TaggerPoint> fTaggerList = new Vector<TaggerPoint>();
		Vector<TaggerPoint> sentimentWordList = new Vector<TaggerPoint>();
		Vector<TaggerPoint> taggerList = new Vector<TaggerPoint>();

		IdentifyImpl idfea = new IdentifyImpl();
		List<Feature> fea = idfea.getComplexFeatures(text);
		Collections.sort(fea);

		try {
			sentimentWordList = ObjectAnalyseByDict.getInstance()
					.getTaggerPointByDict(text);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Feature feature : fea) {
			// System.out.println(feature.getFeatureName());
			int start = feature.getPos();
			int end = feature.posEnd();

			TaggerPoint tp = new TaggerPoint();
			tp.setStart(start);
			tp.setEnd(end);
			tp.setPolarity(2);
			fTaggerList.add(tp);
			System.out.println("添加一个特征词: "
					+ text.substring(tp.getStart(), tp.getEnd()));
		}
		Collections.sort(fTaggerList);

		for (TaggerPoint tp : sentimentWordList) {
			System.out.println("找到情感词： "
					+ text.substring(tp.getStart(), tp.getEnd()));
		}

		taggerList.addAll(sentimentWordList);
		taggerList.addAll(fTaggerList);
		Collections.sort(taggerList);
		
		System.out.println(text);
		for(TaggerPoint tp:taggerList){
			System.out.print(text.substring(tp.getStart(),tp.getEnd()) + " ");
		}
		FeatureSentimentMerge mergetest = new FeatureSentimentMerge(
				text, taggerList);
		Vector<MergePoint> matches = mergetest.getMatch();

		for (MergePoint m : matches) {
			System.out.println(text.substring(m.getFeature().getStart(), m
					.getFeature().getEnd())
					+ " ----> "
					+ text.substring(m.getSentiment().getStart(), m
							.getSentiment().getEnd()));
		}
	}
}
