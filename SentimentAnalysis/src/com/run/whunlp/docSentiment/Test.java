package com.run.whunlp.docSentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.run.whunlp.dict.Record;

@SuppressWarnings("unused")
public class Test {

	public static void main(String[] args) throws IOException, SentimentException {
		// int i = 0;
		// while(i < 5){
		// performance();
		// i++;
		// }
		// performance();
		test();
	}

	public static void performance() throws IOException, SentimentException {
		String filePath = "D:\\workspace\\SentimentAnalysis_lyn\\performance\\weibo.rand_out_test_wh";
		FileInputStream fis = new FileInputStream(new File(filePath));
		InputStreamReader isr = new InputStreamReader(fis, "GB2312");
		BufferedReader br = new BufferedReader(isr);
		double startTime = System.currentTimeMillis();
		String tempOne = "";
		int i = 1;
		double singleMaxTime = 0.0;
		while ((tempOne = br.readLine()) != null) {
			double singleStartTime = System.currentTimeMillis();
			int timesCount = 1;

			StringBuilder results = new StringBuilder(1000);
			String newLine = "\r\n";
			results.append(tempOne);
			results.append(newLine);
			DocSentiment ds = new DocSentiment(tempOne, "雅阁");
			List<String> segs = ds.SentiAnalyse();

			List<Record> pieces = ds.getFSwords();

			if (!segs.isEmpty()) {
				for (String s : segs) {
					Pattern pt = Pattern.compile("<[^<]+>");
					Matcher mt = pt.matcher(s);
					String noTags = mt.replaceAll("");
					results.append("片段：" + s + newLine);
				}
			}

			if (!pieces.isEmpty()) {
				for (Record s : pieces) {
					results.append("三元组：" + s.toString() + newLine);
				}
			}

			results.append("类别： " + ds.getFeatureClass() + newLine);
			i++;
			ds = null;
			double singleEndTime = System.currentTimeMillis();
			if (singleMaxTime < (singleEndTime - singleStartTime)) {
				singleMaxTime = (singleEndTime - singleStartTime);
			}
			results.append(newLine);
			writeTofile(results.toString(), filePath + "_results");
		}
		br.close();
		double endTime = System.currentTimeMillis();
		System.out.println("平均每条用时 ：" + (endTime - startTime) / i + "毫秒");
		System.out.println("单条最大用时为" + singleMaxTime + "毫秒");
	}

	public static void test() throws SentimentException, IOException {
		String doctest = "[汽车之家 专业评测]  欧美厂商首先嗅到了国内小型SUV市场的商机;;;;并接连推出了昂科拉ENCORE、TRAX创酷、翼搏和标致2008等车型；；；；这种售价相对更低、身材更适合城市使用且更加时尚有活力的SUV车型非常受年轻消费者的青睐。而广汽本田也终于在近期推出了自己的小型SUV车型——缤智。"
				+ " 缤智的外形设计非常成功；；；；非常省油，后排空间不错;;;车内有噪音，方向盘大气充沛漂亮，方向盘不是特别差，昂科拉ENCORE、TRAX创酷动力油耗都算满意，换挡有问题。仅看前脸就能在第$$$一时间抓住路人的目光。$$这点非常符合$$$年轻消费者$$$时$刻都想$成$为焦点$的$心理。另外缤智的车身尺寸较同级别其它车型，在长度和轴距方面也都有着不小的优势。"
				+ " 缤智的轮圈$造型本身没有问题，双$色加刀锋的样式符$合车型定位。不过左$右轮圈采用的是同一个模具生产，导致滚动方向相反，细琢磨会觉得有些别扭。"
				+ "我相当喜欢缤智的车尾造型，这是在其它车型$上很难得的设计。可$以看出设计师深$谙讴歌的风格之道$，这真正地让缤智$成为3$60°无死角的吸睛利器。";
		String shortString1 = "编辑点评：可以说广汽推出了凌派，有效的填补了锋范和雅阁之间的市场空缺，对于消费者来说也增加了更多的选择性。　　【温馨提示】在4S店购车时，如果提及搜狐汽车，您将享受到地区更优质的服务。声明：此价格为个体行为，不代表全部。";
		// String shortString = "";
		// int ntimes = 100;
		// while((ntimes--)>0){
		// shortString = shortString.concat(shortString1);
		// }

		// System.out.println(shortString1.substring(56,63));
		double time1 = System.currentTimeMillis();

		DocSentiment ds = new DocSentiment(shortString1, "雅阁");

		List<String> segs = ds.SentiAnalyse();

		List<Record> pieces = ds.getFSwords();

		int[] numbers = new int[100];
		
		if (!segs.isEmpty()) {
			for (String s : segs) {
				Pattern pt = Pattern.compile("<[^<]+>");
				Matcher mt = pt.matcher(s);
				String noTags = mt.replaceAll("");
				System.out.println("片段：" + s + noTags.length());
			}
		}

		if (!pieces.isEmpty()) {
			for (Record s : pieces) {
				System.out.println("三元组：" + s.toString());
			}
		}

		System.out.println("类别： " + ds.getFeatureClass());

		double timeend = System.currentTimeMillis();
		System.out.println((timeend - time1) / 1000);

	}

	public static void writeTofile(String text, String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream os = new FileOutputStream(f, true);
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(text);
		bw.close();
	}

	public static void test1() {
		String s = "汽车之家";

		System.out.println("this is : " + s.substring(0, 0));
	}

}
