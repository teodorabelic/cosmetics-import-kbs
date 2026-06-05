package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoRunner implements CommandLineRunner {

	private final ImportAssessmentService assessmentService;

	public DemoRunner(ImportAssessmentService assessmentService) {
		this.assessmentService = assessmentService;
	}

	@Override
	public void run(String... args) {
		ImportAssessment assessment = assessmentService.evaluate(ImportRequest.demo());
		System.out.println("=== Demo procene uvoza ===");
		System.out.println("Proizvod: " + assessment.getProductName());
		System.out.println("Ukupna uvozna cena: " + assessment.getLandedCost());
		System.out.println("Marza: " + assessment.getMarginPercent() + "%");
		System.out.println("Preporucena B2B cena: " + assessment.getRecommendedB2BPrice());
		System.out.println("Preporucena D2C cena: " + assessment.getRecommendedD2CPrice());
		System.out.println("Ocekivani profit: " + assessment.getExpectedProfit());
		System.out.println("Prosecna prodaja 7 dana: " + assessment.getAverageDailySales7());
		System.out.println("Prosecna prodaja 30 dana: " + assessment.getAverageDailySales30());
		System.out.println("Preostali dani zaliha: " + assessment.getRemainingStockDays());
		System.out.println("Trend prodaje: " + assessment.getSalesTrend());
		System.out.println("Odluka: " + assessment.getDecision());
		System.out.println("Rizik: " + assessment.getRiskLevel());
		System.out.println("Predlozena kolicina: " + assessment.getSuggestedOrderQuantity());
		System.out.println("Aktivirana pravila: " + assessment.getActivatedRules());
		System.out.println("Preporuke: " + assessment.getRecommendations());
	}
}
