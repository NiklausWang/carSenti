package com.run.rhea.core;

import java.util.Collection;

import com.run.rhea.model.Feature;

/**
 * 特征识别接口
 * 
 * @author WangDuo
 *
 */

public interface FeatureIdentify {
	/**
	 * 获取文本中的特征列表
	 * 
	 * @param text
	 *            文本
	 * @return 特征列表
	 */
	Collection<Feature> getFeatures(String text);
}
