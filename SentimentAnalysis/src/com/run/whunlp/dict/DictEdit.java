package com.run.whunlp.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DictEdit {
	private static List<DictTerm> dict;
	public static void main(String args[]) throws IOException {
		String sentimentTerm = "�߼�";
		String polarity = "1";
		
		if(polarity.equals("5")){
			addTerm("E:\\wanghao\\gitProjects\\carSenti\\SentimentAnalysis\\dict\\OptiDict",sentimentTerm,polarity);
		}
		else{
			addTerm("E:\\wanghao\\gitProjects\\carSenti\\SentimentAnalysis\\dict\\sentiment",sentimentTerm,polarity);
		}
		
	}

	@SuppressWarnings("resource")
	public static void loadTerms(String path) throws IOException {
		File f = new File(path);
//		if (!f.exists()) {
//			f.createNewFile();
//		}
		dict = new LinkedList<DictTerm>();
		FileInputStream fs = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String potemp = "";
		while ((potemp = br.readLine()) != null) {
			String[] Dicterm = potemp.split("\\t");
			DictTerm dt = new DictTerm(Dicterm[0],Dicterm[1]);
			dict.add(dt);
		}

		System.out.println("��ǰ�ʵ���Ŀ��Ϊ�� " + dict.size());
	}

	public static void writeTofile(String text, String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream os = new FileOutputStream(f, false);
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(text);
		bw.close();
	}

	private static void addTerm(String dictName, String sentimentTerm, String polarity) throws IOException {
		if(polarity.equals("0") || polarity.equals("1") || polarity.equals("-1") || polarity.equals(("5"))){
			loadTerms(dictName);
			List<DictTerm> toRemove = new LinkedList<DictTerm>();
			for(DictTerm dt:dict){
				if(dt.getTerm().equals(sentimentTerm)){
					if(dt.getPolarity().equals(polarity)){
						System.out.println("�Ѵ����ڴʵ�");
						return;
					}else{
						System.out.println("���Գ�ͻ�������Ϊ׼��");
						toRemove.add(dt);
					}
				}
			}
			
			dict.removeAll(toRemove);
			DictTerm newTerm = new DictTerm(sentimentTerm,polarity);
			dict.add(newTerm);
			StringBuilder dictString = new StringBuilder();
			for(DictTerm dt:dict){
				dictString = dictString.append(dt.getTerm());
				dictString = dictString.append("\t");
				dictString = dictString.append(dt.getPolarity());
				dictString = dictString.append("\r\n");
			}
			
			File oldDict = new File(dictName);
			Boolean renameSuccessful = false;
			if(oldDict.exists()){
				Date now = new Date();
				String backupName = dictName + ".bak";
				File dest = new File(backupName);
				FileUtils.copyFile(oldDict,dest,true);
				if(dest.exists()){
					renameSuccessful = true;
					FileUtils.deleteQuietly(oldDict);
				}
			}
			
			if(renameSuccessful){
				
				writeTofile(dictString.toString().trim(),dictName);
				System.out.println("�������");
			}else{
				System.out.println("����ʧ��");
			}
		}
	}
}
