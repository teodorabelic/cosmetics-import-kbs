package com.ftn.sbnz.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportDecision;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.RecommendationQueryResult;
import com.ftn.sbnz.model.SalesTrend;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceApplicationTests {

	@Autowired
	private ImportAssessmentService assessmentService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void recommendationQueryConfirmsDemoImport() {
		RecommendationQueryResult result = assessmentService
				.checkImportRecommendation(assessmentService.getDemoRequest("approved"));

		assertThat(result.isImportRecommended()).isTrue();
		assertThat(result.getFactors()).contains("HighDemand", "ImportRecommended");
		assertThat(result.getExplanation()).contains("Hipoteza ImportRecommended je potvrđena.");
	}

	@Test
	void approvedDemoIsApproved() {
		ImportAssessment assessment = assessmentService.evaluate(assessmentService.getDemoRequest("approved"));

		assertThat(assessment.getDecision()).isEqualTo(ImportDecision.APPROVE);
		assertThat(assessment.getRecommendations()).anyMatch(recommendation -> recommendation.contains("Uvoz je isplativ"));
	}

	@Test
	void rejectedPriceDemoIsRejectedAsNotCompetitive() {
		ImportAssessment assessment = assessmentService.evaluate(assessmentService.getDemoRequest("rejected-price"));

		assertThat(assessment.getDecision()).isEqualTo(ImportDecision.REJECT);
		assertThat(assessment.getRecommendations()).anyMatch(recommendation -> recommendation.contains("nije konkurentan"));
	}

	@Test
	void rejectedNonCompetitiveProductDoesNotConfirmBackwardImportRecommendation() {
		ImportRequest request = assessmentService.getDemoRequest("approved");
		request.setDemandScore(1);
		request.setCompetitorMinPrice(9);
		request.setCompetitorMaxPrice(10);

		RecommendationQueryResult result = assessmentService.checkImportRecommendation(request);

		assertThat(result.getAssessment().getDecision()).isEqualTo(ImportDecision.REJECT);
		assertThat(result.isImportRecommended()).isFalse();
		assertThat(result.getFactors()).doesNotContain("HighDemand", "CompetitiveD2CPrice", "ImportRecommended");
		assertThat(result.getExplanation()).contains("Hipoteza ImportRecommended nije potvrđena.",
				"Lanac potražnje je prekinut: nedostaje faktor HighDemand.",
				"Lanac konkurentnosti je prekinut: nedostaje faktor CompetitiveD2CPrice.");
		assertThat(result.getAssessment().getRecommendations())
				.doesNotContain("Backward query ImportRecommended je potvrđen kroz lanac faktora.");
	}

	@Test
	void plannedSellingPriceAboveCompetitorsIsRisky() {
		ImportRequest request = assessmentService.getDemoRequest("approved");
		request.setExpectedSellingPrice(1000);

		ImportAssessment assessment = assessmentService.evaluate(request);

		assertThat(assessment.getDecision()).isEqualTo(ImportDecision.APPROVE_WITH_CAUTION);
		assertThat(assessment.getRecommendations())
				.contains("Planirana prodajna cena je iznad maksimalne konkurentske cene, pa je uvoz rizičan dok se cena ne uskladi sa tržištem.");
	}

	@Test
	void rejectedSupplierDemoIsRejectedAsRiskySupplier() {
		ImportAssessment assessment = assessmentService.evaluate(assessmentService.getDemoRequest("rejected-supplier"));

		assertThat(assessment.getDecision()).isEqualTo(ImportDecision.REJECT);
		assertThat(assessment.getRecommendations()).contains("Uvoz nije preporučen zbog rizičnog dobavljača.");
	}

	@Test
	void lowStockDemoTriggersCriticalStockRecommendations() {
		ImportAssessment assessment = assessmentService.evaluate(assessmentService.getDemoRequest("low-stock"));

		assertThat(assessment.getRecommendations()).contains("Zalihe su kritične: potrebno je hitno naručivanje.",
				"Detektovano je naglo praznjenje zaliha u poslednjih sat vremena.",
				"Detektovana je učestala prodaja u poslednjem satu.",
				"Zalihe su se učestalo menjale u poslednjem satu.");
		assertThat(assessment.getSuggestedOrderQuantity()).isGreaterThan(0);
	}

	@Test
	void decliningSalesDemoDetectsDecliningTrendAndReducesOrder() {
		ImportAssessment assessment = assessmentService.evaluate(assessmentService.getDemoRequest("declining-sales"));

		assertThat(assessment.getSalesTrend()).isEqualTo(SalesTrend.DECLINING);
		assertThat(assessment.getSuggestedOrderQuantity()).isZero();
		assertThat(assessment.getRecommendations()).anyMatch(recommendation -> recommendation.contains("Opadajući trend"));
		assertThat(assessment.getRecommendations()).contains("Primljena je nova isporuka, ali nema prodaje u poslednja 24 sata.");
	}

	@Test
	void demoEndpointReturnsApprovedScenario() throws Exception {
		mockMvc.perform(get("/api/import-assessment/demo/approved"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.decision").value("APPROVE"))
				.andExpect(jsonPath("$.recommendedD2CPrice").isNumber());
	}

	@Test
	void explainEndpointReturnsDetailedExplanation() throws Exception {
		mockMvc.perform(post("/api/import-assessment/explain")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(assessmentService.getDemoRequest("approved"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.decision").value("APPROVE"))
				.andExpect(jsonPath("$.importRecommended").value(true))
				.andExpect(jsonPath("$.factors").isArray())
				.andExpect(jsonPath("$.activatedRules").isArray());
	}

}
