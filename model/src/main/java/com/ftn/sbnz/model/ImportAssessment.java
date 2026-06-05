package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class ImportAssessment {

	private String productName;
	private double landedCost;
	private double marginPercent;
	private double recommendedB2BPrice;
	private double recommendedD2CPrice;
	private double expectedProfit;
	private double averageDailySales7;
	private double averageDailySales30;
	private double remainingStockDays;
	private SalesTrend salesTrend;
	private ImportDecision decision;
	private RiskLevel riskLevel;
	private int suggestedOrderQuantity;
	private final List<String> recommendations = new ArrayList<>();
	private final List<String> activatedRules = new ArrayList<>();

	public ImportAssessment() {
	}

	public ImportAssessment(String productName) {
		this.productName = productName;
	}

	public void addRecommendation(String recommendation) {
		if (!recommendations.contains(recommendation)) {
			recommendations.add(recommendation);
		}
	}

	public boolean hasRecommendation(String recommendation) {
		return recommendations.contains(recommendation);
	}

	public void addActivatedRule(String ruleName) {
		if (!activatedRules.contains(ruleName)) {
			activatedRules.add(ruleName);
		}
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getLandedCost() {
		return landedCost;
	}

	public void setLandedCost(double landedCost) {
		this.landedCost = round(landedCost);
	}

	public double getMarginPercent() {
		return marginPercent;
	}

	public void setMarginPercent(double marginPercent) {
		this.marginPercent = round(marginPercent);
	}

	public double getRecommendedB2BPrice() {
		return recommendedB2BPrice;
	}

	public void setRecommendedB2BPrice(double recommendedB2BPrice) {
		this.recommendedB2BPrice = round(recommendedB2BPrice);
	}

	public double getRecommendedD2CPrice() {
		return recommendedD2CPrice;
	}

	public void setRecommendedD2CPrice(double recommendedD2CPrice) {
		this.recommendedD2CPrice = round(recommendedD2CPrice);
	}

	public double getExpectedProfit() {
		return expectedProfit;
	}

	public void setExpectedProfit(double expectedProfit) {
		this.expectedProfit = round(expectedProfit);
	}

	public double getAverageDailySales7() {
		return averageDailySales7;
	}

	public void setAverageDailySales7(double averageDailySales7) {
		this.averageDailySales7 = round(averageDailySales7);
	}

	public double getAverageDailySales30() {
		return averageDailySales30;
	}

	public void setAverageDailySales30(double averageDailySales30) {
		this.averageDailySales30 = round(averageDailySales30);
	}

	public double getRemainingStockDays() {
		return remainingStockDays;
	}

	public void setRemainingStockDays(double remainingStockDays) {
		this.remainingStockDays = round(remainingStockDays);
	}

	public SalesTrend getSalesTrend() {
		return salesTrend;
	}

	public void setSalesTrend(SalesTrend salesTrend) {
		this.salesTrend = salesTrend;
	}

	public ImportDecision getDecision() {
		return decision;
	}

	public void setDecision(ImportDecision decision) {
		this.decision = decision;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public int getSuggestedOrderQuantity() {
		return suggestedOrderQuantity;
	}

	public void setSuggestedOrderQuantity(int suggestedOrderQuantity) {
		this.suggestedOrderQuantity = suggestedOrderQuantity;
	}

	public List<String> getRecommendations() {
		return recommendations;
	}

	public List<String> getActivatedRules() {
		return activatedRules;
	}

	private double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
