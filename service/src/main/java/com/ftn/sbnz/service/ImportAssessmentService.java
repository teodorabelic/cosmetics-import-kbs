package com.ftn.sbnz.service;

import com.ftn.sbnz.model.ImportAssessment;
import com.ftn.sbnz.model.ImportRequest;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
public class ImportAssessmentService {

	private final KieContainer kieContainer;

	public ImportAssessmentService(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	public ImportAssessment evaluate(ImportRequest request) {
		ImportAssessment assessment = new ImportAssessment(request.getProductName());
		KieSession kieSession = kieContainer.newKieSession("importAssessmentSession");
		try {
			kieSession.insert(request);
			kieSession.insert(assessment);
			kieSession.fireAllRules();
			return assessment;
		} finally {
			kieSession.dispose();
		}
	}
}
