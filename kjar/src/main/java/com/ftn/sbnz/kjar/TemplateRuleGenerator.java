package com.ftn.sbnz.kjar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;

// ucita .drl i .csv pa generise DRL pravila

public final class TemplateRuleGenerator {

	private static final String TEMPLATE_PATH = "/templates/product-policy.drt";
	private static final String DATA_PATH = "/templates/product-policy-data.csv";

	private TemplateRuleGenerator() {
	}

	public static String generateProductPolicyRules() {
		try (InputStream template = TemplateRuleGenerator.class.getResourceAsStream(TEMPLATE_PATH);
				InputStream data = TemplateRuleGenerator.class.getResourceAsStream(DATA_PATH)) {
			if (template == null || data == null) {
				throw new IllegalStateException("Template resources are missing.");
			}
			DataProvider dataProvider = new ArrayDataProvider(readCsvRows(data));
			return new DataProviderCompiler().compile(dataProvider, template);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read product policy template resources.", exception);
		}
	}

	private static String[][] readCsvRows(InputStream data) throws IOException {
		List<String[]> rows = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					rows.add(line.split(",", -1));
				}
			}
		}
		return rows.toArray(String[][]::new);
	}
}
