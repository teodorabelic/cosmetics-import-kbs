package com.ftn.sbnz.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;

class ModelApplicationTests {

	@Test
	void calculatesRecommendedPricesAndProfit() {
		ImportRequest request = pricingRequest();
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
		assertThat(result.getExplanation()).contains("Hipoteza ImportRecommended je potvrđena.",
				"Potražnja je visoka.", "D2C cena je konkurentna.");
	}

	@Test
	void demoContainsCepEventsForPresentation() {
		ImportRequest request = pricingRequest();
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 10, 4));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 25, 3));
		request.getSalesEvents().add(new SalesEvent(request.getProductName(), 50, 3));
		request.getStockEvents().add(new StockChangeEvent(request.getProductName(), 20, 30, 18));

		assertThat(request.getSalesEvents()).hasSize(3);
		assertThat(request.getStockEvents()).hasSize(1);
	}

	private ImportRequest pricingRequest() {
		ImportRequest request = new ImportRequest("Serum", Category.SKINCARE, "South Korea", 8.2, 1.4,
				0.1, 0.2, 19.9, 16.5, 24.0, 120, 18, 40, 18, 0.92);
		request.setProductType(ProductType.TREND);
		return request;
	}
}
