package com.run.rhea.core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
///import java.util.HashMap;
import java.util.List;
///import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.IndexAnalysis;

import com.run.rhea.model.Feature;
import com.run.rhea.tools.DictUtil;
import com.run.rhea.tools.FeatureWordSet;
import com.run.rhea.tools.SingletonPaserAlpha;
///import com.run.rhea.tools.UnambiguousWordSet;
import com.run.rhea.tools.UnambiguousWordSet;

public class IdentifyImpl implements FeatureIdentify{

	SingletonPaserAlpha singletonPaserAlpha = SingletonPaserAlpha.getInstance();

	/**
	 * 获取文本中所有的可能特征词，存在于特征词词典中的词
	 * @param text 输入文本
	 */
	public List<Feature> getPendingFeatureWords(String text){
		
		//分词结果
		List<Term> terms = IndexAnalysis.parse(text);
//		System.out.println(terms);
		//存放文本中的所有待定特征词
		List<Feature> featureList = new ArrayList<Feature>();
		
		
		//获得文本中的所有待定特征词
		for(Term term:terms){
			if (FeatureWordSet.wordsSet.contains(term.getName())) {
				
				//临时存放判断为待定特征词
				Feature feature = new Feature();
				
				feature.setFeatureName(term.getName());
				feature.setPos(term.getOffe());
				featureList.add(feature);
			}
		}
		
		Collections.sort(featureList);
		
		return featureList;
	}
	
	/**
	 * 去除分词中产生的特征包含词；比如“前轮距”包含“前轮”会在分词中分出来，将其去除
	 * @param features
	 * @return
	 */
	public List<Feature> removeContainFeature(List<Feature> features){
		
		//存放返回结果
		List<Feature> resultFeatures = new ArrayList<Feature>();
		
		//去包含词
		for(Feature  feature : features) {
			
			if(resultFeatures.size() == 0){
				resultFeatures.add(feature);
				continue;
			}
			
			Feature lastFeature = resultFeatures.get(resultFeatures.size() - 1);
			
			// 判断是否是包含关系
			if ((feature.getPos() < lastFeature.posEnd()) && (feature.posEnd() <= lastFeature.posEnd())) {
				continue;
			}
			resultFeatures.add(feature);
		}
		
		return resultFeatures;
	}
	
	/***
	 * 获取上下文的个数从configure里
	 */
	public int getContexNum(){
		int result = 0;
		BufferedReader reader = null;
		try {
			InputStreamReader isr = new InputStreamReader(this.getClass().getResourceAsStream("/configure"), "UTF-8");
			reader = new BufferedReader(isr);
			String negString = "";
			while ((negString = reader.readLine()) != null) {
				if(negString.contains("/")){
					continue;
				} else {
					result = Integer.parseInt(negString);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("获取失败！", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return result;
    
	}
	
	/***
	 * 判断输入是否为特征词
	 * @param terms 
	 * @return
	 */
	public boolean isFeatureWord(List<Feature> features, int pos){
		
		//用来控制非歧义词的上下文
		int lengthK =getContexNum();//K值
	
		String word = features.get(pos).getFeatureName();
		
		// 如果是非歧义词典中存在该特征词，则识别为特征词
		if (UnambiguousWordSet.wordsSet.contains(word)) {
			return true;
		}
		
		// 判断歧义词是否是特征词,带判断词前若干部分
		for(int i = pos; i >= 0; i--){
			String featureName = features.get(i).getFeatureName(); 

			int posText = features.get(pos).getPos();
			
			if (((posText - lengthK) < features.get(i).getPos()) 
					&& UnambiguousWordSet.wordsSet.contains(featureName)) {
				return true;
			}
		}
		
		// 判断歧义词是否是特征词,带判断词后若干部分
		for(int i = pos; i<features.size(); i++){
			String featureName = features.get(i).getFeatureName();
			int posText = features.get(pos).getPos();
			
			if (((posText + lengthK) > features.get(i).getPos()) 
					&& UnambiguousWordSet.wordsSet.contains(featureName)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 获取文本中的特征列表
	 * 
	 * @param text 文本
	 * @return 特征列表
	 */
	@Override
	public List<Feature> getFeatures(String text){
		// 文本中包含的特征
		List<Feature> textFeatureList = new ArrayList<Feature>();
		
		//插入分词词典数据
		DictUtil.getInstance();
		
		// 对文本进行分词，并对获取到的，特征词词典中的特征词排序
		List<Feature> features = getPendingFeatureWords(text);
		
		//合并包含特征词
		features = removeContainFeature(features);
		
		
		for (int i = 0; i < features.size(); i++) {
			// 获取当前词
			String word = features.get(i).getFeatureName();
			// 当前词为特征词
			if (isFeatureWord(features, i)) {
				/*// 已获取过该特征
				if (textFeatureMap.containsKey(word)) {
					Feature f = textFeatureMap.get(word);
					f.setCount(f.getCount() + 1);
					textFeatureMap.put(word, f);
				} else{*/
				textFeatureList.add(createFeature(word.toString(), features.get(i).getPos()));
				
			}
		}
		return textFeatureList;
	}
	
	/**
	 * 获得文本中所有特征（包含复杂特征）
	 * 
	 * @return 特征列表
	 */
	public List<Feature> getComplexFeatures(String text){
		List<Feature> textFeatureList = getFeatures(text);
		Collections.sort(textFeatureList);
		int i = 0;
		int featureCount = textFeatureList.size();
		List<Integer> indexToRemove = new ArrayList<Integer>();
		for (i = 0; i < featureCount - 1; i++) {
			if (textFeatureList.get(i).posEnd() == textFeatureList.get(i + 1)
					.getPos()) {
				String complexName = textFeatureList.get(i).getFeatureName()
						+ textFeatureList.get(i + 1).getFeatureName();
				textFeatureList.get(i + 1).setFeatureName(complexName);
				textFeatureList.get(i + 1).setPos(
						textFeatureList.get(i).getPos());
				indexToRemove.add(i);
			}
		}
		int rIndex, len;
		int j = 0;
		for (rIndex = 0, len = indexToRemove.size(); rIndex < len; rIndex++) {
			textFeatureList.remove(indexToRemove.get(rIndex) - j);
			j++;
		}
		return textFeatureList;
	}
/*	public List<Feature> getComplexFeatures(String text){

		// 文本中包含的特征
		List<Feature> textFeatureList = new ArrayList<Feature>();
		
		//插入分词词典数据
		DictUtil.getInstance();
		
		// 对文本进行分词，并对获取到的特征词排序
		List<Feature> features = getPendingFeatureWords(text);
		
		//合并包含特征词
		features = removeContainFeature(features);
		
		for (int i = 0; i < features.size(); i++) {
			
			// 获取当前词
			String word = features.get(i).getFeatureName();
			
			// 当前词为特征词
			if (isFeatureWord(features, i)) {
				textFeatureList.add(createFeature(word, features.get(i).getPos()));	
			}
		}
	
		// 获得文本中所有单个词特征的列表
		List<Feature> singleFeatures = textFeatureList;
		
		//文本中所有词的特征列表（包括相邻的复杂特征）
		List<Feature> textFeature = new ArrayList<Feature>();
		
		//排序
		Collections.sort(singleFeatures);
		
		//标记最后一个词是否与其前边的相邻词组成复杂特征，默认为否
		boolean flag=false;
		
		//存放特征名的字符串
		StringBuffer nameString = null; 
		
		//遍历整个单个词组成的特征链表，发现复杂特征
		for(int i=0; i<singleFeatures.size()-1;){
			nameString=new StringBuffer();
			//获得当前特征
			Feature feature = singleFeatures.get(i);
			
			//获得当前特征的下个特征
			Feature nextFeature = singleFeatures.get(++i);
		
			nameString.append(feature.getFeatureName());
			
			//如果当前特征与其下一个特征相邻，将他们组合作为复杂特征
			while(feature.posEnd()==nextFeature.getPos()){
				nameString.append(nextFeature.getFeatureName());
				feature = nextFeature;
				
				//判断最后一个词是否已经作为复杂特征一部分
				if(i<singleFeatures.size()-1){
					nextFeature = singleFeatures.get(++i);
				}else {
						flag=true;  //最后一个词已经作为复杂特征一部分
						break;
				}
			}
			//将组合后的词组成特征加入特征列表
			textFeature.add(createFeature(nameString.toString(), feature.posEnd()-nameString.length()));
			
		}
		
		////最后一个词没有已经作为复杂特征一部分，单独加入
		if(flag == false && singleFeatures.size()>1){
			textFeature.add(singleFeatures.get(singleFeatures.size()-1));
		}
		
		return textFeature;
		
	}*/
	
	/**
	 * 创建特征
	 * @return
	 */
	private Feature createFeature(String word, int pos) {
		//用来存储子特征的临时变量
		Feature feature = new Feature();
		
		//用来存储特征的临时变量
		Feature parentFeatureTemp = new Feature();
		
		//获取子特征所属特征
		String getParentFeature = singletonPaserAlpha.getFeatureNameAccordChildFeature(word);
		
		//将特征词存放到Feature类
		parentFeatureTemp.setFeatureName(getParentFeature);
		feature.setCount(feature.getCount()+1);
		feature.setPos(pos);
		feature.setFeatureName(word);
		feature.setParentFeature(parentFeatureTemp);
		
		return feature;
	}
	
	public static void main(String[] args){
		IdentifyImpl identifyImpl = new IdentifyImpl();
		//identifyImpl.testCount();
		String text = "缤智的外形设计非常成功";
		//List<Feature> features = identifyImpl.getPendingFeatureWords(text);
		//String text = "abs";
		//String text = "配置部件及系统：动力系统，底盘，车身，标准零件，汽车内部，娱乐系统，发动机电控系统，汽车照明系统，电气系统，驾驶员辅助系统、汽车安全系统，电动汽车蓄电池（OEM），替代驱动单元，车辆及商用车再生部件，商用车、乘用车、SUV、两轮车、三轮车、牵引车、老式汽车部件及服务v 附件及改装：通用汽车配件，改装，定制，特殊设备及修改，轮圈，车轮，轮胎，拖车，小型商用车零部件，电动汽车充电器、蓄电池（转换、循环利用）v 轮胎及电池：各类汽车轮胎，轮圈，离心管及套管，电池及电池组件v 维修及保养：维修站设备及工具，起重装置，检测设备，轮胎安装，车身维修，喷漆及腐蚀防护，拖车服务，事故援助，移动服务，拖车，废物处理及回收，经销商设备，培训，老式车修复与保养，行业机构及出版商v IT及管理：经销商策划及建设，金融及特许经营概念，索赔管理及控制，配置";
		//System.out.println("esp:"+featureHashMap.get(text).getWord());
		//String text = "车型安全配置";
		List<Feature> comfeatures = identifyImpl.getComplexFeatures(text);
		List<Feature> features = identifyImpl.getFeatures(text);
		List<Feature> penfeatures = identifyImpl.getPendingFeatureWords(text);
		Collections.sort(features);
		//MarkText markText = identifyImpl.getAllBrandMarkTextAccordBrandName("camry我很喜欢凯美瑞，但是汉兰达也挺不你这不是废话啊，大哥 错的，所以大方的丰Camry盛的方式的算法萨菲定点数飞我最后买个汉兰达这一款丰田系列的轿车！camry","凯美瑞");
		//System.out.println(identifyImpl.getFactoryNameAccordBrandName("凯美瑞"));
		//System.out.println(markText.getMarkingText());
		/*for(int i=0;i<markText.getMarkingPositions().size();i++){
			System.out.println(markText.getMarkingPositions().get(i).getMarkingText());
		}*/
		/*for(Feature feature : features){
			System.out.println("结果："+feature.getFeatureName()+" "+feature.getParentFeature().getFeatureName()+" "+feature.getCount());
		}*/
		System.out.println(">>>>>>>待定特征词");
		for(Feature feature : penfeatures){
			System.out.println("特征名："+feature.getFeatureName()+" 	位置："+feature.getPos()+" "+feature.posEnd());
		}
		System.out.println(">>>>>>>特征词");
		for(Feature feature : features){
			System.out.println("特征名："+feature.getFeatureName()+" 	位置："+feature.getPos()+" "+feature.posEnd()+"  	父特征: "+feature.getParentFeature().getFeatureName()+"  	次数："+feature.getCount());
		}
		System.out.println(">>>>>>>复杂特征词！");
		for(Feature feature : comfeatures){
			System.out.println("特征名："+feature.getFeatureName()+" 	位置："+feature.getPos()+" "+feature.posEnd()+"  	父特征: "+feature.getParentFeature().getFeatureName()+"  	次数："+feature.getCount());
		}
	}
}
