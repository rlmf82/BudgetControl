# BudgetControl

Spring Boot service to record personal financial operations (payments and incomes) and generate monthly/yearly reports.

## Requirements

- Java 21
- MySQL reachable at `localhost:3306` (the app has no embedded/dev database — see below)
- No local Maven install needed; use the bundled wrapper (`./mvnw`)

## 1. Create the database

Create an empty schema named `budgetcontrol`. The app doesn't create it for you — Liquibase only creates the tables *inside* it on startup:

```sql
CREATE DATABASE budgetcontrol;
```

## 2. Configure credentials

The datasource is `jdbc:mysql://localhost:3306/budgetcontrol`, configured in `src/main/resources/application.properties`. Username/password come from environment variables, defaulting to `root` / empty password:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

If your MySQL isn't on `localhost:13306`, edit `spring.datasource.url` in `application.properties` directly.

## 3. Run the app

```bash
./mvnw spring-boot:run
```

On startup, Liquibase creates the `category` and `operation` tables and seeds a default list of categories. The app listens on `http://localhost:8080`.

## 4. Try it out

Create a payment:

```bash
curl -X POST http://localhost:8080/api/operations \
  -H "Content-Type: application/json" \
  -d '{"type":"PAYMENT","amount":45.90,"description":"groceries","categoryId":4}'
```

List categories (to find a valid `categoryId`):

```bash
curl http://localhost:8080/api/categories
```

Get a monthly report:

```bash
curl "http://localhost:8080/api/reports/monthly?year=2026&month=8"
```

### API reference

| Method | Path | Notes |
|---|---|---|
| POST | `/api/operations` | Body: `type` (`PAYMENT`/`INCOME`), `amount`, `description`, `categoryId`, optional `date` (`dd/MM/yyyy`, defaults to today) |
| PUT | `/api/operations/{id}` | Same body as create |
| DELETE | `/api/operations/{id}` | |
| GET | `/api/operations/{id}` | |
| GET | `/api/operations` | List all |
| GET | `/api/categories` | List available categories |
| GET | `/api/reports/monthly?year=&month=` | Full calendar month, broken down by category |
| GET | `/api/reports/custom?startDate=&endDate=` | `dd/MM/yyyy`; range can't cross a year boundary |
| GET | `/api/reports/yearly?year=` | Month-by-month breakdown; only elapsed months for the current year |

## Other commands

- Build: `./mvnw clean install`
- Run all tests (uses an in-memory H2 database, no MySQL needed): `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=OperationControllerTest`
- Package: `./mvnw clean package`

## More detail

See `CLAUDE.md` for the architecture (hexagonal: `domain/` / `application/` / `adapter/`) and `doc/specs/financial-operations-tracking.md` for the underlying business rules.
