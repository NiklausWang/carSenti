package com.run.whunlp.model;

import java.util.HashMap;
import java.util.Vector;

public class FeatureSentimentMergeSimple {
	private Vector<TaggerPoint> featureList;
	private Vector<TaggerPoint> sentimentList;
	private String str;
	private double[][] costMatrix;   //情感词和特征词之间的�?短距�?
	private double[][] punctuationMatrix; //情感词和特征词之间的标点个数
	
	private Vector<Integer> []featureSentiment;  //第i个特征词对应的情感词
	
	private int deep;//深度优先遍历的深�?
	boolean []visitedRow;  //行是否访�?
	boolean []visitedCol;  //列是否访�?
	
	
	private double fitness = Double.MAX_VALUE; //�?有特征词和情感词匹配的�?�应值，越小越好
	private Vector<Integer> dfsIndex;  //深度优先遍历，特征词索引
	
	
	private Vector<Integer> res;//匹配结果
	private static HashMap<String, Double> punctuationCost;
	
	
	
	/**
	 * 标点符号的惩罚�?�，根据实际情况调整惩罚权重
	 */
	static{
		punctuationCost = new HashMap<String, Double>();
		punctuationCost.put("。", 30.0);
		punctuationCost.put("！", 30.0);
		punctuationCost.put("!", 30.0);
		punctuationCost.put("：", 10.0);
		punctuationCost.put(":", 10.0);
		punctuationCost.put("，", 8.0);
		punctuationCost.put(",", 8.0);
		punctuationCost.put("、", 2.0);
	}

	public FeatureSentimentMergeSimple() {
	}

	public FeatureSentimentMergeSimple(String str, Vector<TaggerPoint> tagger) {
		featureList = new Vector<TaggerPoint>();
		sentimentList = new Vector<TaggerPoint>();
		this.str = str;
		for (TaggerPoint taggerPoint : tagger) {
			if (taggerPoint.getPolarity() == 2) {
				featureList.add(taggerPoint);
			} else {
				sentimentList.add(taggerPoint);
			}
		}
		
		//计算距离代价矩阵和标点符号代价矩�?
		costMatrix = new double[featureList.size()][sentimentList.size()];
		punctuationMatrix = new double[featureList.size()][sentimentList.size()];
		getCostMatrix();
		
		featureSentiment = new Vector[featureList.size()];
		for( int i = 0; i < featureSentiment.length; i++){
			featureSentiment[i] = new Vector<Integer>();
		}
		//构�?�特征词相邻情感词列�?
//		System.out.println(featureList.size() + "\t" + sentimentList.size());
		for( int i = 0; i < featureList.size(); i++){
			TaggerPoint feature = featureList.get(i);
			for( int j = 0; j < sentimentList.size(); j++){
				TaggerPoint sentiment = sentimentList.get(j);
				int interval = Math.abs(feature.getOrder() - sentiment.getOrder());
				if( interval <= 1){
					featureSentiment[i].add(j);
				}
			}
		}
		
	}
	
	/**
	 * 获取情感词和特征词之间的�?小距�?
	 * @param f
	 * @param s
	 * @return
	 */
	private double getCost(TaggerPoint feature, TaggerPoint sentiment) {

		int res = -1;
		if (feature.getStart() > sentiment.getStart()) {
			res = feature.getStart() - sentiment.getEnd();
		} else {
			res = sentiment.getStart() - feature.getEnd();
		}
//		if (res <= 0) {
//			System.out.println("get Cost buf");
//		}

		return res+1.0;
	}

	private double getCharCost(String c){
		double res = 0;
		if( punctuationCost.containsKey(c)){
			res = punctuationCost.get(c);
		}
		return res;
	}
	
	private double getPunctuation(TaggerPoint feature, TaggerPoint sentiment){
		double res = 0;
		/**
		 *惩罚 这种情况�?
		 *我感觉很 满意【Y�? �? 内饰【T�? 感觉十分�? 温馨舒�?��?�Y�? 
		 *如果不做惩罚，内饰和满意配对�? 因此做如下惩罚：
		 *特征词和情感词之间有标点符号情况�?
		 *1、特征词在前，情感词在后的加大惩罚量�? 因为很少情感词在前面，特征词在后面，并且中间有标点符号�??
		 *2、对应特征词在前，情感词在后的�?? 也要做惩罚，惩罚量要比情�?1少�?�因为有可能有这样的表达
		 *内饰【T�? 装饰了很多东西，相当漂亮【Y�?
		 *
		 */
		//我感觉很 满意【Y�? �? 内饰【T�? 感觉十分�? 温馨舒�?��?�Y�? 
		//情感词在后面�?  外观【T�? �? 漂亮【Y�? . 
		if (feature.getStart() < sentiment.getStart()) {
			for( int i = feature.getEnd(); i < sentiment.getStart(); i++){
				res += getCharCost(str.charAt(i)+"");
			}
		} //情感词在前面，漂亮�?�Y】的 外观【T】， 
		else if( sentiment.getStart() < feature.getStart()){
			for( int i = sentiment.getEnd(); i < feature.getStart(); i++){
				res += getCharCost(str.charAt(i)+"")*Double.MAX_VALUE;
			}
		}
		return res;
	}
	/**
	 * 获取特征词和情感词任意两个之间的距离
	 */
	private void getCostMatrix() {
		for (int i = 0; i < featureList.size(); i++) {
			for (int j = 0; j < sentimentList.size(); j++) {
				costMatrix[i][j] = getCost(featureList.get(i),sentimentList.get(j));
				punctuationMatrix[i][j] = getPunctuation(featureList.get(i),sentimentList.get(j));
			}
		}
	}

	
	private double getFeatureSentimentCost(int featureIndex, int sentimentIndex){
		if( featureIndex >= featureList.size() || sentimentIndex >= sentimentList.size() || featureIndex < 0 || sentimentIndex < 0){
			return Double.MAX_VALUE;
		}
		double cost = 0;
		cost = costMatrix[featureIndex][sentimentIndex] + punctuationMatrix[featureIndex][sentimentIndex];
		return cost;
	}
	/**
	 * 获取特征词和情感词的�?佳匹配结�?
	 * @return
	 */
	public Vector<MergePoint> getMatch() {
		Vector<MergePoint> resList = new Vector<MergePoint>();
		int rowCnt = featureList.size();
		int colCnt = sentimentList.size();
		if( rowCnt == 0 || colCnt == 0){
			return resList;
		}
		
		visitedRow = new boolean[rowCnt];
		visitedCol = new boolean[colCnt];

		Vector<Integer> buf = new Vector<Integer>();
		
		
		
		res = new Vector<Integer>();
		fitness = Integer.MAX_VALUE;
		dfsIndex = new Vector<Integer>();
		for( int i = 0; i < featureSentiment.length; i++){
			if( featureSentiment[i].size() > 0){
				dfsIndex.add(i);
			}
		}
		deep = dfsIndex.size();
//		System.out.println("deep\t" + deep);
		
		dfs(0,buf);
//		System.out.println("size ="+ res.size() + "\t" + "deep="+deep);
		for( int i = 0; i < res.size(); i++){
			int featureIndex = dfsIndex.get(i);
			int sentimentIndex = res.get(i);
			if(sentimentIndex >= 0){
				MergePoint mp = new MergePoint();
				mp.setFeature(featureList.get(featureIndex));
				mp.setSentiment(sentimentList.get(sentimentIndex));
				resList.add(mp);
			}
		}
		
		return resList;
	}

	

	/**
	 * dfs 遍历 情感词和特征词的匹配�? 找出匹配代价�?小的
	 * @param k
	 * @param buf
	 */
	private void dfs(int k,Vector<Integer> buf) {
		if (k == deep) {
			double thisCost = 0;
			int matchCnt = 0;
			for( int i = 0; i < k; i++){
				int featureIndex = dfsIndex.get(i);
				int sentimentIndex = buf.get(i);
				if( sentimentIndex >= 0 ){
					thisCost += getFeatureSentimentCost(featureIndex,sentimentIndex);
					matchCnt++;
				}
			}
			if (matchCnt > 0) {
				thisCost = thisCost / Math.pow(matchCnt*1.0, 2);			
//				System.out.println("thisCost\t"+thisCost);
				if (thisCost < fitness) {
					fitness = thisCost;
					res.clear();
					for (Integer integer : buf) {
						res.add(integer);
					}
				}
			}
			return;
		}
		int featureIndex = dfsIndex.get(k);
		Vector<Integer> thisFeatureSentiment = featureSentiment[featureIndex];
		int tag = 0;
		for (int i = 0; i < thisFeatureSentiment.size(); i++) {
			int sentimentIndex = thisFeatureSentiment.get(i);
			if (visitedCol[sentimentIndex] == false) {
				//计算此特征词和情感词匹配的代�?
				double thisCost = getFeatureSentimentCost(featureIndex,sentimentIndex);
//				System.out.println(featureIndex + "\t" + "\t" + sentimentIndex + "\t" + thisCost);
				if( thisCost < 20 ){
					buf.add(sentimentIndex);
					visitedCol[sentimentIndex] = true;
				}
				else{
					buf.add(-1);
				}
				dfs(k+1,buf);
				visitedCol[sentimentIndex] = false;
				buf.remove(buf.size()-1);
				tag = 1;
			}
		}
		if( tag == 0 ){
			buf.add(-1);
			dfs(k+1,buf);
			buf.remove(buf.size()-1);
		}
	}

}
