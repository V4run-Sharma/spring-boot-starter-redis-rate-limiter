# Spring Boot Starter Redis RateLimiter

This guide is written so you can share it directly in a LinkedIn post and so other developers can copy-paste it into their projects.

## What This Starter Gives You

- Annotation-driven rate limiting with `@RateLimit`
- Redis-backed fixed-window counter (`INCR` + TTL)
- Auto-configured AOP interceptor (no manual aspect wiring)
- Automatic HTTP `429` handling for Spring MVC (Servlet apps)
- Micrometer metrics for allowed/blocked/error outcomes

## Requirements

- Java 17+
- Spring Boot 3.x
- Redis reachable from your app

## 1) Add Dependency

`pom.xml`:

```xml
<dependency>
  <groupId>io.github.v4run-sharma</groupId>
  <artifactId>spring-boot-starter-redis-ratelimiter</artifactId>
  <version>1.0.1</version>
</dependency>
```

Gradle:

```gradle
implementation("io.github.v4run-sharma:spring-boot-starter-redis-ratelimiter:1.0.1")
```

## 2) Configure Redis

`application.yml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Local Redis (Docker):

```bash
docker run --name redis-ratelimiter -p 6379:6379 -d redis:7-alpine
```

## 3) Add `@RateLimit` to Service Methods

```java
import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

  @RateLimit(
      name = "invoice-create",
      scope = "GLOBAL",
      limit = 10,
      duration = 1,
      timeUnit = TimeUnit.MINUTES
  )
  public String createInvoice(String accountId) {
    return "ok";
  }
}
```

## 4) Understand Keying (Important)

By default, this starter resolves keys like this:

- If `key` is set: `scope:key`
- If `key` is not set: `scope:fully.qualified.ClassName#methodName`

That means `scope` is a label, not identity by itself.  
If you need per-user or per-tenant limits, use a custom `RateLimitKeyResolver`.

## 5) Per-User Example with Custom Key Resolver

```java
import io.github.v4runsharma.ratelimiter.core.RateLimitContext;
import io.github.v4runsharma.ratelimiter.key.RateLimitKeyResolver;
import org.springframework.stereotype.Component;

@Component
public class UserIdKeyResolver implements RateLimitKeyResolver {
  @Override
  public String resolveKey(RateLimitContext context) {
    Object[] args = context.getArguments();
    String userId = String.valueOf(args[0]); // Example: first argument is userId
    return "user:" + userId + ":" + context.getMethod().getName();
  }
}
```

Use it in annotation:

```java
@RateLimit(
    name = "invoice-create-per-user",
    scope = "USER",
    keyResolver = UserIdKeyResolver.class,
    limit = 5,
    duration = 1,
    timeUnit = TimeUnit.MINUTES
)
public String createInvoice(String userId, String accountId) {
  return "ok";
}
```

## 6) Class-Level Annotation

You can place `@RateLimit` on class or method.  
Method-level annotation takes precedence over class-level annotation.

## 7) What Happens When Limit Is Exceeded

In Spring MVC (Servlet apps), starter returns:

- HTTP `429 Too Many Requests`
- RFC7807 `ProblemDetail` body
- Optional headers (`Retry-After`, `RateLimit-Limit`, `RateLimit-Remaining`, `RateLimit-Reset`)

Example response body:

```json
{
  "type": "about:blank",
  "title": "Rate limit exceeded",
  "status": 429,
  "detail": "Rate limit exceeded: invoice-create (limit=10, window=PT1M, remaining=34000)",
  "timestamp": "2026-02-27T12:00:00Z",
  "key": "global:com.example.BillingService#createInvoice",
  "limit": 10,
  "windowSeconds": 60,
  "name": "invoice-create",
  "retryAfterSeconds": 34
}
```

## 8) Starter Properties

```properties
ratelimiter.enabled=true
ratelimiter.redis-key-prefix=ratelimiter
ratelimiter.fail-open=false
ratelimiter.include-http-headers=true
ratelimiter.metrics-enabled=true
```

Property meanings:

- `ratelimiter.enabled`: master feature toggle
- `ratelimiter.redis-key-prefix`: prefix for Redis bucket keys
- `ratelimiter.fail-open`: if `true`, allow requests when Redis is down
- `ratelimiter.include-http-headers`: include rate limit headers in 429
- `ratelimiter.metrics-enabled`: enable Micrometer metrics recorder

## 9) Metrics You Get

When Micrometer is present:

- `ratelimiter.requests` counter
- `ratelimiter.errors` counter
- `ratelimiter.evaluate.latency` timer

Useful metric tags include:

- `name`
- `scope`
- `outcome` (`allowed` or `blocked`)
- `exception` (for error metric)

## 10) Quick Validation Checklist

1. Start Redis.
2. Start your Spring Boot app.
3. Hit a `@RateLimit`-protected endpoint repeatedly.
4. Confirm HTTP `429` after threshold is crossed.
5. Confirm Redis keys are created with your configured prefix.
6. Confirm metrics appear in your meter registry.

## 11) Test Commands (from this project)

```bash
mvn test
mvn verify -DrunITs=true
```

Integration tests require Docker (Testcontainers).

