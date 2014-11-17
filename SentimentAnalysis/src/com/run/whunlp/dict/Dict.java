package com.run.whunlp.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.run.whunlp.model.TaggerPoint;

public class Dict {
	private int posCount = 0;
	private int negCount = 0;
	private int othCount = 0;

	private List<DictTerm> posTerms = new ArrayList<DictTerm>();
	private List<DictTerm> negTerms = new ArrayList<DictTerm>();
	private List<DictTerm> othTerms = new ArrayList<DictTerm>();

	public Dict() {

	}

	public Dict(String dicPath) throws IOException {
		FileInputStream f = new FileInputStream(new File(dicPath));
		InputStreamReader sr = new InputStreamReader(f, "UTF-8");
		BufferedReader br = new BufferedReader(sr);

		String temp = "";
		int line = 0;
		while ((temp = br.readLine()) != null) {
			line++;
			System.out.println("Term " + line + ": " + temp);
			String[] termVec = temp.trim().split("\\s+");
			if (termVec[0].isEmpty() || termVec[1].isEmpty()) {
				continue;
			}
			DictTerm dt = new DictTerm(termVec[0], termVec[1]);
			if (termVec[1].equals("1")) {
				if (!posTerms.contains(dt))
					posTerms.add(dt);
			} else if (termVec[1].equals("-1")) {
				if (!negTerms.contains(dt))
					negTerms.add(dt);
			} else {
				if (!othTerms.contains(dt))
					;
				othTerms.add(dt);
			}
		}
		posCount = posTerms.size();
		negCount = negTerms.size();
		othCount = othTerms.size();

		br.close();
	}

	public String mtoDict() {
		String positive = "";
		for (DictTerm dt : posTerms) {
			positive += (dt.getTerm() + "\r\n");
		}

		for (DictTerm dt : negTerms) {
			positive += (dt.getTerm() + "\r\n");
		}

		System.out.println(positive);
		return positive;
	}

	public String mtoString() {
		String positive = "";
		for (DictTerm dt : posTerms) {
			positive += (dt.getTerm() + "|");
		}

		positive += "\r\n";
		for (DictTerm dt : negTerms) {
			positive += (dt.getTerm() + "|");
		}

		positive += "\r\n";

		for (DictTerm dt : othTerms) {
			positive += (dt.getTerm() + "|");
		}
		System.out.println(positive);
		return positive;
	}

	public void saveToDict(String path) throws IOException {
		FileWriter fw = new FileWriter(new File(path));
		fw.write(this.mtoString());
		fw.close();
	}


	public static void main(String args[]) throws IOException {
		String text = "";
		OptiDict od = OptiDict.getInstance();
		Vector<TaggerPoint> tv = od.getTaggerPointByDict(text);
		for (TaggerPoint tp : tv) {
			System.out.println(text.substring(tp.getStart(), tp.getEnd()));
		}
	}
}
