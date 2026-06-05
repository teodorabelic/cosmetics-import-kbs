package com.ftn.sbnz.model;

public class ProductPolicy {

	private ProductType productType;
	private int minStockThreshold;
	private double orderMultiplier;

	public ProductPolicy() {
	}

	public ProductPolicy(ProductType productType, int minStockThreshold, double orderMultiplier) {
		this.productType = productType;
		this.minStockThreshold = minStockThreshold;
		this.orderMultiplier = orderMultiplier;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public int getMinStockThreshold() {
		return minStockThreshold;
	}

	public void setMinStockThreshold(int minStockThreshold) {
		this.minStockThreshold = minStockThreshold;
	}

	public double getOrderMultiplier() {
		return orderMultiplier;
	}

	public void setOrderMultiplier(double orderMultiplier) {
		this.orderMultiplier = orderMultiplier;
	}
}
