package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportRequest;
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

	@PostMapping("/evaluate")
	public ImportAssessment evaluate(@RequestBody ImportRequest request) {
		return assessmentService.evaluate(request);
	}
}
