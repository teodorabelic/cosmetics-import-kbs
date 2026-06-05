package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportAssessmentExplanation;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.Category;
import com.ftn.sbnz.model.InventoryProduct;
import com.ftn.sbnz.model.ProductPolicy;
import com.ftn.sbnz.model.ProductType;
import com.ftn.sbnz.model.RecommendationFactor;
import com.ftn.sbnz.model.RecommendationLink;
import com.ftn.sbnz.model.RecommendationQueryResult;
import com.ftn.sbnz.model.SalesEvent;
import com.ftn.sbnz.model.SalesRecord;
import com.ftn.sbnz.model.SalesTrend;
import com.ftn.sbnz.model.ShipmentReceivedEvent;
import com.ftn.sbnz.model.StockChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.stereotype.Service;

@Service
public class ImportAssessmentService {

	private final KieContainer kieContainer;

	public ImportAssessmentService(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	public ImportAssessment evaluate(ImportRequest request) {
		return executeAssessment(request).assessment();
	}

	public RecommendationQueryResult checkImportRecommendation(ImportRequest request) {
		AssessmentExecution execution = executeAssessment(request);
		return new RecommendationQueryResult(execution.assessment(), execution.importRecommended(), execution.factors());
	}

	public ImportAssessmentExplanation explain(ImportRequest request) {
		return new ImportAssessmentExplanation(checkImportRecommendation(request));
	}

	public List<InventoryProduct> getInventoryProducts() {
		return List.of(
				inventoryProduct("Vitamin C serum", Category.SKINCARE, ProductType.TREND, 18, 40, "Glow Korea",
						SalesTrend.GROWING),
				inventoryProduct("Repair hair mask", Category.HAIRCARE, ProductType.PROFESSIONAL, 9, 25, "SalonPro",
						SalesTrend.STABLE),
				inventoryProduct("Matte liquid lipstick", Category.MAKEUP, ProductType.MASS, 64, 20, "Color Lab",
						SalesTrend.GROWING),
				inventoryProduct("Body shimmer oil", Category.BODYCARE, ProductType.STANDARD, 132, 35, "Summer Care",
						SalesTrend.DECLINING),
				inventoryProduct("Luxury night cream", Category.SKINCARE, ProductType.LUXURY, 26, 18, "Maison Belle",
						SalesTrend.STABLE),
				inventoryProduct("Peptide eye cream", Category.SKINCARE, ProductType.LUXURY, 7, 12, "Derma Seoul",
						SalesTrend.GROWING),
				inventoryProduct("Keratin shampoo", Category.HAIRCARE, ProductType.MASS, 88, 30, "HairLab",
						SalesTrend.STABLE),
				inventoryProduct("Niacinamide toner", Category.SKINCARE, ProductType.TREND, 14, 35, "Pure K-Beauty",
						SalesTrend.GROWING),
				inventoryProduct("Aloe body lotion", Category.BODYCARE, ProductType.MASS, 11, 18, "Green Care",
						SalesTrend.STABLE),
				inventoryProduct("Rose eau de parfum", Category.FRAGRANCE, ProductType.LUXURY, 42, 10, "Maison Fleur",
						SalesTrend.DECLINING),
				inventoryProduct("Brow styling gel", Category.MAKEUP, ProductType.TREND, 5, 22, "Brow Studio",
						SalesTrend.GROWING),
				inventoryProduct("Retinol night serum", Category.SKINCARE, ProductType.PROFESSIONAL, 31, 16,
						"DermaLab Pro", SalesTrend.STABLE),
				inventoryProduct("Hand repair cream", Category.BODYCARE, ProductType.STANDARD, 74, 25, "Daily Care",
						SalesTrend.DECLINING),
				inventoryProduct("Scalp peeling tonic", Category.HAIRCARE, ProductType.TREND, 16, 28, "Root Science",
						SalesTrend.GROWING),
				inventoryProduct("Pressed powder compact", Category.MAKEUP, ProductType.STANDARD, 39, 15, "Color Lab",
						SalesTrend.STABLE));
	}

	// vezujem dogadjaje za proizvod
	private InventoryProduct inventoryProduct(String productName, Category category, ProductType productType,
			int currentStock, int minStock, String supplier, SalesTrend salesTrend) {
		InventoryProduct product = new InventoryProduct(productName, category, productType, currentStock, minStock,
				supplier, salesTrend, salesHistory(productName, salesTrend));
		product.setSalesEvents(salesEvents(productName, salesTrend));
		product.setStockEvents(stockEvents(productName, salesTrend, currentStock));
		product.setShipmentEvents(shipmentEvents(productName, salesTrend));
		return product;
	}

	// 3 metode za simulaciju eventova
	private List<SalesRecord> salesHistory(String productName, SalesTrend trend) {
		return switch (trend) {
		case GROWING -> List.of(
				new SalesRecord(productName, 1, 10),
				new SalesRecord(productName, 2, 9),
				new SalesRecord(productName, 3, 10),
				new SalesRecord(productName, 4, 8),
				new SalesRecord(productName, 5, 9),
				new SalesRecord(productName, 6, 10),
				new SalesRecord(productName, 7, 9),
				new SalesRecord(productName, 10, 3),
				new SalesRecord(productName, 15, 3),
				new SalesRecord(productName, 20, 4),
				new SalesRecord(productName, 25, 3),
				new SalesRecord(productName, 30, 4));
		case DECLINING -> List.of(
				new SalesRecord(productName, 1, 1),
				new SalesRecord(productName, 2, 1),
				new SalesRecord(productName, 3, 2),
				new SalesRecord(productName, 4, 1),
				new SalesRecord(productName, 5, 1),
				new SalesRecord(productName, 6, 2),
				new SalesRecord(productName, 7, 1),
				new SalesRecord(productName, 10, 8),
				new SalesRecord(productName, 15, 9),
				new SalesRecord(productName, 20, 8),
				new SalesRecord(productName, 25, 9),
				new SalesRecord(productName, 30, 8));
		case STABLE -> List.of(
				new SalesRecord(productName, 1, 5),
				new SalesRecord(productName, 2, 4),
				new SalesRecord(productName, 3, 5),
				new SalesRecord(productName, 4, 4),
				new SalesRecord(productName, 5, 5),
				new SalesRecord(productName, 6, 4),
				new SalesRecord(productName, 7, 5),
				new SalesRecord(productName, 10, 4),
				new SalesRecord(productName, 15, 5),
				new SalesRecord(productName, 20, 4),
				new SalesRecord(productName, 25, 5),
				new SalesRecord(productName, 30, 4));
		};
	}

	private List<SalesEvent> salesEvents(String productName, SalesTrend trend) {
		return switch (trend) {
		case GROWING -> List.of(
				new SalesEvent(productName, 10, 4),
				new SalesEvent(productName, 25, 3),
				new SalesEvent(productName, 45, 5));
		case STABLE -> List.of(new SalesEvent(productName, 35, 2));
		case DECLINING -> List.of();
		};
	}

	private List<StockChangeEvent> stockEvents(String productName, SalesTrend trend, int currentStock) {
		return switch (trend) {
		case GROWING -> List.of(
				new StockChangeEvent(productName, 20, currentStock + 12, currentStock),
				new StockChangeEvent(productName, 50, currentStock + 18, currentStock + 8));
		case STABLE -> List.of(new StockChangeEvent(productName, 80, currentStock + 2, currentStock));
		case DECLINING -> List.of(new StockChangeEvent(productName, 90, currentStock - 20, currentStock));
		};
	}

	private List<ShipmentReceivedEvent> shipmentEvents(String productName, SalesTrend trend) {
		return trend == SalesTrend.DECLINING ? List.of(new ShipmentReceivedEvent(productName, 120, 40)) : List.of();
	}

	private AssessmentExecution executeAssessment(ImportRequest request) {
		ImportAssessment assessment = new ImportAssessment(request.getProductName());
		KieSession kieSession = kieContainer.newKieSession("importAssessmentSession");
		try {
			insertProductPolicies(kieSession);
			insertRecommendationLinks(kieSession);
			kieSession.insert(request);
			kieSession.insert(assessment);
			for (SalesRecord salesRecord : request.getSalesHistory()) {
				kieSession.insert(salesRecord);
			}
			// eventovi se ubacuju u KieSession
			insertTimedEvents(kieSession, request);
			// trigger svih pravila
			kieSession.fireAllRules();
			// provera BC query-a
			QueryResults queryResults = kieSession.getQueryResults("ImportRecommended", request.getProductName());
			// ako je queryResults.size() > 0 - dokazano da je preporucen uvoz
			return new AssessmentExecution(assessment, queryResults.size() > 0, collectFactors(kieSession));
		} finally {
			kieSession.dispose();
		}
	}

	// ubacuje events u KieSession
	private void insertTimedEvents(KieSession kieSession, ImportRequest request) {
		List<TimedEvent> events = new ArrayList<>();
		for (SalesEvent salesEvent : request.getSalesEvents()) {
			events.add(new TimedEvent(salesEvent.getMinutesAgo(), salesEvent));
		}
		for (StockChangeEvent stockEvent : request.getStockEvents()) {
			events.add(new TimedEvent(stockEvent.getMinutesAgo(), stockEvent));
		}
		for (ShipmentReceivedEvent shipmentEvent : request.getShipmentEvents()) {
			events.add(new TimedEvent(shipmentEvent.getMinutesAgo(), shipmentEvent));
		}
		if (events.isEmpty()) {
			return;
		}

		events.sort(Comparator.comparingInt(TimedEvent::minutesAgo).reversed());
		SessionPseudoClock clock = kieSession.getSessionClock();
		int currentMinute = 0;
		int newestMinute = events.get(0).minutesAgo();
		for (TimedEvent event : events) {
			int eventMinute = newestMinute - event.minutesAgo();
			clock.advanceTime(eventMinute - currentMinute, TimeUnit.MINUTES);
			currentMinute = eventMinute;
			kieSession.insert(event.fact());
		}
		clock.advanceTime(newestMinute - currentMinute, TimeUnit.MINUTES);
	}

	private List<String> collectFactors(KieSession kieSession) {
		List<String> factors = new ArrayList<>();
		for (Object object : kieSession.getObjects()) {
			if (object instanceof RecommendationFactor factor && !factors.contains(factor.getCode())) {
				factors.add(factor.getCode());
			}
		}
		factors.sort(Comparator.naturalOrder());
		return factors;
	}

	private void insertProductPolicies(KieSession kieSession) {
		kieSession.insert(new ProductPolicy(ProductType.LUXURY, 5, 0.8));
		kieSession.insert(new ProductPolicy(ProductType.MASS, 10, 1.0));
		kieSession.insert(new ProductPolicy(ProductType.PROFESSIONAL, 8, 0.9));
		kieSession.insert(new ProductPolicy(ProductType.TREND, 20, 1.4));
		kieSession.insert(new ProductPolicy(ProductType.STANDARD, 10, 1.0));
	}

	// za bc - graf zakljucivanja
	private void insertRecommendationLinks(KieSession kieSession) {
		kieSession.insert(new RecommendationLink("HighDemand", "DemandPotential"));
		kieSession.insert(new RecommendationLink("DemandPotential", "ImportOpportunity"));
		kieSession.insert(new RecommendationLink("AcceptableTotalCost", "CostAcceptable"));
		kieSession.insert(new RecommendationLink("CostAcceptable", "ImportOpportunity"));
		kieSession.insert(new RecommendationLink("CompetitiveD2CPrice", "ImportOpportunity"));
		kieSession.insert(new RecommendationLink("PositiveExpectedProfit", "ImportOpportunity"));
		kieSession.insert(new RecommendationLink("ImportOpportunity", "ImportRecommended"));
	}

	private record AssessmentExecution(ImportAssessment assessment, boolean importRecommended, List<String> factors) {
	}

	private record TimedEvent(int minutesAgo, Object fact) {
	}
}
