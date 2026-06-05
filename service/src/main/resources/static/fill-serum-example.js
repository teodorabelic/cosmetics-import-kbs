(() => {
	const form = document.querySelector("#requestForm");
	if (!form) {
		console.error("Forma #requestForm nije pronadjena.");
		return;
	}

	const fields = {
		productName: "Vitamin C serum",
		category: "SKINCARE",
		productType: "TREND",
		countryOfOrigin: "South Korea",
		purchasePrice: "8.2",
		transportRate: "0.06",
		customsRate: "0.1",
		vatRate: "0.2",
		expectedSellingPrice: "19.9",
		competitorMinPrice: "16.5",
		competitorMaxPrice: "24",
		demandScore: "9",
		monthlyDemand: "120",
		currentStock: "18",
		minStock: "40",
		shelfLifeMonths: "18",
		deliveryTimeDays: "14",
		supplierReliability: "0.92"
	};

	Object.entries(fields).forEach(([name, value]) => {
		const field = form.elements[name];
		if (field) {
			field.value = value;
		}
	});

	document.querySelector('[data-view="assessmentView"]')?.click();

	console.log("Serum primer je unet u formu. Klikni Proceni.");
})();
