package com.ftn.sbnz.model;

public class ImportRequest {

	private String productName;
	private Category category;
	private String countryOfOrigin;
	private double purchasePrice;
	private double shippingCost;
	private double customsRate;
	private double vatRate;
	private double expectedSellingPrice;
	private int monthlyDemand;
	private int currentStock;
	private int minStock;
	private int shelfLifeMonths;
	private double supplierReliability;

	public ImportRequest() {
	}

	public ImportRequest(String productName, Category category, String countryOfOrigin, double purchasePrice,
			double shippingCost, double customsRate, double vatRate, double expectedSellingPrice, int monthlyDemand,
			int currentStock, int minStock, int shelfLifeMonths, double supplierReliability) {
		this.productName = productName;
		this.category = category;
		this.countryOfOrigin = countryOfOrigin;
		this.purchasePrice = purchasePrice;
		this.shippingCost = shippingCost;
		this.customsRate = customsRate;
		this.vatRate = vatRate;
		this.expectedSellingPrice = expectedSellingPrice;
		this.monthlyDemand = monthlyDemand;
		this.currentStock = currentStock;
		this.minStock = minStock;
		this.shelfLifeMonths = shelfLifeMonths;
		this.supplierReliability = supplierReliability;
	}

	public static ImportRequest demo() {
		return new ImportRequest("Vitamin C serum", Category.SKINCARE, "South Korea", 8.2, 1.4, 0.1, 0.2, 19.9, 120,
				18, 40, 18, 0.92);
	}

	public double calculateLandedCost() {
		double baseCost = purchasePrice + shippingCost;
		return baseCost + (baseCost * customsRate) + (baseCost * vatRate);
	}

	public double calculateMarginPercent(double landedCost) {
		if (expectedSellingPrice <= 0) {
			return 0;
		}
		return ((expectedSellingPrice - landedCost) / expectedSellingPrice) * 100;
	}

	public int calculateDefaultOrderQuantity() {
		return Math.max(0, (monthlyDemand * 2) - currentStock);
	}

	public int calculateUrgentOrderQuantity() {
		return Math.max(calculateDefaultOrderQuantity(), (minStock - currentStock) + monthlyDemand);
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public double getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}

	public double getCustomsRate() {
		return customsRate;
	}

	public void setCustomsRate(double customsRate) {
		this.customsRate = customsRate;
	}

	public double getVatRate() {
		return vatRate;
	}

	public void setVatRate(double vatRate) {
		this.vatRate = vatRate;
	}

	public double getExpectedSellingPrice() {
		return expectedSellingPrice;
	}

	public void setExpectedSellingPrice(double expectedSellingPrice) {
		this.expectedSellingPrice = expectedSellingPrice;
	}

	public int getMonthlyDemand() {
		return monthlyDemand;
	}

	public void setMonthlyDemand(int monthlyDemand) {
		this.monthlyDemand = monthlyDemand;
	}

	public int getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(int currentStock) {
		this.currentStock = currentStock;
	}

	public int getMinStock() {
		return minStock;
	}

	public void setMinStock(int minStock) {
		this.minStock = minStock;
	}

	public int getShelfLifeMonths() {
		return shelfLifeMonths;
	}

	public void setShelfLifeMonths(int shelfLifeMonths) {
		this.shelfLifeMonths = shelfLifeMonths;
	}

	public double getSupplierReliability() {
		return supplierReliability;
	}

	public void setSupplierReliability(double supplierReliability) {
		this.supplierReliability = supplierReliability;
	}
}
