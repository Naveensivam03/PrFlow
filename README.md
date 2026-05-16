# PRFlow

Event-driven PR review orchestration and intelligence platform integrated with GitHub.

## Monorepo Structure
- `backend/spring-api` — Core orchestration API (Spring Boot, Java 21)
- `integrations/github-webhook-service` — GitHub webhook ingestion service (Node.js + TypeScript)
- `frontend/dashboard` — Future UI dashboard (Next.js)
- `infra/docker` — Local infrastructure definitions (to be added)
- `infra/scripts` — Operational/dev scripts
- `docs/architecture` — Architecture decisions and diagrams
- `docs/workflows` — Product and orchestration workflows
- `docs/database` — Schema and migration strategy docs
- `docs/events` — Event contracts and lifecycle docs
- `docs/engines` — Review/intelligence engine documentation

## Current Status
Repository initialized with foundational structure only.
No application code, Docker definitions, or service bootstrapping yet.

## Tech Stack (Planned)
- Spring Boot (Java 21)
- PostgreSQL
- Redis
- Node.js + TypeScript
- Flyway
- Docker (local infra)
- Next.js (later phase)

## Getting Started
1. Clone repository
2. Review docs in `docs/`
3. Start service scaffolding phase

## Contributing
- Use feature branches
- Keep PRs focused and small
- Update docs for architectural changes

## License
Proprietary (or replace with your chosen license)
