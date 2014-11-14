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
		String text = "��������λ��ȱ������ֵ���򣬲��пϣ���ϲ�������ѹң������Խϲ��GS��ٹ������11.3��ȱ����ǰ����GL�����Բ�������Ϊ10.8�롣��80����˵�����ݲ�����Ŀ��ʵ�ʼ�ʻ�еĶ����������������⡣��������Ϊ���ɣ��̾������ͦ���䡣��������ҪС�ĶԴ����Ų��ܱ�֤���շ���������ʻ��6���Զ��������뵱ǰ�ľ�����һ�������������İ汾������������ࡣϲ����������ߵ�λ���ص����ɣ����Ǳ�֤�ͺľ����Եı�Ҫ�ֶΡ�����ζ���ڴ�������ʱ������Ҫ�ϳ��Ľ���ʱ�䲻�����Ҳ��ζ�Ų��ṩ���˶�������ģʽ��һ��ȱ����";
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
		String text = "�Ÿ�>>����550�����ۼۣ�9.98��-25.98��6��������1103̨���ߴ磺2705mm�����ŵ㣺�ռ�ϴ����οƼ���ǿ���������˳���ȱ�㣺1.8L����ƫ�����ֶ������������Ľ����������������������������ͣ����Լ۱����Ʋ��羺������";

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
			System.out.println("���һ��������: "
					+ text.substring(tp.getStart(), tp.getEnd()));
		}
		Collections.sort(fTaggerList);

		for (TaggerPoint tp : sentimentWordList) {
			System.out.println("�ҵ���дʣ� "
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
