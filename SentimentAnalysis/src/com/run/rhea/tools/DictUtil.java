package com.run.rhea.tools;

import java.util.Set;
import org.ansj.library.UserDefineLibrary;

/**
 * 分词工具加载词典
 * 
 * @author lei
 *
 */
public class DictUtil {

	private DictUtil() {

		Set<String> dictSet = UnambiguousWordSet.wordsSet;
		Set<String> addDictSet = FeatureWordSet.wordsSet;
		insertWord(dictSet);
		insertWord(addDictSet);
	}

	private static DictUtil single;

	static {
		single = new DictUtil();
	}

	public synchronized static DictUtil getInstance() {
		if (single == null) {
			single = new DictUtil();
		}
		return single;
	}

	public static void insertWord(Set<String> words) {
		for (String word : words) {
			UserDefineLibrary.insertWord(word, "feature", 1000);
		}
	}

	public static void main(String args[]) {
		DictUtil.getInstance();
	}
}