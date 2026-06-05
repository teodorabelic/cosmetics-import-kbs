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

	public static ImportRequest demo() {
		ImportRequest request = new ImportRequest("Vitamin C serum", Category.SKINCARE, "South Korea", 8.2, 1.4,
				0.1, 0.2, 19.9, 16.5, 24.0, 120, 18, 40, 18, 0.92);
		request.setProductType(ProductType.TREND);
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 1, 9));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 2, 8));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 3, 7));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 10, 4));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 20, 3));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 10, 4));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 25, 3));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 50, 3));
		request.getStockEvents().add(new StockChangeEvent(request.getProductName(), 20, 30, 18));
		return request;
	}

	public static ImportRequest approvedDemo() {
		return demo();
	}

	public static ImportRequest rejectedByPriceDemo() {
		ImportRequest request = new ImportRequest("Luxury night cream", Category.SKINCARE, "Japan", 28.0, 5.0, 0.12,
				0.2, 62.0, 35.0, 42.0, 95, 30, 20, 24, 0.9);
		request.setProductType(ProductType.LUXURY);
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 1, 4));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 2, 5));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 14, 4));
		request.getShipmentEvents().add(new ShipmentReceivedEvent(request.getProductName(), 120, 60));
		return request;
	}

	public static ImportRequest rejectedBySupplierDemo() {
		ImportRequest request = new ImportRequest("Matte lipstick", Category.MAKEUP, "Turkey", 3.2, 0.6, 0.05, 0.2,
				8.5, 7.5, 11.0, 110, 22, 20, 18, 0.45);
		request.setProductType(ProductType.MASS);
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 1, 6));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 2, 7));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 20, 6));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 30, 5));
		return request;
	}

	public static ImportRequest lowStockDemo() {
		ImportRequest request = new ImportRequest("Repair hair mask", Category.HAIRCARE, "Italy", 4.5, 0.8, 0.0, 0.2,
				13.0, 11.0, 16.0, 70, 3, 25, 20, 0.85);
		request.setProductType(ProductType.PROFESSIONAL);
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 1, 5));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 2, 6));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 5, 5));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 5, 4));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 20, 4));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 45, 3));
		request.getStockEvents().add(new StockChangeEvent(request.getProductName(), 15, 16, 3));
		request.getStockEvents().add(new StockChangeEvent(request.getProductName(), 30, 24, 16));
		request.getStockEvents().add(new StockChangeEvent(request.getProductName(), 55, 30, 24));
		return request;
	}

	public static ImportRequest decliningSalesDemo() {
		ImportRequest request = new ImportRequest("Body shimmer oil", Category.BODYCARE, "France", 6.5, 1.0, 0.0, 0.2,
				18.0, 16.0, 24.0, 30, 140, 35, 18, 0.88);
		request.setProductType(ProductType.STANDARD);
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 1, 1));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 2, 1));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 3, 2));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 12, 8));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 20, 9));
		request.getSalesHistory().add(new SalesRecord(request.getProductName(), 28, 7));
		request.getShipmentEvents().add(new ShipmentReceivedEvent(request.getProductName(), 180, 80));
		return request;
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
