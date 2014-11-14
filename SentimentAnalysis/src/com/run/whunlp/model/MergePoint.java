package com.run.whunlp.model;

import java.util.Vector;


public class MergePoint {
	private TaggerPoint feature;
	private TaggerPoint sentiment;
	private double cost;
	
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	private Vector<TaggerPoint> sentimentList;
	
	public Vector<TaggerPoint> getSentimentList() {
		return sentimentList;
	}
	public void setSentimentList(Vector<TaggerPoint> sentimentList) {
		this.sentimentList = sentimentList;
	}
	public TaggerPoint getFeature() {
		return feature;
	}
	public void setFeature(TaggerPoint feature) {
		this.feature = feature;
	}
	public TaggerPoint getSentiment() {
		return sentiment;
	}
	public void setSentiment(TaggerPoint sentiment) {
		this.sentiment = sentiment;
	}
	
}
