# spring-boot-starter-redis-ratelimiter

Redis-backed, annotation-driven rate limiting for Spring Boot 3.x (Java 17+).

## Features

- `@RateLimit` for method/class-level throttling
- Redis fixed-window algorithm (`INCR` + TTL), no Lua required
- Spring Boot auto-configuration
- HTTP `429` mapping with optional rate-limit headers
- Micrometer metrics for allowed/blocked/error outcomes
- Configurable backend failure mode (`fail-open` or `fail-closed`)

## Installation

```xml
<dependency>
  <groupId>io.github.v4runsharma</groupId>
  <artifactId>spring-boot-starter-redis-ratelimiter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

```java
import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class BillingService {

  @RateLimit(name = "invoice-create", scope = "USER", limit = 10, duration = 1, timeUnit = TimeUnit.MINUTES)
  public String createInvoice(String accountId) {
    return "ok";
  }
}
```

## Configuration

| Property | Default | Description |
|---|---|---|
| `ratelimiter.enabled` | `true` | Enables/disables all starter beans. |
| `ratelimiter.redis-key-prefix` | `ratelimiter` | Prefix used for Redis keys. |
| `ratelimiter.fail-open` | `false` | If `true`, allows requests when Redis backend fails. |
| `ratelimiter.include-http-headers` | `true` | Adds `Retry-After` and `RateLimit-*` headers to `429`. |
| `ratelimiter.metrics-enabled` | `true` | Enables Micrometer metrics recorder when registry exists. |

## HTTP Behavior

When throttled, the starter throws `RateLimitExceededException` and returns:

- Status: `429 Too Many Requests`
- Headers (default): `Retry-After`, `RateLimit-Limit`, `RateLimit-Remaining`, `RateLimit-Reset`
- Body: RFC7807 `ProblemDetail` payload

## Metrics

When Micrometer is present:

- Counter: `ratelimiter.requests` (`outcome=allowed|blocked`)
- Counter: `ratelimiter.errors`
- Timer: `ratelimiter.evaluate.latency`

## Testing Notes

- Unit tests run with `mvn test`.
- Redis integration tests are property-gated and run with `mvn verify -DrunITs=true`.
- Local integration tests require a running Docker engine (Docker Desktop on macOS).

## Release

See `CHANGELOG.md` for version history.  
For signed release artifacts, run with `-DperformRelease=true` and configured `ossrh` credentials.
