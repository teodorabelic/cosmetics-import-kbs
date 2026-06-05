package com.ftn.sbnz.model;

public class RecommendationFactor {

	private String productName;
	private String code;

	public RecommendationFactor() {
	}

	public RecommendationFactor(String productName, String code) {
		this.productName = productName;
		this.code = code;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
