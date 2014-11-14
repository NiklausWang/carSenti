package com.run.whunlp.docSentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.run.rhea.model.Feature;
import com.run.whunlp.develop.FeatureSentiAnalyisis;
import com.run.whunlp.dict.Record;

public final class GetFeatureWords {
	private String text = "";
	private FeatureSentiAnalyisis fs;

	public GetFeatureWords(String text) throws NumberFormatException, IOException {
		this.text = text;
		fs = new FeatureSentiAnalyisis(text);
	}

	public String getWords() throws NumberFormatException, IOException {
		List<Feature> features = new ArrayList<Feature>();
		features.addAll(fs.getFeatureList());
		features.addAll(fs.getSentimentList());

		Collections.sort(features);

		List<String> segStrings = new ArrayList<String>();
		int startPos = 0;
		for (int j = 0, feasize = features.size(); j < feasize; j++) {
			String before = "";
			if (features.get(j).getPos() >= startPos) { // 特征词和情感词之间没有相交
				before = text.substring(startPos, features.get(j).getPos());

				startPos = features.get(j).posEnd();
			}
			segStrings.add(before);
		}

		Collections.sort(features);

		for (int p = 0, feasize = features.size(); p < feasize; p++) {
			// System.out.println(features.get(p).getFeatureName() + " " +
			// features.get(p).getParentFeature().getFeatureName());
			if (features.get(p).getParentFeature().getFeatureName().equals(Integer.toString(-1))) {
				features.get(p).setFeatureName("<span class=\"sentiment-neg\">" + features.get(p).getFeatureName() + "</span>");
			} else if (features.get(p).getParentFeature().getFeatureName().equals(Integer.toString(0))) {
				features.get(p).setFeatureName("<span class=\"sentiment-neu\">" + features.get(p).getFeatureName() + "</span>");
			} else if (features.get(p).getParentFeature().getFeatureName().equals(Integer.toString(1))) {
				features.get(p).setFeatureName("<span class=\"sentiment-pos\">" + features.get(p).getFeatureName() + "</span>");
			} else if (features.get(p).getParentFeature().getFeatureName().equals("drop")) {
				features.get(p).setFeatureName("");
			} else {
				features.get(p).setFeatureName("<span class=\"sentiment-feature\">" + features.get(p).getFeatureName() + "</span>");
			}
		}
		String lastString = text.substring(startPos, text.length());
		String htmlString = "";
		for (int i = 0; i < segStrings.size(); i++) {
			htmlString += segStrings.get(i);
			htmlString += features.get(i).getFeatureName();
		}
		htmlString += lastString;
		htmlString = htmlString.trim();

		return htmlString;

	}



	public List<Record> getPairs() throws IOException {
		List<Record> FeaSenWords = fs.getFeaSenPairs();
		return FeaSenWords;
	}

	public List<Feature> getFeatureList() {
		return fs.getFeatureList();
	}
}
