AI Contract Analyzer

A full-stack Java + React application that extracts, analyzes, and visualizes contract intelligence using AI-driven processing. Built to be developer-friendly and recruiter-ready: clear architecture, easy setup, and production-ready deployment options.

## Live link


```bash
https://aicontract-ecru.vercel.app
```
## Key Highlights

- **Purpose:** Upload contracts, extract structured information, run AI analysis, and visualize results on a dashboard for faster contract review.
- **Frontend:** React + Vite, single-page app with authentication and analytics views.
- **Backend:** Spring Boot (Java) REST API handling AI processing, persistence, and security.
- **Dev & Prod Ready:** Run locally with Maven and Vite, or containerize via Docker / Docker Compose.

## Why this project stands out

- Complete full-stack example showing modern Java backend + modern React frontend.
- Clear separations of concerns: API layer, service layer, AI processing, persistence, and UI.
- Real-world use case (contract intelligence) with potential extensions for legal-tech portfolios.
- Easy for recruiters to evaluate: meaningful features, clean structure, and runnable demo.

## Repo Structure (high level)

- `client/` — React frontend (Vite)
  - `src/` — UI components, pages, and services
  - `package.json` — frontend scripts & dependencies
- `server/` — Java Spring Boot backend
  - `src/main/java/com/aicontract/contractanalyzer/controller` — REST controllers
  - `src/main/java/com/aicontract/contractanalyzer/ai` — AI processing service and DTOs
  - `src/main/resources/application.yml` — configuration
- `docker-compose.yml` — top-level service orchestration for full stack
- `uploads/` — runtime uploads directory for contract files

See the code for details in the folders above: the API controllers are in [server/src/main/java/com/aicontract/contractanalyzer/controller](server/src/main/java/com/aicontract/contractanalyzer/controller) and the frontend app entry is [client/src/App.jsx](client/src/App.jsx).

## Tech Stack

- Frontend: React, Vite, Axios
- Backend: Java, Spring Boot, Maven
- Containerization: Docker, Docker Compose
- Testing: (project contains test sources under `server/src/test/java`)

## Features / Use Cases

- Upload contract documents (PDF/DOCX) and store them in `uploads/`.
- AI-driven extraction and analysis of clauses, dates, parties, and obligations.
- Dashboard with analytics cards and charts for contract metrics.
- Authentication and protected routes for user-specific data.

## Getting Started — Local Development

Prerequisites

- Java 17+ (or the version used by the project), Node.js 16+/18+, npm or pnpm, Docker (optional)
- On Windows, use the provided `mvnw.cmd` wrapper; on macOS/Linux, use `./mvnw`.

Run backend (development)

```bash
cd server
# Windows
.\mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```

Run frontend (development)

```bash
cd client
npm install
npm run dev
```

Open the app

- Frontend dev server typically at `http://localhost:5173` (Vite) — check terminal output.
- Backend API typically at `http://localhost:8080` — check `application.yml` for overrides.

Run with Docker (recommended for demo)

```bash
docker-compose up --build
```

This will build and start services defined in `docker-compose.yml` and expose the frontend and backend for testing.

## API Overview

The backend exposes REST endpoints implemented in the `controller` package. Example capabilities include:

- Authentication endpoints (login / register)
- Contract upload endpoint (file POST) — stores files in `uploads/`
- Contract analysis endpoint — triggers AI processing and returns structured results
- Contract retrieval and analytics endpoints for dashboard data

Inspect the controllers folder for concrete routes and request/response DTOs: [server/src/main/java/com/aicontract/contractanalyzer/controller](server/src/main/java/com/aicontract/contractanalyzer/controller).

## Developer notes & extension ideas

- Swap or extend the AI processing service to use external LLMs or local pipelines.
- Add CI: GitHub Actions for build/test and container image publishing.
- Add e2e tests (Cypress / Playwright) to cover upload → analysis → dashboard flows.
- Add RBAC roles, tenant isolation, or exportable reports (PDF/CSV).

## Tests

Unit and integration tests are located under `server/src/test/java` — run them with Maven:

```bash
cd server
.\mvnw.cmd test   # Windows
./mvnw test       # macOS / Linux
```

## Contributing

Contributions are welcome. For meaningful changes:

1. Fork the repo and create a feature branch.
2. Run unit tests and ensure formatting/linting passes.
3. Open a Pull Request describing the change and its rationale.


