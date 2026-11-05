# Cosmetics Import KBS

A **knowledge-based decision support system** for evaluating the profitability of cosmetic product imports, recommending B2B and D2C prices, assessing business risks, and managing inventory.

Developed as an **independent academic project** using Java, Spring Boot, and Drools, the system applies rule-based reasoning and complex event processing (CEP) to generate explainable business recommendations.

## Key Features

- **Import Profitability Assessment** — Evaluates import costs, expected profitability, market demand, and competitor pricing to recommend whether a product should be imported.
- **B2B & D2C Pricing** — Calculates recommended wholesale and retail prices based on product costs and predefined pricing rules.
- **Risk Assessment** — Identifies potential risks related to high costs, low margins, unreliable suppliers, and market competitiveness.
- **Inventory Management** — Monitors stock levels, estimates replenishment needs, and generates low-stock warnings.
- **Sales Trend Analysis** — Analyzes historical sales and detects increasing or decreasing demand.
- **Explainable Decisions** — Provides recommendations, supporting factors, and information about activated business rules.

## Technologies

- **Java 17**
- **Spring Boot** — REST API and application services
- **Drools** — Rule engine and knowledge-based reasoning
- **Maven** — Dependency management and build automation
- **Postman** — API testing

## Knowledge-Based Reasoning

The system demonstrates several knowledge engineering techniques:

| Technique | Implementation |
|---|---|
| Forward Chaining | Multi-step reasoning for import evaluation, pricing, and inventory decisions |
| Backward Chaining | Queries used to determine whether an import recommendation can be supported |
| Accumulate | Aggregation of sales data to calculate average sales over different periods |
| Complex Event Processing (CEP) | Detection of sales trends and stock-related events over time |
| Rule Templates | Generation of business rules from configurable product policy data |

## Project Structure

```text
cosmetics-import-kbs/
├── model/       # Domain models and DTOs
├── kjar/        # Drools rules, queries, CEP and rule templates
├── service/     # Spring Boot REST API and KIE integration
├── docs/        # Project documentation
└── postman/     # API testing resources
```

## Getting Started

### Prerequisites

- Java 17
- Maven Wrapper (included in the project)

### Running the Application

From the project root, execute the following commands in separate steps:

```powershell
cd model
.\mvnw.cmd install -DskipTests

cd ..\kjar
.\mvnw.cmd install -DskipTests

cd ..\service
.\mvnw.cmd spring-boot:run
```

The application runs at:

`http://localhost:8080`

## API Endpoints

The REST API provides demonstration scenarios and endpoints for evaluating import decisions.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/import-assessment/demo` | Run a default demonstration |
| GET | `/api/import-assessment/demo/approved` | Demonstrate an approved import |
| GET | `/api/import-assessment/demo/rejected-price` | Demonstrate rejection due to pricing |
| GET | `/api/import-assessment/demo/rejected-supplier` | Demonstrate supplier-related rejection |
| GET | `/api/import-assessment/demo/low-stock` | Demonstrate low-stock conditions |
| GET | `/api/import-assessment/demo/declining-sales` | Demonstrate declining sales |
| POST | `/api/import-assessment/evaluate` | Evaluate a product using supplied business data |
| POST | `/api/import-assessment/recommendation-query` | Execute a recommendation query |
| POST | `/api/import-assessment/explain` | Retrieve an explanation of the decision |

## Example Decision-Making Process

For a cosmetic product, the system:

1. Calculates the total import cost based on purchase price, shipping, and customs.
2. Determines recommended B2B and D2C selling prices.
3. Compares the recommended retail price with competitor prices.
4. Evaluates profitability, demand, and business risks.
5. Generates an import recommendation.
6. Analyzes sales history and stock levels.
7. Suggests inventory adjustments and provides an explanation of the decision.

## Running Tests

Run tests for each module:

```powershell
cd model
.\mvnw.cmd test

cd ..\kjar
.\mvnw.cmd test

cd ..\service
.\mvnw.cmd test
```

## Documentation

For a detailed description of the system requirements,
business rules, and reasoning mechanisms, see the
[Project Specification](docs/project-specification.pdf).
