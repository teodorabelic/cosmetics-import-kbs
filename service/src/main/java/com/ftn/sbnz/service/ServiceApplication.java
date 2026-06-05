package com.ftn.sbnz.service;

import com.ftn.sbnz.kjar.TemplateRuleGenerator;
import java.nio.charset.StandardCharsets;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServiceApplication {

	// lista DRL pravila
	private static final String[] RULE_RESOURCES = {
			"rules/import-assessment.drl",
			"rules/forward-chaining.drl",
			"rules/backward-chaining.drl",
			"rules/accumulate-rules.drl",
			"rules/cep-rules.drl"
	};

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	@Bean
	public KieContainer kieContainer() {
		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId("com.ftn.sbnz", "generated-import-kjar", "0.0.1-SNAPSHOT");
		KieFileSystem kieFileSystem = ks.newKieFileSystem();
		kieFileSystem.generateAndWritePomXML(releaseId);
		// KieBase
		kieFileSystem.writeKModuleXML("""
				<kmodule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://jboss.org/kie/6.0.0/kmodule">
					<kbase name="importAssessmentKBase" packages="rules" eventProcessingMode="stream">
						<ksession name="importAssessmentSession" clockType="pseudo"/>
					</kbase>
				</kmodule>
				""");
		// upisivanje fajlova u KieFileSystem
		for (String ruleResource : RULE_RESOURCES) {
			kieFileSystem.write(ks.getResources().newClassPathResource(ruleResource));
		}
		kieFileSystem.write("src/main/resources/rules/product-policy-generated.drl", ks.getResources()
				.newByteArrayResource(TemplateRuleGenerator.generateProductPolicyRules().getBytes(StandardCharsets.UTF_8))
				.setResourceType(ResourceType.DRL));

		// Drools kompajlira sva pravila
		KieBuilder kieBuilder = ks.newKieBuilder(kieFileSystem).buildAll();
		Results results = kieBuilder.getResults();
		if (!results.getMessages(Message.Level.ERROR).isEmpty()) {
			throw new IllegalStateException("Drools rules contain errors: " + results.getMessages(Message.Level.ERROR));
		}
		ks.getRepository().addKieModule(kieBuilder.getKieModule());
		return ks.newKieContainer(releaseId);
	}

}
