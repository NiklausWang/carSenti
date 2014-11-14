package com.run.whunlp.dict;

public class Record {
	private String feature;
	private String sentiment;
	private int polarity;

	public String getSentiment() {
		return sentiment;
	}

	@Override
	public String toString() {
		return "Record [feature=" + feature + ", sentiment=" + sentiment
				+ ", polarity=" + polarity + "]";
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public Record(String feature, String sentiment, int polarity) {
		this.feature = feature;
		this.sentiment = sentiment;
		this.polarity = polarity;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}

}
