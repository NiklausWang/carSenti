package com.run.whunlp.dict;

class DictTerm {
	private String term = "";
	private String polarity = "";

	public DictTerm(String term, String polarity) {
		this.term = term;
		this.polarity = polarity;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getPolarity() {
		return polarity;
	}

	public void setPolarity(String polarity) {
		this.polarity = polarity;
	}

}
