package com.ftn.sbnz.model;

public class StockChangeEvent {

	private String productName;
	private int minutesAgo;
	private int quantityBefore;
	private int quantityAfter;

	public StockChangeEvent() {
	}

	public StockChangeEvent(String productName, int minutesAgo, int quantityBefore, int quantityAfter) {
		this.productName = productName;
		this.minutesAgo = minutesAgo;
		this.quantityBefore = quantityBefore;
		this.quantityAfter = quantityAfter;
	}

	public int getDecrease() {
		return Math.max(0, quantityBefore - quantityAfter);
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

	public int getQuantityBefore() {
		return quantityBefore;
	}

	public void setQuantityBefore(int quantityBefore) {
		this.quantityBefore = quantityBefore;
	}

	public int getQuantityAfter() {
		return quantityAfter;
	}

	public void setQuantityAfter(int quantityAfter) {
		this.quantityAfter = quantityAfter;
	}
}
