package com.ftn.sbnz.service;

import com.ftn.sbnz.kjar.TemplateRuleGenerator;
import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportAssessmentExplanation;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.InventoryProduct;
import com.ftn.sbnz.model.ProductPolicy;
import com.ftn.sbnz.model.RecommendationFactor;
import com.ftn.sbnz.model.RecommendationLink;
import com.ftn.sbnz.model.RecommendationQueryResult;
import com.ftn.sbnz.model.SalesEvent;
import com.ftn.sbnz.model.SalesRecord;
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
	private final ImportTestDataRepository testDataRepository;

	public ImportAssessmentService(KieContainer kieContainer, ImportTestDataRepository testDataRepository) {
		this.kieContainer = kieContainer;
		this.testDataRepository = testDataRepository;
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
		return testDataRepository.loadInventoryProducts();
	}

	public List<ProductPolicy> getProductPolicies() {
		return TemplateRuleGenerator.loadProductPolicies();
	}

	public ImportRequest getDemoRequest(String scenario) {
		return testDataRepository.loadDemoRequest(scenario);
	}

	private AssessmentExecution executeAssessment(ImportRequest request) {
		ImportAssessment assessment = new ImportAssessment(request.getProductName());
		KieSession kieSession = kieContainer.newKieSession("importAssessmentSession");
		try {
			insertRecommendationLinks(kieSession);
			kieSession.insert(request);
			kieSession.insert(assessment);
			for (SalesRecord salesRecord : request.getSalesHistory()) {
				kieSession.insert(salesRecord);
			}
			insertTimedEvents(kieSession, request);
			kieSession.fireAllRules();
			QueryResults queryResults = kieSession.getQueryResults("ImportRecommended", request.getProductName());
			return new AssessmentExecution(assessment, queryResults.size() > 0, collectFactors(kieSession));
		} finally {
			kieSession.dispose();
		}
	}

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
