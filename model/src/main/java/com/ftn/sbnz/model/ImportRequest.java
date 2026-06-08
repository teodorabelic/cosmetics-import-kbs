package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class ImportRequest {

	private String productName;
	private Category category;
	private ProductType productType = ProductType.STANDARD;
	private String countryOfOrigin;
	private double purchasePrice;
	private double shippingCost;
	private double transportRate;
	private double customsRate;
	private double vatRate;
	private double expectedSellingPrice;
	private double competitorMinPrice;
	private double competitorMaxPrice;
	private int demandScore;
	private int monthlyDemand;
	private int currentStock;
	private int minStock;
	private int shelfLifeMonths;
	private int deliveryTimeDays;
	private double supplierReliability;
	private List<SalesRecord> salesHistory = new ArrayList<>();
	private List<SalesEvent> salesEvents = new ArrayList<>();
	private List<StockChangeEvent> stockEvents = new ArrayList<>();
	private List<ShipmentReceivedEvent> shipmentEvents = new ArrayList<>();

	public ImportRequest() {
	}

	public ImportRequest(String productName, Category category, String countryOfOrigin, double purchasePrice,
			double shippingCost, double customsRate, double vatRate, double expectedSellingPrice, int monthlyDemand,
			int currentStock, int minStock, int shelfLifeMonths, double supplierReliability) {
		this(productName, category, countryOfOrigin, purchasePrice, shippingCost, customsRate, vatRate,
				expectedSellingPrice, 0, 0, monthlyDemand, currentStock, minStock, shelfLifeMonths,
				supplierReliability);
	}

	public ImportRequest(String productName, Category category, String countryOfOrigin, double purchasePrice,
			double shippingCost, double customsRate, double vatRate, double expectedSellingPrice,
			double competitorMinPrice, double competitorMaxPrice, int monthlyDemand, int currentStock, int minStock,
			int shelfLifeMonths, double supplierReliability) {
		this.productName = productName;
		this.category = category;
		this.productType = ProductType.STANDARD;
		this.countryOfOrigin = countryOfOrigin;
		this.purchasePrice = purchasePrice;
		this.shippingCost = shippingCost;
		this.customsRate = customsRate;
		this.vatRate = vatRate;
		this.expectedSellingPrice = expectedSellingPrice;
		this.competitorMinPrice = competitorMinPrice;
		this.competitorMaxPrice = competitorMaxPrice;
		this.monthlyDemand = monthlyDemand;
		this.demandScore = deriveDemandScore(monthlyDemand);
		this.currentStock = currentStock;
		this.minStock = minStock;
		this.shelfLifeMonths = shelfLifeMonths;
		this.deliveryTimeDays = 14;
		this.supplierReliability = supplierReliability;
	}

	public double calculateLandedCost() {
		double transportCost = transportRate > 0 ? purchasePrice * transportRate : shippingCost;
		double baseCost = purchasePrice + transportCost;
		double customsCost = isEuOrigin() ? 0 : baseCost * customsRate;
		return baseCost + customsCost + (baseCost * vatRate);
	}

	public double calculateMarginPercent(double landedCost) {
		if (expectedSellingPrice <= 0) {
			return 0;
		}
		return ((expectedSellingPrice - landedCost) / expectedSellingPrice) * 100;
	}

	public double calculateB2BPrice(double landedCost) {
		return landedCost * 1.30;
	}

	public double calculateD2CPrice(double landedCost) {
		return landedCost * 1.45;
	}

	public double calculateExpectedProfit(double landedCost) {
		return Math.max(0, expectedSellingPrice - landedCost) * monthlyDemand;
	}

	public int calculateDefaultOrderQuantity() {
		return Math.max(0, (monthlyDemand * 2) - currentStock);
	}

	public int calculateUrgentOrderQuantity() {
		return Math.max(calculateDefaultOrderQuantity(), (minStock - currentStock) + monthlyDemand);
	}

	public int getEffectiveDemandScore() {
		return demandScore > 0 ? demandScore : deriveDemandScore(monthlyDemand);
	}

	public boolean isEuOrigin() {
		if (countryOfOrigin == null) {
			return false;
		}
		String country = countryOfOrigin.trim().toLowerCase();
		return country.equals("austria") || country.equals("belgium") || country.equals("bulgaria")
				|| country.equals("croatia") || country.equals("cyprus") || country.equals("czech republic")
				|| country.equals("denmark") || country.equals("estonia") || country.equals("finland")
				|| country.equals("france") || country.equals("germany") || country.equals("greece")
				|| country.equals("hungary") || country.equals("ireland") || country.equals("italy")
				|| country.equals("latvia") || country.equals("lithuania") || country.equals("luxembourg")
				|| country.equals("malta") || country.equals("netherlands") || country.equals("poland")
				|| country.equals("portugal") || country.equals("romania") || country.equals("slovakia")
				|| country.equals("slovenia") || country.equals("spain") || country.equals("sweden");
	}

	private int deriveDemandScore(int monthlyDemand) {
		if (monthlyDemand >= 120) {
			return 10;
		}
		if (monthlyDemand >= 100) {
			return 9;
		}
		if (monthlyDemand >= 80) {
			return 8;
		}
		if (monthlyDemand >= 60) {
			return 7;
		}
		if (monthlyDemand >= 40) {
			return 5;
		}
		if (monthlyDemand >= 20) {
			return 3;
		}
		return monthlyDemand > 0 ? 1 : 0;
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

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		if (productType != null) {
			this.productType = productType;
		}
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

	public double getTransportRate() {
		return transportRate;
	}

	public void setTransportRate(double transportRate) {
		this.transportRate = transportRate;
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

	public double getCompetitorMinPrice() {
		return competitorMinPrice;
	}

	public void setCompetitorMinPrice(double competitorMinPrice) {
		this.competitorMinPrice = competitorMinPrice;
	}

	public double getCompetitorMaxPrice() {
		return competitorMaxPrice;
	}

	public void setCompetitorMaxPrice(double competitorMaxPrice) {
		this.competitorMaxPrice = competitorMaxPrice;
	}

	public int getDemandScore() {
		return demandScore;
	}

	public void setDemandScore(int demandScore) {
		this.demandScore = demandScore;
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

	public int getDeliveryTimeDays() {
		return deliveryTimeDays;
	}

	public void setDeliveryTimeDays(int deliveryTimeDays) {
		this.deliveryTimeDays = deliveryTimeDays;
	}

	public double getSupplierReliability() {
		return supplierReliability;
	}

	public void setSupplierReliability(double supplierReliability) {
		this.supplierReliability = supplierReliability;
	}

	public List<SalesRecord> getSalesHistory() {
		return salesHistory;
	}

	public void setSalesHistory(List<SalesRecord> salesHistory) {
		this.salesHistory = salesHistory != null ? salesHistory : new ArrayList<>();
	}

	public List<SalesEvent> getSalesEvents() {
		return salesEvents;
	}

	public void setSalesEvents(List<SalesEvent> salesEvents) {
		this.salesEvents = salesEvents != null ? salesEvents : new ArrayList<>();
	}

	public List<StockChangeEvent> getStockEvents() {
		return stockEvents;
	}

	public void setStockEvents(List<StockChangeEvent> stockEvents) {
		this.stockEvents = stockEvents != null ? stockEvents : new ArrayList<>();
	}

	public List<ShipmentReceivedEvent> getShipmentEvents() {
		return shipmentEvents;
	}

	public void setShipmentEvents(List<ShipmentReceivedEvent> shipmentEvents) {
		this.shipmentEvents = shipmentEvents != null ? shipmentEvents : new ArrayList<>();
	}
}
