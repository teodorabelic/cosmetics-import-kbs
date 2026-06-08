package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportAssessmentExplanation;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.InventoryProduct;
import com.ftn.sbnz.model.ProductPolicy;
import com.ftn.sbnz.model.RecommendationQueryResult;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import-assessment")
public class ImportAssessmentController {

	private final ImportAssessmentService assessmentService;

	public ImportAssessmentController(ImportAssessmentService assessmentService) {
		this.assessmentService = assessmentService;
	}

	@GetMapping("/demo")
	public ImportAssessment demo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("approved"));
	}

	@GetMapping("/products")
	public List<InventoryProduct> products() {
		return assessmentService.getInventoryProducts();
	}

	@GetMapping("/policies")
	public List<ProductPolicy> policies() {
		return assessmentService.getProductPolicies();
	}

	@GetMapping("/demo/approved")
	public ImportAssessment approvedDemo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("approved"));
	}

	@GetMapping("/demo/rejected-price")
	public ImportAssessment rejectedByPriceDemo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("rejected-price"));
	}

	@GetMapping("/demo/rejected-supplier")
	public ImportAssessment rejectedBySupplierDemo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("rejected-supplier"));
	}

	@GetMapping("/demo/low-stock")
	public ImportAssessment lowStockDemo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("low-stock"));
	}

	@GetMapping("/demo/declining-sales")
	public ImportAssessment decliningSalesDemo() {
		return assessmentService.evaluate(assessmentService.getDemoRequest("declining-sales"));
	}

	@GetMapping("/demo-request/approved")
	public ImportRequest approvedDemoRequest() {
		return assessmentService.getDemoRequest("approved");
	}

	@PostMapping("/evaluate")
	public ImportAssessment evaluate(@RequestBody ImportRequest request) {
		return assessmentService.evaluate(request);
	}

	@PostMapping("/recommendation-query")
	public RecommendationQueryResult recommendationQuery(@RequestBody ImportRequest request) {
		return assessmentService.checkImportRecommendation(request);
	}

	@PostMapping("/explain")
	public ImportAssessmentExplanation explain(@RequestBody ImportRequest request) {
		return assessmentService.explain(request);
	}
}
