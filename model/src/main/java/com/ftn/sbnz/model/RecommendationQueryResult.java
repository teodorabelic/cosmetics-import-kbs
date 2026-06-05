package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class RecommendationQueryResult {

	private ImportAssessment assessment;
	private boolean importRecommended;
	private List<String> factors = new ArrayList<>();
	private List<String> explanation = new ArrayList<>();

	public RecommendationQueryResult() {
	}

	public RecommendationQueryResult(ImportAssessment assessment, boolean importRecommended, List<String> factors) {
		this.assessment = assessment;
		this.importRecommended = importRecommended;
		this.factors = factors;
		this.explanation = buildExplanation(importRecommended, factors);
	}

	private List<String> buildExplanation(boolean importRecommended, List<String> factors) {
		List<String> result = new ArrayList<>();
		if (importRecommended) {
			result.add("Hipoteza ImportRecommended je potvrdena.");
		} else {
			result.add("Hipoteza ImportRecommended nije potvrdena.");
		}
		if (factors.contains("HighDemand")) {
			result.add("Potraznja je visoka.");
		}
		if (factors.contains("AcceptableTotalCost")) {
			result.add("Ukupni trosak je prihvatljiv.");
		}
		if (factors.contains("CompetitiveD2CPrice")) {
			result.add("D2C cena je konkurentna.");
		}
		if (factors.contains("PositiveExpectedProfit")) {
			result.add("Ocekivani profit je pozitivan.");
		}
		if (!importRecommended) {
			if (!factors.contains("HighDemand")) {
				result.add("Lanac potraznje je prekinut: nedostaje faktor HighDemand.");
			}
			if (!factors.contains("AcceptableTotalCost")) {
				result.add("Lanac troskova je prekinut: nedostaje faktor AcceptableTotalCost.");
			}
			if (!factors.contains("CompetitiveD2CPrice")) {
				result.add("Lanac konkurentnosti je prekinut: nedostaje faktor CompetitiveD2CPrice.");
			}
			if (!factors.contains("PositiveExpectedProfit")) {
				result.add("Lanac profita je prekinut: nedostaje faktor PositiveExpectedProfit.");
			}
			result.add("Nedostaje kompletan lanac faktora koji vodi do preporuke za uvoz.");
		}
		return result;
	}

	public ImportAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(ImportAssessment assessment) {
		this.assessment = assessment;
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

	public List<String> getExplanation() {
		return explanation;
	}

	public void setExplanation(List<String> explanation) {
		this.explanation = explanation;
	}
}
