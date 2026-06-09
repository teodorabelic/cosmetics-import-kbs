package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryProduct {

	private String productName;
	private Category category;
	private ProductType productType;
	private int currentStock;
	private int minStock;
	private String supplier;
	private SalesTrend salesTrend;
	private List<SalesRecord> salesHistory = new ArrayList<>();
	private List<SalesEvent> salesEvents = new ArrayList<>();
	private List<StockChangeEvent> stockEvents = new ArrayList<>();
	private List<ShipmentReceivedEvent> shipmentEvents = new ArrayList<>();

	public InventoryProduct() {
	}

	public InventoryProduct(String productName, Category category, ProductType productType, int currentStock,
			int minStock, String supplier, SalesTrend salesTrend) {
		this(productName, category, productType, currentStock, minStock, supplier, salesTrend, new ArrayList<>());
	}

	public InventoryProduct(String productName, Category category, ProductType productType, int currentStock,
			int minStock, String supplier, SalesTrend salesTrend, List<SalesRecord> salesHistory) {
		this.productName = productName;
		this.category = category;
		this.productType = productType;
		this.currentStock = currentStock;
		this.minStock = minStock;
		this.supplier = supplier;
		this.salesTrend = salesTrend;
		this.salesHistory = salesHistory != null ? salesHistory : new ArrayList<>();
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
		this.productType = productType;
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

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public SalesTrend getSalesTrend() {
		return salesTrend;
	}

	public void setSalesTrend(SalesTrend salesTrend) {
		this.salesTrend = salesTrend;
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
