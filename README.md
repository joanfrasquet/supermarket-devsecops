# Supermarket DevSecOps

[![CI](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/ci.yml/badge.svg)](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/ci.yml)
[![CodeQL](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/codeql.yml/badge.svg)](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/codeql.yml)

Inventory and order management for a supermarket, built with **Spring Boot** and **React**, with a CI/CD pipeline that tests and security-scans every commit.

> 🚧 Work in progress. See the roadmap below.

## Tech stack

| Layer | Tech |
| --- | --- |
| Backend | Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL |
| Frontend | React + Vite + TypeScript *(coming soon)* |
| Infrastructure | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## Run it locally

Requirements: Docker and Java 21.

```bash
cp .env.example .env          # then edit the password
docker compose up --build     # starts PostgreSQL and the API
```

The API is available at `http://localhost:8080/api/products` and the health check at `http://localhost:8080/actuator/health`.

Run the backend tests:

```bash
cd backend
./mvnw test
```

## Roadmap

- [x] Project structure, PostgreSQL with Docker, Spring Boot skeleton
- [x] CI: build, tests and Docker image
- [x] Product CRUD with input validation
- [ ] Orders
- [ ] Authentication with JWT and roles (ADMIN, EMPLOYEE)
- [ ] React frontend
- [x] Security pipeline: CodeQL, Semgrep, Gitleaks, Trivy and Dependabot

## Security

- Secrets live in `.env`, which is git-ignored. `.env.example` documents the variables.
- The backend container runs as a non-root user on a minimal JRE image.
- Requests use a dedicated `ProductRequest` without an `id`, so clients cannot overwrite other products (mass assignment).
- Every push runs:
  - **CodeQL** and **Semgrep** (OWASP Top 10 rules) for static code analysis
  - **Gitleaks** to catch committed secrets
  - **Trivy** to scan the Docker image, failing on critical vulnerabilities
  - **Dependabot** opens weekly PRs for outdated Maven, Docker and Actions dependencies

## License

MIT
