package com.ftn.sbnz.model;

// veza izmedju 2 faktora preporuke
// u bc delu sluzi kao graf zakljucivanja 

public class RecommendationLink {

	private String fromCode;
	private String toCode;

	public RecommendationLink() {
	}

	public RecommendationLink(String fromCode, String toCode) {
		this.fromCode = fromCode;
		this.toCode = toCode;
	}

	public String getFromCode() {
		return fromCode;
	}

	public void setFromCode(String fromCode) {
		this.fromCode = fromCode;
	}

	public String getToCode() {
		return toCode;
	}

	public void setToCode(String toCode) {
		this.toCode = toCode;
	}
}
