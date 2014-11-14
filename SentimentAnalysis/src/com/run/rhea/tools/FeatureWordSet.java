package com.run.rhea.tools;

import java.util.HashSet;

public class FeatureWordSet {

	public static HashSet<String> wordsSet = new HashSet<String>();

	private FeatureWordSet() {
		String featureString = SingletonPaserAlpha.getInstance()
				.getAllChildFeatureName();
		String[] splitStrings = featureString.split("\\|");
		for (int i = 0; i < splitStrings.length; i++) {
			wordsSet.add(splitStrings[i].toLowerCase());
		}
	}

	private static FeatureWordSet single;

	static {
		single = new FeatureWordSet();
	}

	public synchronized static FeatureWordSet getInstance() {
		if (single == null) {
			single = new FeatureWordSet();
		}
		return single;
	}

	public static void main(String args[]) {
		FeatureWordSet.getInstance();
	}
}
