# Cosmetics Import KBS

Sistem baziran na znanju za procenu isplativosti uvoza, formiranje cena i upravljanje zalihama kozmetickih proizvoda.

## Preduslovi

- Java 17
- Maven Wrapper se koristi kroz `mvnw.cmd`

## Struktura Projekta

- `model` - domenski model i DTO klase
- `kjar` - Drools pravila, query-ji, CEP i template resursi
- `service` - REST API, KIE integracija i demo scenariji

## Pokretanje

Iz root direktorijuma projekta:

```powershell
cd model
.\mvnw.cmd install -DskipTests

cd ..\kjar
.\mvnw.cmd install -DskipTests

cd ..\service
.\mvnw.cmd spring-boot:run
```

Servis se pokrece na:

```text
http://localhost:8080
```

## Demo Endpoint-i

```text
GET  /api/import-assessment/demo
GET  /api/import-assessment/demo/approved
GET  /api/import-assessment/demo/rejected-price
GET  /api/import-assessment/demo/rejected-supplier
GET  /api/import-assessment/demo/low-stock
GET  /api/import-assessment/demo/declining-sales
POST /api/import-assessment/evaluate
POST /api/import-assessment/recommendation-query
POST /api/import-assessment/explain
```

`/evaluate` vraca procenu uvoza, cene, rizik, kolicinu i objasnjenja.
`/recommendation-query` dodatno vraca rezultat backward query-ja `ImportRecommended`.
`/explain` vraca sazet DTO za prezentaciju: odluku, glavni razlog, cene, rizik, faktore, aktivirana pravila i preporuke.

## Primer POST Zahteva

```json
{
  "productName": "Vitamin C serum",
  "category": "SKINCARE",
  "productType": "TREND",
  "countryOfOrigin": "South Korea",
  "purchasePrice": 8.2,
  "shippingCost": 1.4,
  "customsRate": 0.1,
  "vatRate": 0.2,
  "expectedSellingPrice": 19.9,
  "competitorMinPrice": 16.5,
  "competitorMaxPrice": 24.0,
  "monthlyDemand": 120,
  "currentStock": 18,
  "minStock": 40,
  "shelfLifeMonths": 18,
  "supplierReliability": 0.92,
  "salesHistory": [
    { "productName": "Vitamin C serum", "daysAgo": 1, "quantity": 9 },
    { "productName": "Vitamin C serum", "daysAgo": 2, "quantity": 8 },
    { "productName": "Vitamin C serum", "daysAgo": 3, "quantity": 7 },
    { "productName": "Vitamin C serum", "daysAgo": 10, "quantity": 4 },
    { "productName": "Vitamin C serum", "daysAgo": 20, "quantity": 3 }
  ],
  "salesEvents": [
    { "productName": "Vitamin C serum", "minutesAgo": 10, "quantity": 4 },
    { "productName": "Vitamin C serum", "minutesAgo": 25, "quantity": 3 },
    { "productName": "Vitamin C serum", "minutesAgo": 50, "quantity": 3 }
  ],
  "stockEvents": [
    { "productName": "Vitamin C serum", "minutesAgo": 20, "quantityBefore": 30, "quantityAfter": 18 }
  ],
  "shipmentEvents": [
  ]
}
```

## Mapiranje Specifikacije Na Implementaciju

| Zahtev iz specifikacije | Implementacija |
| --- | --- |
| Ukupni trosak proizvoda | Pravilo `Izracunavanje ukupne uvozne cene` |
| B2B i D2C cena | Pravilo `Formiranje preporucenih B2B i D2C cena` |
| Konkurentski opseg cena | Pravila za povoljnu i nekonkurentnu D2C cenu |
| Procena isplativosti uvoza | Pravila za approve/caution/reject odluke |
| Procena rizika | `RiskLevel` i pravila za visok trosak, nisku marzu, dobavljaca i konkurentnost |
| Upravljanje zalihama | Pravila za niske/kriticne zalihe i kolicinu narudzbine |
| Accumulate | Pravilo `Izracunaj prosecnu prodaju za 7 i 30 dana` |
| CEP | `SalesEvent`, `StockChangeEvent`, `ShipmentReceivedEvent`, pseudo-clock i `over window:time(...)` pravila |
| Trend 7 vs 30 dana | Pravila `Detektuj rastuci/opadajuci/stabilan trend prodaje` |
| Backward chaining | Rekurzivni query `ImportRecommended` / `FactorLeadsToImport` i endpoint `/recommendation-query` |
| Template mehanizam | `templates/product-policy.drt`, `product-policy-data.csv` i `TemplateRuleGenerator` koji generise DRL pravila iz CSV podataka |
| Objasnjenje odluke | `recommendations`, `activatedRules`, `RecommendationQueryResult`, `/explain` |

## Provera

```powershell
cd model
.\mvnw.cmd test

cd ..\kjar
.\mvnw.cmd test

cd ..\service
.\mvnw.cmd test
```
