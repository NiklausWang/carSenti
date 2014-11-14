package com.run.whunlp.model;

public class TaggerPoint implements Comparable<TaggerPoint>{
	private int start;    //字符串的开始位置
	private int end;      //字符串的结束位置
	private int polarity; //极性 -1 表示负面；1表示正面,0代表中性词,2特征词
	private int tokenStart; //分词的开始位置
	private int tokenEnd;   //分词的结束位置
	private String parentFeature; //特征词的父特征，情感词此字段为空
	private String hidenName;     //模板匹配时的名称
	
	public String getParentFeature() {
		return parentFeature;
	}
	public void setParentFeature(String parentFeature) {
		this.parentFeature = parentFeature;
	}
	public String getHidenName() {
		return hidenName;
	}
	public void setHidenName(String hidenName) {
		this.hidenName = hidenName;
	}
	private int order;      //识别结果，按照从小到大排序的序号，
	
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getPolarity() {
		return polarity;
	}
	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getTokenStart() {
		return tokenStart;
	}
	public void setTokenStart(int tokenStart) {
		this.tokenStart = tokenStart;
	}
	public int getTokenEnd() {
		return tokenEnd;
	}
	public void setTokenEnd(int tokenEnd) {
		this.tokenEnd = tokenEnd;
	}
	@Override
	public String toString() {
		return "TaggerPoint [start=" + start + ", end=" + end + ", polarity="
				+ polarity + "]";
	}
	@Override
	public int compareTo(TaggerPoint o) {
		if(this.getStart() > o.getStart()){
			return 1;
		}else if(this.getStart() < o.getStart()){
			return -1;
		}else{
			if (this.getEnd() < o.getEnd()) {
				return 1;
			} else if (this.getEnd() > o.getEnd()) {
				return -1;
			}
		}
		return 0;
	}
	
	
	
}
