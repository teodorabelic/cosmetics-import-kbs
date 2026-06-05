package com.ftn.sbnz.kjar;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;

class KjarApplicationTests {

	private static final String[] RULE_RESOURCES = {
			"rules/import-assessment.drl",
			"rules/forward-chaining.drl",
			"rules/backward-chaining.drl",
			"rules/accumulate-rules.drl",
			"rules/cep-rules.drl"
	};

	@Test
	void rulesCompileWithoutErrors() {
		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		for (String ruleResource : RULE_RESOURCES) {
			kieFileSystem.write(kieServices.getResources().newClassPathResource(ruleResource));
		}
		kieFileSystem.write("src/main/resources/rules/product-policy-generated.drl", kieServices.getResources()
				.newByteArrayResource(TemplateRuleGenerator.generateProductPolicyRules().getBytes(StandardCharsets.UTF_8))
				.setResourceType(ResourceType.DRL));
		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();

		Results results = kieBuilder.getResults();

		assertThat(results.getMessages(Message.Level.ERROR)).isEmpty();
	}

	@Test
	void productPolicyTemplateResourcesExist() {
		assertThat(getClass().getClassLoader().getResource("templates/product-policy.drt")).isNotNull();
		assertThat(getClass().getClassLoader().getResource("templates/product-policy-data.csv")).isNotNull();
		assertThat(TemplateRuleGenerator.generateProductPolicyRules())
				.contains("Template TREND prag zaliha", "Template LUXURY prag zaliha");
	}
}
