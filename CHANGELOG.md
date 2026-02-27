# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- Public API surface for rate-limiter annotation, model, and core contracts.
- Redis-backed fixed-window `RateLimiter` implementation.
- Spring Boot 3 auto-configuration via `AutoConfiguration.imports`.
- HTTP 429 exception handler with optional `RateLimit-*` headers.
- Micrometer metrics recorder integration.
- Auto-configuration tests and Redis limiter tests.
- Docker-optional integration tests for Redis behavior and concurrency.
