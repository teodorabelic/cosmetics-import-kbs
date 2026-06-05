package com.ftn.sbnz.model;

public class SalesRecord {

	private String productName;
	private int daysAgo;
	private int quantity;

	public SalesRecord() {
	}

	public SalesRecord(String productName, int daysAgo, int quantity) {
		this.productName = productName;
		this.daysAgo = daysAgo;
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
