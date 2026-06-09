package com.ftn.sbnz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.sbnz.model.ImportRequest;
import com.ftn.sbnz.model.InventoryProduct;
import com.ftn.sbnz.model.SalesEvent;
import com.ftn.sbnz.model.SalesRecord;
import com.ftn.sbnz.model.ShipmentReceivedEvent;
import com.ftn.sbnz.model.StockChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ImportTestDataRepository {

	private static final String INVENTORY_PRODUCTS_PATH = "/data/inventory-products.json";
	private static final String DEMO_REQUESTS_PATH = "/data/demo-requests.json";
	private static final String SALES_TREND_PROFILES_PATH = "/data/sales-trend-profiles.json";

	private final ObjectMapper objectMapper;

	public ImportTestDataRepository(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public List<InventoryProduct> loadInventoryProducts() {
		try (InputStream stream = openResource(INVENTORY_PRODUCTS_PATH)) {
			List<InventoryProduct> products = objectMapper.readValue(stream, new TypeReference<List<InventoryProduct>>() {
			});
			applySalesTrendProfiles(products);
			return products;
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read inventory test data.", exception);
		}
	}

	public ImportRequest loadDemoRequest(String scenario) {
		try (InputStream stream = openResource(DEMO_REQUESTS_PATH)) {
			Map<String, ImportRequest> scenarios = objectMapper.readValue(stream, new TypeReference<Map<String, ImportRequest>>() {
			});
			ImportRequest request = scenarios.get(scenario);
			if (request == null) {
				throw new IllegalArgumentException("Unknown demo scenario: " + scenario);
			}
			return request;
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read demo request test data.", exception);
		}
	}

	private InputStream openResource(String path) {
		InputStream stream = ImportTestDataRepository.class.getResourceAsStream(path);
		if (stream == null) {
			throw new IllegalStateException("Missing test data resource: " + path);
		}
		return stream;
	}

	private void applySalesTrendProfiles(List<InventoryProduct> products) {
		Map<String, SalesTrendProfile> profiles = loadSalesTrendProfiles();
		for (InventoryProduct product : products) {
			if (product.getSalesTrend() == null) {
				continue;
			}
			SalesTrendProfile profile = profiles.get(product.getSalesTrend().name());
			if (profile == null) {
				continue;
			}
			if (product.getSalesHistory().isEmpty()) {
				product.setSalesHistory(profile.salesHistory().stream()
						.map(record -> new SalesRecord(product.getProductName(), record.daysAgo(), record.quantity()))
						.toList());
			}
			if (product.getSalesEvents().isEmpty()) {
				product.setSalesEvents(profile.salesEvents().stream()
						.map(event -> new SalesEvent(product.getProductName(), event.minutesAgo(), event.quantity()))
						.toList());
			}
			if (product.getStockEvents().isEmpty()) {
				product.setStockEvents(profile.stockEvents().stream()
						.map(event -> new StockChangeEvent(product.getProductName(), event.minutesAgo(),
								product.getCurrentStock() + event.quantityBeforeOffset(),
								product.getCurrentStock() + event.quantityAfterOffset()))
						.toList());
			}
			if (product.getShipmentEvents().isEmpty()) {
				product.setShipmentEvents(profile.shipmentEvents().stream()
						.map(event -> new ShipmentReceivedEvent(product.getProductName(), event.minutesAgo(),
								event.quantity()))
						.toList());
			}
		}
	}

	private Map<String, SalesTrendProfile> loadSalesTrendProfiles() {
		try (InputStream stream = openResource(SALES_TREND_PROFILES_PATH)) {
			return objectMapper.readValue(stream, new TypeReference<Map<String, SalesTrendProfile>>() {
			});
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read sales trend profile test data.", exception);
		}
	}

	private record SalesTrendProfile(List<ProfileSalesRecord> salesHistory, List<ProfileTimedQuantity> salesEvents,
			List<ProfileStockEvent> stockEvents, List<ProfileTimedQuantity> shipmentEvents) {
	}

	private record ProfileSalesRecord(int daysAgo, int quantity) {
	}

	private record ProfileTimedQuantity(int minutesAgo, int quantity) {
	}

	private record ProfileStockEvent(int minutesAgo, int quantityBeforeOffset, int quantityAfterOffset) {
	}
}
