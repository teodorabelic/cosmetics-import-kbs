let existingProducts = [];
let productPolicyThresholds = {};
let selectedProduct = null;

const form = document.querySelector("#requestForm");

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
		const [productsResponse, policiesResponse] = await Promise.all([
			fetch("/api/import-assessment/products"),
			fetch("/api/import-assessment/policies")
		]);
		if (!productsResponse.ok) {
			throw new Error(`Status ${productsResponse.status}`);
		}
		existingProducts = await productsResponse.json();
		if (policiesResponse.ok) {
			productPolicyThresholds = buildPolicyThresholdMap(await policiesResponse.json());
		}
		renderInventory();
		setStatus("Spremno");
	} catch (error) {
		existingProducts = [];
		productPolicyThresholds = {};
		renderInventory();
		setStatus("Spremno");
		console.error(error);
	}
}

function buildPolicyThresholdMap(policies) {
	return (policies || []).reduce((thresholds, policy) => {
		thresholds[policy.productType] = policy.minStockThreshold;
		return thresholds;
	}, {});
}

function bindNavigation() {
	document.querySelectorAll("[data-view]").forEach((button) => {
		button.addEventListener("click", () => showView(button.dataset.view));
	});
	document.querySelector("#newAssessmentButton").addEventListener("click", () => showView("assessmentView"));
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
		{ label: "Rastući trend", value: risingProducts.length },
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
					<span>${product.currentStock} na stanju / min ${product.minStock}</span>
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
	renderStockInsight(product);
	renderCepEventSummary(product);
	clearProductDetailResult();
	showView("productDetailView");
	runProductAnalysis();
}

function renderDetailSummary(product) {
	const status = getStockStatus(product);
	const templateThreshold = productPolicyThresholds[product.productType] ?? "-";
	const items = [
		{ label: "Kategorija", value: product.category },
		{ label: "Tip", value: product.productType },
		{ label: "Trenutne zalihe", value: product.currentStock },
		{ label: "Min proizvoda", value: product.minStock },
		{ label: "Template prag (CSV)", value: templateThreshold },
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

function renderStockInsight(product) {
	const status = getStockStatus(product);
	const templateThreshold = Number(productPolicyThresholds[product.productType] ?? 0);
	const current = Number(product.currentStock || 0);
	const minStock = Number(product.minStock || 0);
	const maxValue = Math.max(current, minStock, templateThreshold, 1);
	const scaleMax = Math.ceil(maxValue * 1.25);
	const currentPercent = percentOf(current, scaleMax);
	const minPercent = percentOf(minStock, scaleMax);
	const templatePercent = percentOf(templateThreshold, scaleMax);
	const ringPercent = Math.min(Math.round((current / Math.max(minStock, 1)) * 100), 100);
	const gapToMin = Math.max(minStock - current, 0);
	const gapToTemplate = Math.max(templateThreshold - current, 0);
	const insightText = gapToMin > 0
		? `Ima ${gapToMin} komada ispod minimuma proizvoda.`
		: "Zalihe su iznad minimuma proizvoda.";
	const templateText = templateThreshold
		? (gapToTemplate > 0
			? `Template prag traži još ${gapToTemplate} komada da bi bio ispunjen minimum za tip ${product.productType}.`
			: `Template prag za tip ${product.productType} je pokriven.`)
		: "Template prag nije pronađen za ovaj tip proizvoda.";

	document.querySelector("#stockInsight").innerHTML = `
		<div class="stock-insight-main">
			<div class="stock-ring ${status.className}" style="--ring: ${ringPercent}%">
				<strong>${current}</strong>
				<span>na stanju</span>
			</div>
			<div>
				<p class="eyebrow">Stanje zaliha</p>
				<h2>${status.label}</h2>
				<p>${insightText} ${templateText}</p>
			</div>
		</div>
		<div class="stock-chart" aria-hidden="true">
			<div class="stock-chart-track">
				<i class="stock-chart-fill ${status.className}" style="width: ${currentPercent}%"></i>
				<span class="stock-marker min" style="left: ${minPercent}%"></span>
				<span class="stock-marker template" style="left: ${templatePercent}%"></span>
			</div>
			<div class="stock-chart-legend">
				<span><i class="legend-current"></i>Trenutno: ${current}</span>
				<span><i class="legend-min"></i>Min proizvoda: ${minStock}</span>
				<span><i class="legend-template"></i>Template CSV: ${templateThreshold || "-"}</span>
			</div>
		</div>
	`;
}

function percentOf(value, maxValue) {
	return Math.max(0, Math.min((Number(value || 0) / Math.max(maxValue, 1)) * 100, 100));
}

function clearProductDetailResult() {
	document.querySelector("#detailAvg7").textContent = "0.00";
	document.querySelector("#detailAvg30").textContent = "0.00";
	document.querySelector("#detailStockDays").textContent = "0.00";
	document.querySelector("#detailTrend").textContent = "-";
	renderList("#detailRecommendations", ["Računanje trenda na osnovu postojeće istorije prodaje."]);
	renderList("#detailRules", []);
	renderList("#cepRecommendations", ["CEP analiza se računa na osnovu događaja proizvoda."]);
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
			body: JSON.stringify(await buildProductAssessmentRequest(selectedProduct))
		});
		if (!response.ok) {
			throw new Error(`Status ${response.status}`);
		}
		const data = await response.json();
		renderProductDetailAssessment(data);
		setStatus("Spremno");
	} catch (error) {
		setStatus("Greška");
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
		item.includes("naručivanje") ||
		item.includes("narudžbinu")
	);
	const accumulateRules = (data.activatedRules || []).filter((rule) =>
		rule.includes("prosečnu prodaju") ||
		rule.includes("trend prodaje") ||
		rule.includes("zaliha") ||
		rule.includes("naručivanje") ||
		rule.includes("narudžbinu")
	);
	renderList("#detailRecommendations", accumulateRecommendations);
	renderList("#detailRules", accumulateRules);

	const cepRecommendations = (data.recommendations || []).filter((item) =>
		item.includes("poslednjih sat vremena") ||
		item.includes("poslednja 24 sata") ||
		item.includes("nestašice") ||
		item.includes("Zalihe su povećane")
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
		return { label: "Naručiti", className: "danger" };
	}
	if (product.currentStock <= product.minStock * 1.5) {
		return { label: "Pratiti", className: "warning" };
	}
	if (product.salesTrend === "DECLINING") {
		return { label: "Višak rizik", className: "warning" };
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

async function buildProductAssessmentRequest(product) {
	const response = await fetch("/api/import-assessment/demo-request/approved");
	if (!response.ok) {
		throw new Error(`Status ${response.status}`);
	}
	const defaults = await response.json();
	return {
		...defaults,
		productName: product.productName,
		category: product.category,
		productType: product.productType,
		currentStock: product.currentStock,
		minStock: product.minStock,
		salesHistory: product.salesHistory || [],
		salesEvents: product.salesEvents || [],
		stockEvents: product.stockEvents || [],
		shipmentEvents: product.shipmentEvents || []
	};
}

async function fillAssessmentFromProduct(product) {
	const values = await buildProductAssessmentRequest(product);
	Object.entries(values).forEach(([name, value]) => {
		const field = form.elements[name];
		if (field && !Array.isArray(value) && typeof value !== "object") {
			field.value = value;
		}
	});
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
		setStatus("Greška");
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
	void text;
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

