const fallbackProducts = [
	{ productName: "Vitamin C serum", category: "SKINCARE", productType: "TREND", currentStock: 18, minStock: 40, supplier: "Glow Korea", salesTrend: "GROWING" },
	{ productName: "Repair hair mask", category: "HAIRCARE", productType: "PROFESSIONAL", currentStock: 9, minStock: 25, supplier: "SalonPro", salesTrend: "STABLE" },
	{ productName: "Matte liquid lipstick", category: "MAKEUP", productType: "MASS", currentStock: 64, minStock: 20, supplier: "Color Lab", salesTrend: "GROWING" },
	{ productName: "Body shimmer oil", category: "BODYCARE", productType: "STANDARD", currentStock: 132, minStock: 35, supplier: "Summer Care", salesTrend: "DECLINING" },
	{ productName: "Luxury night cream", category: "SKINCARE", productType: "LUXURY", currentStock: 26, minStock: 18, supplier: "Maison Belle", salesTrend: "STABLE" },
	{ productName: "Peptide eye cream", category: "SKINCARE", productType: "LUXURY", currentStock: 7, minStock: 12, supplier: "Derma Seoul", salesTrend: "GROWING" },
	{ productName: "Keratin shampoo", category: "HAIRCARE", productType: "MASS", currentStock: 88, minStock: 30, supplier: "HairLab", salesTrend: "STABLE" },
	{ productName: "Niacinamide toner", category: "SKINCARE", productType: "TREND", currentStock: 14, minStock: 35, supplier: "Pure K-Beauty", salesTrend: "GROWING" },
	{ productName: "Aloe body lotion", category: "BODYCARE", productType: "MASS", currentStock: 11, minStock: 18, supplier: "Green Care", salesTrend: "STABLE" },
	{ productName: "Rose eau de parfum", category: "FRAGRANCE", productType: "LUXURY", currentStock: 42, minStock: 10, supplier: "Maison Fleur", salesTrend: "DECLINING" },
	{ productName: "Brow styling gel", category: "MAKEUP", productType: "TREND", currentStock: 5, minStock: 22, supplier: "Brow Studio", salesTrend: "GROWING" },
	{ productName: "Retinol night serum", category: "SKINCARE", productType: "PROFESSIONAL", currentStock: 31, minStock: 16, supplier: "DermaLab Pro", salesTrend: "STABLE" },
	{ productName: "Hand repair cream", category: "BODYCARE", productType: "STANDARD", currentStock: 74, minStock: 25, supplier: "Daily Care", salesTrend: "DECLINING" },
	{ productName: "Scalp peeling tonic", category: "HAIRCARE", productType: "TREND", currentStock: 16, minStock: 28, supplier: "Root Science", salesTrend: "GROWING" },
	{ productName: "Pressed powder compact", category: "MAKEUP", productType: "STANDARD", currentStock: 39, minStock: 15, supplier: "Color Lab", salesTrend: "STABLE" }
].map((product) => ({
	...product,
	salesHistory: buildSalesHistory(product.productName, scenarioFromTrend(product.salesTrend)),
	...buildCepEvents(product)
}));

let existingProducts = fallbackProducts;
let selectedProduct = null;

const form = document.querySelector("#requestForm");
const statusPill = document.querySelector("#apiStatus");

loadInventory();
bindNavigation();
fillFormFromQueryParams();

form.addEventListener("submit", async (event) => {
	event.preventDefault();
	if (!form.reportValidity()) {
		setStatus("Unos");
		return;
	}
	await explainCurrentForm();
});

async function loadInventory() {
	try {
		const response = await fetch("/api/import-assessment/products");
		if (!response.ok) {
			throw new Error(`Status ${response.status}`);
		}
		existingProducts = await response.json();
		renderInventory();
		setStatus("Spremno");
	} catch (error) {
		existingProducts = fallbackProducts;
		renderInventory();
		setStatus("Spremno");
		console.error(error);
	}
}

function bindNavigation() {
	document.querySelectorAll("[data-view]").forEach((button) => {
		button.addEventListener("click", () => showView(button.dataset.view));
	});
	document.querySelector("#newAssessmentButton").addEventListener("click", () => showView("assessmentView"));
	document.querySelector("#detailAssessButton").addEventListener("click", () => {
		if (selectedProduct) {
			fillAssessmentFromProduct(selectedProduct);
		}
		showView("assessmentView");
	});
}

function showView(viewId) {
	document.querySelectorAll(".page-view").forEach((view) => {
		view.classList.toggle("active", view.id === viewId);
	});
	document.querySelectorAll(".nav-button").forEach((button) => {
		button.classList.toggle("active", button.dataset.view === viewId);
	});
	if (viewId === "assessmentView") {
		form.elements.productName.focus();
	}
}

function renderInventory() {
	const lowStockProducts = existingProducts.filter((product) => product.currentStock <= product.minStock);
	const risingProducts = existingProducts.filter((product) => product.salesTrend === "GROWING");
	const totalStock = existingProducts.reduce((sum, product) => sum + product.currentStock, 0);

	renderSummary([
		{ label: "Proizvoda u sistemu", value: existingProducts.length },
		{ label: "Ispod praga", value: lowStockProducts.length },
		{ label: "Rastuci trend", value: risingProducts.length },
		{ label: "Ukupno na zalihama", value: totalStock }
	]);
	renderInventoryTable();
}

function renderSummary(items) {
	const summary = document.querySelector("#inventorySummary");
	summary.innerHTML = "";
	items.forEach((item) => {
		const card = document.createElement("article");
		card.className = "summary-card";
		card.innerHTML = `<span>${item.label}</span><strong>${item.value}</strong>`;
		summary.append(card);
	});
}

function renderInventoryTable() {
	const body = document.querySelector("#inventoryTableBody");
	body.innerHTML = "";
	existingProducts.forEach((product) => {
		const ratio = Math.min(product.currentStock / Math.max(product.minStock, 1), 2);
		const percent = Math.max(8, Math.min(ratio * 50, 100));
		const status = getStockStatus(product);
		const row = document.createElement("tr");
		row.tabIndex = 0;
		row.className = "clickable-row";
		row.addEventListener("click", () => openProductDetail(product));
		row.addEventListener("keydown", (event) => {
			if (event.key === "Enter" || event.key === " ") {
				event.preventDefault();
				openProductDetail(product);
			}
		});
		row.innerHTML = `
			<td>
				<strong>${product.productName}</strong>
				<small>${product.supplier}</small>
			</td>
			<td>${product.category}</td>
			<td>${product.productType}</td>
			<td>
				<div class="stock-cell">
					<span>${product.currentStock} / min ${product.minStock}</span>
					<div class="stock-bar"><i style="width: ${percent}%"></i></div>
				</div>
			</td>
			<td>${formatTrend(product.salesTrend)}</td>
			<td><span class="status-tag ${status.className}">${status.label}</span></td>
		`;
		body.append(row);
	});
}

function openProductDetail(product) {
	selectedProduct = product;
	document.querySelector("#detailProductName").textContent = product.productName;
	renderDetailSummary(product);
	renderCepEventSummary(product);
	clearProductDetailResult();
	showView("productDetailView");
	runProductAnalysis();
}

function renderDetailSummary(product) {
	const status = getStockStatus(product);
	const items = [
		{ label: "Kategorija", value: product.category },
		{ label: "Tip", value: product.productType },
		{ label: "Zalihe", value: `${product.currentStock} / min ${product.minStock}` },
		{ label: "Status", value: status.label }
	];
	const summary = document.querySelector("#detailSummary");
	summary.innerHTML = "";
	items.forEach((item) => {
		const card = document.createElement("article");
		card.className = "summary-card";
		card.innerHTML = `<span>${item.label}</span><strong>${item.value}</strong>`;
		summary.append(card);
	});
}

function clearProductDetailResult() {
	document.querySelector("#detailAvg7").textContent = "0.00";
	document.querySelector("#detailAvg30").textContent = "0.00";
	document.querySelector("#detailStockDays").textContent = "0.00";
	document.querySelector("#detailTrend").textContent = "-";
	renderList("#detailRecommendations", ["Racunanje trenda na osnovu postojece istorije prodaje."]);
	renderList("#detailRules", []);
	renderList("#cepRecommendations", ["CEP analiza se racuna na osnovu dogadjaja proizvoda."]);
	renderList("#cepRules", []);
}

async function runProductAnalysis() {
	if (!selectedProduct) {
		return;
	}
	setStatus("Accumulate");
	try {
		const response = await fetch("/api/import-assessment/explain", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(buildProductAssessmentRequest(selectedProduct))
		});
		if (!response.ok) {
			throw new Error(`Status ${response.status}`);
		}
		const data = await response.json();
		renderProductDetailAssessment(data);
		setStatus("Spremno");
	} catch (error) {
		setStatus("Greska");
		alert(error.message);
		console.error(error);
	}
}

function renderProductDetailAssessment(data) {
	document.querySelector("#detailAvg7").textContent = formatMoney(data.averageDailySales7);
	document.querySelector("#detailAvg30").textContent = formatMoney(data.averageDailySales30);
	document.querySelector("#detailStockDays").textContent = formatMoney(data.remainingStockDays);
	document.querySelector("#detailTrend").textContent = formatTrend(data.salesTrend);

	const accumulateRecommendations = (data.recommendations || []).filter((item) =>
		item.includes("prodaja") ||
		item.includes("Prodaja") ||
		item.includes("zaliha") ||
		item.includes("Zalihe") ||
		item.includes("narucivanje") ||
		item.includes("narudzbinu")
	);
	const accumulateRules = (data.activatedRules || []).filter((rule) =>
		rule.includes("prosecnu prodaju") ||
		rule.includes("trend prodaje") ||
		rule.includes("zaliha") ||
		rule.includes("narucivanje") ||
		rule.includes("narudzbinu")
	);
	renderList("#detailRecommendations", accumulateRecommendations);
	renderList("#detailRules", accumulateRules);

	const cepRecommendations = (data.recommendations || []).filter((item) =>
		item.includes("poslednjih sat vremena") ||
		item.includes("poslednja 24 sata") ||
		item.includes("nestasice") ||
		item.includes("Zalihe su povecane")
	);
	const cepRules = (data.activatedRules || []).filter((rule) => rule.startsWith("CEP "));
	document.querySelector("#cepStatus").textContent = cepRules.length ? "Upozorenje" : "Bez alarma";
	renderList("#cepRecommendations", cepRecommendations);
	renderList("#cepRules", cepRules);
}

function renderCepEventSummary(product) {
	document.querySelector("#cepSalesCount").textContent = (product.salesEvents || []).length;
	document.querySelector("#cepStockCount").textContent = (product.stockEvents || []).length;
	document.querySelector("#cepShipmentCount").textContent = (product.shipmentEvents || []).length;
	document.querySelector("#cepStatus").textContent = "-";
}

function getStockStatus(product) {
	if (product.currentStock <= product.minStock) {
		return { label: "Naruciti", className: "danger" };
	}
	if (product.currentStock <= product.minStock * 1.5) {
		return { label: "Pratiti", className: "warning" };
	}
	if (product.salesTrend === "DECLINING") {
		return { label: "Visak rizik", className: "warning" };
	}
	return { label: "Dovoljno", className: "ok" };
}

function readForm() {
	const data = Object.fromEntries(new FormData(form).entries());
	const numericFields = [
		"purchasePrice",
		"shippingCost",
		"transportRate",
		"customsRate",
		"vatRate",
		"expectedSellingPrice",
		"competitorMinPrice",
		"competitorMaxPrice",
		"demandScore",
		"monthlyDemand",
		"currentStock",
		"minStock",
		"shelfLifeMonths",
		"deliveryTimeDays",
		"supplierReliability"
	];
	numericFields.forEach((field) => {
		data[field] = Number(data[field] || 0);
	});
	data.salesHistory = [];
	data.salesEvents = [];
	data.stockEvents = [];
	data.shipmentEvents = [];
	return data;
}

function buildSalesHistory(productName, scenario) {
	const quantitiesByScenario = {
		growing: [
			[1, 10],
			[2, 9],
			[3, 10],
			[4, 8],
			[5, 9],
			[6, 10],
			[7, 9],
			[10, 3],
			[15, 3],
			[20, 4],
			[25, 3],
			[30, 4]
		],
		declining: [
			[1, 1],
			[2, 1],
			[3, 2],
			[4, 1],
			[5, 1],
			[6, 2],
			[7, 1],
			[10, 8],
			[15, 9],
			[20, 8],
			[25, 9],
			[30, 8]
		],
		stable: [
			[1, 5],
			[2, 4],
			[3, 5],
			[4, 4],
			[5, 5],
			[6, 4],
			[7, 5],
			[10, 4],
			[15, 5],
			[20, 4],
			[25, 5],
			[30, 4]
		]
	};
	return (quantitiesByScenario[scenario] || []).map(([daysAgo, quantity]) => ({
		productName,
		daysAgo,
		quantity
	}));
}

function buildProductAssessmentRequest(product) {
	const demandByTrend = {
		GROWING: 9,
		STABLE: 6,
		DECLINING: 4
	};
	const monthlyDemandByTrend = {
		GROWING: 120,
		DECLINING: 45,
		STABLE: 80
	};
	return {
		productName: product.productName,
		category: product.category,
		productType: product.productType,
		countryOfOrigin: "South Korea",
		purchasePrice: 8.2,
		transportRate: 0.06,
		customsRate: 0.1,
		vatRate: 0.2,
		expectedSellingPrice: 19.9,
		competitorMinPrice: 18,
		competitorMaxPrice: 24,
		demandScore: demandByTrend[product.salesTrend] || 6,
		monthlyDemand: monthlyDemandByTrend[product.salesTrend] || 80,
		currentStock: product.currentStock,
		minStock: product.minStock,
		shelfLifeMonths: 18,
		deliveryTimeDays: 14,
		supplierReliability: 0.92,
		salesHistory: product.salesHistory || [],
		salesEvents: product.salesEvents || [],
		stockEvents: product.stockEvents || [],
		shipmentEvents: product.shipmentEvents || []
	};
}

function fillAssessmentFromProduct(product) {
	const values = buildProductAssessmentRequest(product);
	Object.entries(values).forEach(([name, value]) => {
		const field = form.elements[name];
		if (field && !Array.isArray(value) && typeof value !== "object") {
			field.value = value;
		}
	});
}

function scenarioFromTrend(trend) {
	const scenarios = {
		GROWING: "growing",
		DECLINING: "declining",
		STABLE: "stable"
	};
	return scenarios[trend] || "stable";
}

function buildCepEvents(product) {
	if (product.salesTrend === "GROWING") {
		return {
			salesEvents: [
				{ productName: product.productName, minutesAgo: 10, quantity: 4 },
				{ productName: product.productName, minutesAgo: 25, quantity: 3 },
				{ productName: product.productName, minutesAgo: 45, quantity: 5 }
			],
			stockEvents: [
				{ productName: product.productName, minutesAgo: 20, quantityBefore: product.currentStock + 12, quantityAfter: product.currentStock },
				{ productName: product.productName, minutesAgo: 50, quantityBefore: product.currentStock + 18, quantityAfter: product.currentStock + 8 }
			],
			shipmentEvents: []
		};
	}
	if (product.salesTrend === "DECLINING") {
		return {
			salesEvents: [],
			stockEvents: [
				{ productName: product.productName, minutesAgo: 90, quantityBefore: product.currentStock - 20, quantityAfter: product.currentStock }
			],
			shipmentEvents: [{ productName: product.productName, minutesAgo: 120, quantity: 40 }]
		};
	}
	return {
		salesEvents: [{ productName: product.productName, minutesAgo: 35, quantity: 2 }],
		stockEvents: [{ productName: product.productName, minutesAgo: 80, quantityBefore: product.currentStock + 2, quantityAfter: product.currentStock }],
		shipmentEvents: []
	};
}

async function explainCurrentForm() {
	setStatus("Procena");
	try {
		const response = await fetch("/api/import-assessment/explain", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(readForm())
		});
		if (!response.ok) {
			throw new Error(`Status ${response.status}`);
		}
		const data = await response.json();
		renderAssessment(data);
		setStatus("Spremno");
	} catch (error) {
		setStatus("Greska");
		alert(error.message);
		console.error(error);
	}
}

function renderAssessment(data) {
	const decisionBand = document.querySelector("#decisionBand");
	decisionBand.classList.toggle("reject", data.decision === "REJECT");
	decisionBand.classList.toggle("caution", data.decision === "APPROVE_WITH_CAUTION");

	document.querySelector("#decisionLabel").textContent = formatDecision(data.decision);
	document.querySelector("#productLabel").textContent = data.productName || form.elements.productName.value || "Proizvod";
	document.querySelector("#landedCost").textContent = formatMoney(data.landedCost);
	document.querySelector("#d2cPrice").textContent = formatMoney(data.recommendedD2CPrice);
	document.querySelector("#b2bPrice").textContent = formatMoney(data.recommendedB2BPrice);
	document.querySelector("#profit").textContent = formatMoney(data.expectedProfit);
	document.querySelector("#risk").textContent = data.riskLevel || "-";
	document.querySelector("#orderQty").textContent = data.suggestedOrderQuantity ?? 0;
	document.querySelector("#mainReason").textContent = data.mainReason || "-";

	const factors = [...(data.factors || [])];
	if (data.salesTrend && !factors.includes(data.salesTrend)) {
		factors.unshift(data.salesTrend);
	}
	if (data.importRecommended && !factors.includes("ImportRecommended")) {
		factors.unshift("ImportRecommended");
	}
	renderChips("#factorChips", factors);
	renderList("#recommendations", [...(data.recommendations || []), ...(data.queryExplanation || [])]);
	renderList("#rulesList", data.activatedRules || []);
}

function renderChips(selector, items) {
	const element = document.querySelector(selector);
	element.innerHTML = "";
	if (!items.length) {
		element.append(createChip("-"));
		return;
	}
	items.forEach((item) => element.append(createChip(item)));
}

function createChip(text) {
	const chip = document.createElement("span");
	chip.className = "chip";
	chip.textContent = text;
	return chip;
}

function renderList(selector, items) {
	const element = document.querySelector(selector);
	element.innerHTML = "";
	if (!items.length) {
		const empty = document.createElement("li");
		empty.textContent = "-";
		element.append(empty);
		return;
	}
	items.forEach((item) => {
		const li = document.createElement("li");
		li.textContent = item;
		element.append(li);
	});
}

function formatMoney(value) {
	return Number(value || 0).toFixed(2);
}

function formatDecision(decision) {
	const labels = {
		APPROVE: "Odobreno",
		APPROVE_WITH_CAUTION: "Oprez",
		REJECT: "Odbijeno"
	};
	return labels[decision] || "-";
}

function formatTrend(trend) {
	const labels = {
		GROWING: "Rast prodaje",
		DECLINING: "Pad prodaje",
		STABLE: "Stabilno"
	};
	return labels[trend] || "-";
}

function setStatus(text) {
	statusPill.textContent = text;
}

function fillFormFromQueryParams() {
	const params = new URLSearchParams(window.location.search);
	if (!params.size) {
		return;
	}
	for (const [name, value] of params.entries()) {
		const field = form.elements[name];
		if (field) {
			field.value = value;
		}
	}
	showView("assessmentView");
}
