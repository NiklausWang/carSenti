package com.run.rhea.model;

/**
 * 瀛樻斁鐨勬槸瀛愮壒寰佸悕绉板拰瀛愮壒寰佹墍灞炵殑鐗瑰緛鐨勭被
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
