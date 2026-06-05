package com.ftn.sbnz.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;

class ModelApplicationTests {

	@Test
	void calculatesRecommendedPricesAndProfit() {
		ImportRequest request = ImportRequest.demo();
		double landedCost = request.calculateLandedCost();

		assertThat(request.calculateB2BPrice(landedCost)).isCloseTo(16.224, within(0.001));
		assertThat(request.calculateD2CPrice(landedCost)).isCloseTo(18.096, within(0.001));
		assertThat(request.calculateExpectedProfit(landedCost)).isCloseTo(890.4, within(0.001));
	}

	@Test
	void buildsRecommendationQueryExplanation() {
		RecommendationQueryResult result = new RecommendationQueryResult(new ImportAssessment("Serum"), true,
				List.of("HighDemand", "CompetitiveD2CPrice", "ImportRecommended"));

		assertThat(result.isImportRecommended()).isTrue();
		assertThat(result.getExplanation()).contains("Hipoteza ImportRecommended je potvrdena.",
				"Potraznja je visoka.", "D2C cena je konkurentna.");
	}

	@Test
	void demoContainsCepEventsForPresentation() {
		ImportRequest request = ImportRequest.demo();

		assertThat(request.getSalesEvents()).hasSize(3);
		assertThat(request.getStockEvents()).hasSize(1);
		assertThat(ImportRequest.decliningSalesDemo().getShipmentEvents()).hasSize(1);
	}
}
