(() => {
	const form = document.querySelector("#requestForm");
	if (!form) {
		console.error("Forma #requestForm nije pronadjena.");
		return;
	}

	fetch("/api/import-assessment/demo-request/approved")
		.then((response) => {
			if (!response.ok) {
				throw new Error(`Status ${response.status}`);
			}
			return response.json();
		})
		.then((fields) => {
			Object.entries(fields).forEach(([name, value]) => {
				const field = form.elements[name];
				if (field && !Array.isArray(value) && typeof value !== "object") {
					field.value = value;
				}
			});
			document.querySelector('[data-view="assessmentView"]')?.click();
			console.log("Serum primer je unet u formu. Klikni Proceni.");
		})
		.catch((error) => console.error(error));
})();
