package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportAssessmentExplanation;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.InventoryProduct;
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
		return assessmentService.evaluate(ImportRequest.demo());
	}

	@GetMapping("/products")
	public List<InventoryProduct> products() {
		return assessmentService.getInventoryProducts();
	}

	@GetMapping("/demo/approved")
	public ImportAssessment approvedDemo() {
		return assessmentService.evaluate(ImportRequest.approvedDemo());
	}

	@GetMapping("/demo/rejected-price")
	public ImportAssessment rejectedByPriceDemo() {
		return assessmentService.evaluate(ImportRequest.rejectedByPriceDemo());
	}

	@GetMapping("/demo/rejected-supplier")
	public ImportAssessment rejectedBySupplierDemo() {
		return assessmentService.evaluate(ImportRequest.rejectedBySupplierDemo());
	}

	@GetMapping("/demo/low-stock")
	public ImportAssessment lowStockDemo() {
		return assessmentService.evaluate(ImportRequest.lowStockDemo());
	}

	@GetMapping("/demo/declining-sales")
	public ImportAssessment decliningSalesDemo() {
		return assessmentService.evaluate(ImportRequest.decliningSalesDemo());
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
