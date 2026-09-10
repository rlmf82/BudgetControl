# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

springboot service to manage personal budget.

## Commands

Use the Maven wrapper (`./mvnw`), not a system-installed `mvn`.

- Build: `./mvnw clean install`
- Run the app: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=BudgetControlApplicationTests`
- Run a single test method: `./mvnw test -Dtest=BudgetControlApplicationTests#contextLoads`
- Package: `./mvnw clean package`

## Architecture

This is a Spring Boot 4.1.0 app on Java 21, Maven-built, base package `rlmf.java.budgetcontrol`.

- **Web/persistence starters use Spring Boot 4's modular naming**, not the classic `spring-boot-starter-web`/`spring-boot-starter-test`: the app depends on `spring-boot-starter-webmvc` and `spring-boot-starter-data-jpa`, and tests depend on `spring-boot-starter-webmvc-test` and `spring-boot-starter-data-jpa-test`. Autoconfiguration itself is modularized too (e.g. `org.springframework.boot.hibernate.autoconfigure.HibernateJpaConfiguration`) — Liquibase needs `spring-boot-starter-liquibase`, not a bare `liquibase-core` dependency, or its autoconfiguration never runs and Hibernate's schema validation fails with "missing table" (Liquibase never got a chance to create it). Keep this naming convention when adding related dependencies.
- **Persistence**: PostgreSQL is the default datasource (`spring.datasource.*` in `application.properties`, pointed at `jdbc:postgresql://localhost:5432/budgetcontrol` via `DB_HOST`/`DB_PORT`, credentials via `DB_USERNAME`/`DB_PASSWORD` env vars defaulting to `postgres`/`root`) — you must create the `budgetcontrol` database yourself and have PostgreSQL reachable; Liquibase creates the tables on startup. H2 is only used for tests (`src/test/resources/application.properties`, in-memory). `spring.jpa.hibernate.ddl-auto=validate` — schema changes always go through a new Liquibase changeset under `src/main/resources/db/changelog/changes/`, never by letting Hibernate generate DDL.
- **`doc/specs/`** holds business-rule specifications derived from user stories, written in a "Rule / Example / Counter-example / Questions" format (deliberately not Gherkin/Given-When-Then). Before implementing a feature, check whether a spec for it already exists there — it captures the agreed business rules and resolved clarifying questions that should drive the implementation.
- **Jackson is on the 3.x line (`tools.jackson.*`), not the classic 2.x `com.fasterxml.jackson.databind`**: `ObjectMapper`/`JsonNode`/etc. live under `tools.jackson.databind`. Only `jackson-annotations` (`@JsonFormat`, `@JsonProperty`, ...) stayed under `com.fasterxml.jackson.annotation`. Test code that builds/parses JSON (e.g. via an autowired `ObjectMapper`) needs the `tools.jackson.databind` import or it won't compile.
- MockMvc test annotations are modularized like the starters: `AutoConfigureMockMvc` is `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`, not the classic `org.springframework.boot.test.autoconfigure.web.servlet` package.

## Coding Conventions

### Money
BigDecimal for ALL monetary values. NEVER float, double, or int.
Always explicit RoundingMode. Cashback: RoundingMode.DOWN, scale 2.
BigDecimal.valueOf() or new BigDecimal("...") — NEVER new BigDecimal(double).

### Java 25
Records for value objects, sealed interfaces, pattern matching.
No Lombok — records replace it.

### REST & Spring
Constructor injection only (no field @Autowired).
@Valid on request bodies. 201 create, 200 query, 400 validation, 404 not found.
Domain exceptions for business rule violations. Map to HTTP in controller only.
Never swallow exceptions or leak infrastructure details.

## Project Structure: Hexagonal (Ports & adapters)

Domain (domain/): Pure Java. NO spring, NO framework dependencies.
    model/ - entities and value objects
    service/ - business rules
Application (application/): ports/in and ports/out interfaces.
    @Service orchestration only, no business logic here.
Adapters:
    adapter/in/web - @RestController, DTO only.
    adapter/out/persistence - JPA repositories and entities(NOT in domain).

NEVER import adapter classes from domain.

Both inbound and outbound ports live under application/ports/:
- application/ports/in/ — driving ports (OperationUseCase, CategoryUseCase, ReportUseCase), implemented by the *Service classes in application/. adapter/in/web/ controllers depend on the interface type, never the concrete Service.
- application/ports/out/ — driven ports (OperationRepository, CategoryRepository), implemented by adapter/out/persistence/ adapters. The *Service classes depend on these interfaces, never on a concrete adapter.

Business rules that are true of the model itself (e.g. an Operation's amount must be > 0, its date can't be in the future) live in domain/service/ as pure-Java classes (e.g. OperationRules, ReportRules) operating on domain/model/ records — never inside a Spring-managed *Service. application/*Service classes call domain/service/ for the rule, and application/ports/out/ for persistence; they don't decide anything themselves.

## Development Process

Follow these steps for every feature. Do NOT skip steps.

Step 1: Discovery — Run /discover.
Propose rules, surface questions with options, let the user decide.
Save draft spec to docs/specs/.
STOP. User reviews, edits, and annotates the spec.
Do NOT proceed if the spec has unresolved questions.
Re-read the final spec before continuing.

Step 2: Acceptance Test — Write test for the NEXT rule only.
@Nested = rule, test = example. @SpringBootTest + MockMvc.
Complete Step 3 until this rule is GREEN before writing the next.
Never modify a test to make it pass. Fix the production code.
No @Disabled, no loosened assertions, no swallowed exceptions.

Step 3: TDD (Inner Loop) — RED → GREEN → REFACTOR.
Write ONE failing test. Minimum code to pass. Refactor.
Run ALL tests. STOP after each cycle.

Step 4: Review — Verify coverage, boundaries, no AI smells.
Update CLAUDE.md if new conventions emerged.

Step 5: Add archunit-junit5 to tests the architecture. Consider the latest version.