package com.ftn.sbnz.model;

public class SalesEvent {

	private String productName;
	private int minutesAgo;
	private int quantity;

	public SalesEvent() {
	}

	public SalesEvent(String productName, int minutesAgo, int quantity) {
		this.productName = productName;
		this.minutesAgo = minutesAgo;
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getMinutesAgo() {
		return minutesAgo;
	}

	public void setMinutesAgo(int minutesAgo) {
		this.minutesAgo = minutesAgo;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
