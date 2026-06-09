package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class ImportAssessmentExplanation {

	private ImportDecision decision;
	private RiskLevel riskLevel;
	private String mainReason;
	private double landedCost;
	private double recommendedB2BPrice;
	private double recommendedD2CPrice;
	private double expectedProfit;
	private double averageDailySales7;
	private double averageDailySales30;
	private double remainingStockDays;
	private int suggestedOrderQuantity;
	private SalesTrend salesTrend;
	private boolean importRecommended;
	private List<String> factors = new ArrayList<>();
	private List<String> queryExplanation = new ArrayList<>();
	private List<String> activatedRules = new ArrayList<>();
	private List<String> recommendations = new ArrayList<>();

	public ImportAssessmentExplanation() {
	}

	public ImportAssessmentExplanation(RecommendationQueryResult queryResult) {
		ImportAssessment assessment = queryResult.getAssessment();
		this.decision = assessment.getDecision();
		this.riskLevel = assessment.getRiskLevel();
		this.mainReason = assessment.getRecommendations().isEmpty() ? "Nema dodatnog objašnjenja."
				: assessment.getRecommendations().get(0);
		this.landedCost = assessment.getLandedCost();
		this.recommendedB2BPrice = assessment.getRecommendedB2BPrice();
		this.recommendedD2CPrice = assessment.getRecommendedD2CPrice();
		this.expectedProfit = assessment.getExpectedProfit();
		this.averageDailySales7 = assessment.getAverageDailySales7();
		this.averageDailySales30 = assessment.getAverageDailySales30();
		this.remainingStockDays = assessment.getRemainingStockDays();
		this.suggestedOrderQuantity = assessment.getSuggestedOrderQuantity();
		this.salesTrend = assessment.getSalesTrend();
		this.importRecommended = queryResult.isImportRecommended();
		this.factors = queryResult.getFactors();
		this.queryExplanation = queryResult.getExplanation();
		this.activatedRules = assessment.getActivatedRules();
		this.recommendations = assessment.getRecommendations();
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

	public String getMainReason() {
		return mainReason;
	}

	public void setMainReason(String mainReason) {
		this.mainReason = mainReason;
	}

	public double getLandedCost() {
		return landedCost;
	}

	public void setLandedCost(double landedCost) {
		this.landedCost = landedCost;
	}

	public double getRecommendedB2BPrice() {
		return recommendedB2BPrice;
	}

	public void setRecommendedB2BPrice(double recommendedB2BPrice) {
		this.recommendedB2BPrice = recommendedB2BPrice;
	}

	public double getRecommendedD2CPrice() {
		return recommendedD2CPrice;
	}

	public void setRecommendedD2CPrice(double recommendedD2CPrice) {
		this.recommendedD2CPrice = recommendedD2CPrice;
	}

	public double getExpectedProfit() {
		return expectedProfit;
	}

	public void setExpectedProfit(double expectedProfit) {
		this.expectedProfit = expectedProfit;
	}

	public double getAverageDailySales7() {
		return averageDailySales7;
	}

	public void setAverageDailySales7(double averageDailySales7) {
		this.averageDailySales7 = averageDailySales7;
	}

	public double getAverageDailySales30() {
		return averageDailySales30;
	}

	public void setAverageDailySales30(double averageDailySales30) {
		this.averageDailySales30 = averageDailySales30;
	}

	public double getRemainingStockDays() {
		return remainingStockDays;
	}

	public void setRemainingStockDays(double remainingStockDays) {
		this.remainingStockDays = remainingStockDays;
	}

	public int getSuggestedOrderQuantity() {
		return suggestedOrderQuantity;
	}

	public void setSuggestedOrderQuantity(int suggestedOrderQuantity) {
		this.suggestedOrderQuantity = suggestedOrderQuantity;
	}

	public SalesTrend getSalesTrend() {
		return salesTrend;
	}

	public void setSalesTrend(SalesTrend salesTrend) {
		this.salesTrend = salesTrend;
	}

	public boolean isImportRecommended() {
		return importRecommended;
	}

	public void setImportRecommended(boolean importRecommended) {
		this.importRecommended = importRecommended;
	}

	public List<String> getFactors() {
		return factors;
	}

	public void setFactors(List<String> factors) {
		this.factors = factors;
	}

	public List<String> getQueryExplanation() {
		return queryExplanation;
	}

	public void setQueryExplanation(List<String> queryExplanation) {
		this.queryExplanation = queryExplanation;
	}

	public List<String> getActivatedRules() {
		return activatedRules;
	}

	public void setActivatedRules(List<String> activatedRules) {
		this.activatedRules = activatedRules;
	}

	public List<String> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<String> recommendations) {
		this.recommendations = recommendations;
	}
}

