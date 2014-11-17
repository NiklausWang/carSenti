package com.run.rhea.model;

/**
 * 特征词类
 * 
 * @author lenovo
 *
 */
public class Feature implements Comparable<Feature> {

	private String featureName;
	private Feature parentFeature;
	private int count;
	private int pos;


	public int posEnd() {
		return pos + featureName.length();
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public Feature getParentFeature() {
		return parentFeature;
	}

	public void setParentFeature(Feature parentFeature) {
		this.parentFeature = parentFeature;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public int compareTo(Feature feature) {
		if (this.getPos() > feature.getPos()) {
			return 1;
		} else if (this.getPos() < feature.getPos()) {
			return -1;
		} else {
			if (this.posEnd() < feature.posEnd()) {
				return 1;
			} else if (this.posEnd() > feature.posEnd()) {
				return -1;
			}
		}
		return 0;
	}
}
