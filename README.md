# Supermarket DevSecOps

[![CI](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/ci.yml/badge.svg)](https://github.com/joanfrasquet/supermarket-devsecops/actions/workflows/ci.yml)

Inventory and order management for a supermarket, built with **Spring Boot** and **React**, with a CI/CD pipeline that tests and security-scans every commit.

> 🚧 Work in progress. See the roadmap below.

## Tech stack

| Layer | Tech |
| --- | --- |
| Backend | Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL |
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
- [x] Basic CI: build, tests and Docker image
- [ ] Full product CRUD and orders
- [ ] Authentication with JWT and roles (ADMIN, EMPLOYEE)
- [ ] React frontend
- [ ] Security pipeline: CodeQL, Semgrep, dependency scanning, Gitleaks, Trivy

## Security

- Secrets live in `.env`, which is git-ignored. `.env.example` documents the variables.
- The backend container runs as a non-root user on a minimal JRE image.

## License

MIT
