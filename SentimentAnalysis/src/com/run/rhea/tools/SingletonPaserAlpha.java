package com.run.rhea.tools;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author kai
 */
public class SingletonPaserAlpha {

	private static Document factorydoc = null; // 厂商品牌特征文档的Document对象
	private static SingletonPaserAlpha signleInstance = null; // 创建单例对象
	private static DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance(); // 得到DOM解析器工厂类的实例
	private static DocumentBuilder db = null;
	public static NodeList factoryNodeList, brandNodeList, seriesNodeList,
			conceptNodeList, seriesFactoryList, groupNodedList,
			groupFactoryList;

	/**
	 * @deprecated:使用懒汉式单例设计模式，获得该单例对象的实例
	 * @param
	 * @return：单例对象的实例
	 */
	public static SingletonPaserAlpha getInstance() {
		if (signleInstance == null) {
			createInstance();
		}
		return signleInstance;
	}

	/**
	 * @deprecated:创建单例对象的实例
	 * @param
	 * @return:单例对象的实例
	 */
	private synchronized static SingletonPaserAlpha createInstance() {
		if (signleInstance == null) // 单例对象的实例为null，则创建单实例
		{
			signleInstance = new SingletonPaserAlpha();
		}
		return signleInstance;
	}

	/**
	 * @deprecated:枸造函数解析XML文档，获得文档的Document对象，并使用计时器对文档进行间隔性的读取
	 * @param
	 * @return
	 */
	private SingletonPaserAlpha() {

		try {
			db = dbf.newDocumentBuilder(); // 得到DOM解析器对象
			factorydoc = db.parse(this.getClass()
					.getResourceAsStream("/特征.xml")); // 使用DOM解析器对象的parse()方法解析XML文档，得到表示整个文档的Documen对象
			conceptNodeList = factorydoc.getElementsByTagName("concept"); // 得到有所有元素名字为"concept"的节点组成的节点链表
		} catch (Exception e) {
			throw new RuntimeException("本体XML加载失败！", e);
		}
	}

	/**
	 * @deprecated:将字符串set集合转换成一串字符串
	 * @param:stringSet 字符串set集合
	 * @return: string 转换后的字符串
	 */
	public String setToString(Set<String> stringSet) {
		String delimiter = "";
		String string = new String();
		for (String eString : stringSet) {
			string = string + delimiter + eString; // 将每个元素中间加上"|"组成正则式字符串
			delimiter = "|";
		}
		return string;
	}

	/**
	 * @deprecated:获取本体中所有的子特征
	 * @param
	 * @return:allChildFeatureNameRex 本体中所有子特征的正则式
	 */
	public String getAllChildFeatureName() {
		String childFeatureName = new String();
		Set<String> childFeatureNameSet = new HashSet<String>();
		for (int i = 0; i < conceptNodeList.getLength(); i++) {
			childFeatureName = conceptNodeList.item(i).getTextContent().trim();
			if (!childFeatureName.matches("\\s*")) {
				childFeatureNameSet.add(childFeatureName); // 将节点内容set集合中
			}
		}
		return setToString(childFeatureNameSet); // 调用setToString()函数将set集合内容转换，并返回
	}

	/**
	 * @deprecated: 由汽车特征名得到该特征下的所有子特征
	 * @param: feature 汽车特征名
	 * @return：childFeatureNameRex 该特征下的所有子特征组成的正则式
	 */
	public String getAllChildFeatureNameAcrrodFeature(String feature) {
		String childFeatureName = new String();
		Set<String> childFeatureNameSet = new HashSet<String>();
		for (int i = 0; i < conceptNodeList.getLength(); i++) {
			Element element = (Element) conceptNodeList.item(i);
			if (element.getAttribute("name").equals(feature)) // 当前元素的属性值等于输入的特征时，进行处理
			{
				childFeatureName = element.getTextContent().trim();
				if (!childFeatureName.matches("\\s*")) {
					childFeatureNameSet.add(childFeatureName); // 将节点内容set集合中
				}
			}
		}
		return setToString(childFeatureNameSet);
	}

	/**
	 * @deprecated:由汽车子特征名得到该子特征所属的特征
	 * @param: childFeature 汽车子特征名
	 * @return: feature 该子特征所属的特征
	 */
	public String getFeatureNameAccordChildFeature(String childFeature) {
		String chiledFeatureRex = new String(); // 子特征的正则式字符串
		String chileFeatureName = new String();
		Set<String> featureNameSet = new HashSet<String>();
		for (int i = 0; i < conceptNodeList.getLength(); i++) {
			chileFeatureName = conceptNodeList.item(i).getTextContent(); // 获得当前节点的内容
			if (!chileFeatureName.matches("\\s*")) {
				featureNameSet.add(chileFeatureName.toLowerCase()); // 将节点内容转成小写添加到set集合中
			}
			chiledFeatureRex = setToString(featureNameSet); // 调用setToString()函数将set集合内容转换，得到厂商的正则式字符串
			Pattern pattern = Pattern.compile(chiledFeatureRex);
			Matcher matcher = pattern.matcher(childFeature.toLowerCase());// 将输入的子特征转换成小写，并匹配
			if (matcher.find()) {
				Element element = (Element) conceptNodeList.item(i);// 获得当前匹配到的节点
				return element.getAttribute("name"); // 得到元素名字为"concept"的节点的"name"的属性值，并返回
			} else {
				featureNameSet.clear();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		SingletonPaserAlpha testDom = SingletonPaserAlpha.getInstance();
		System.out.println(testDom.getFeatureNameAccordChildFeature("防抱死"));
	}

}
