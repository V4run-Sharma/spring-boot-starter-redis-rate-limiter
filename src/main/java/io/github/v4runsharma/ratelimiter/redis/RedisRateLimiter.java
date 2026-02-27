package io.github.v4runsharma.ratelimiter.redis;

import io.github.v4runsharma.ratelimiter.core.RateLimiter;
import io.github.v4runsharma.ratelimiter.exception.RateLimiterBackendException;
import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis-backed fixed-window rate limiter.
 * <p>Algorithm:
 * <p>- Derive a deterministic window bucket from current time and policy window.
 * <p>- Increment bucket counter with Redis INCR.
 * <p>- Set TTL when counter is created (first increment).
 */
public final class RedisRateLimiter implements RateLimiter {

  private static final String DEFAULT_KEY_PREFIX = "ratelimiter";
  private static final Duration TTL_SAFETY_BUFFER = Duration.ofSeconds(1);

  private final StringRedisTemplate redisTemplate;
  private final Clock clock;
  private final String keyPrefix;
  private final boolean failOpen;

  public RedisRateLimiter(StringRedisTemplate redisTemplate) {
    this(redisTemplate, Clock.systemUTC(), DEFAULT_KEY_PREFIX, false);
  }

  public RedisRateLimiter(StringRedisTemplate redisTemplate, Clock clock, String keyPrefix) {
    this(redisTemplate, clock, keyPrefix, false);
  }

  public RedisRateLimiter(StringRedisTemplate redisTemplate, Clock clock, String keyPrefix, boolean failOpen) {
    this.redisTemplate = Objects.requireNonNull(redisTemplate, "redisTemplate must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
    this.keyPrefix = requireNonBlank(keyPrefix, "keyPrefix must not be blank");
    this.failOpen = failOpen;
  }

  @Override
  public RateLimitDecision evaluate(String key, RateLimitPolicy policy) {
    String resolvedKey = requireNonBlank(key, "key must not be blank");
    RateLimitPolicy resolvedPolicy = Objects.requireNonNull(policy, "policy must not be null");

    long windowMillis = resolvedPolicy.getWindow().toMillis();
    if (windowMillis <= 0L) {
      throw new IllegalArgumentException("policy window must be positive");
    }

    long nowMillis = clock.millis();
    long windowStartMillis = nowMillis - (nowMillis % windowMillis);
    long resetAfterMillis = Math.max(1L, windowMillis - (nowMillis - windowStartMillis));
    String redisKey = buildRedisKey(resolvedKey, windowStartMillis);

    try {
      long currentCount = increment(redisKey, resolvedPolicy.getWindow().plus(TTL_SAFETY_BUFFER));
      boolean allowed = currentCount <= resolvedPolicy.getLimit();

      Duration resetAfter = Duration.ofMillis(resetAfterMillis);
      Duration retryAfter = allowed ? null : resetAfter;
      long remainingTime = allowed ? 0L : resetAfterMillis;

      return new RateLimitDecision(allowed, remainingTime, retryAfter, resetAfter);
    } catch (RuntimeException ex) {
      if (failOpen) {
        return new RateLimitDecision(
            true,
            RateLimitDecision.REMAINING_TIME_UNKNOWN,
            null,
            Duration.ofMillis(resetAfterMillis)
        );
      }
      throw new RateLimiterBackendException("Redis rate limiter backend failure for key: " + redisKey, ex);
    }
  }

  private long increment(String redisKey, Duration ttl) {
    Long current = redisTemplate.opsForValue().increment(redisKey);
    if (current == null) {
      throw new IllegalStateException("Redis INCR returned null for key: " + redisKey);
    }

    if (current == 1L) {
      Boolean ttlSet = redisTemplate.expire(redisKey, ttl);
      if (Boolean.FALSE.equals(ttlSet)) {
        throw new IllegalStateException("Failed to set TTL for key: " + redisKey);
      }
    }

    return current;
  }

  private String buildRedisKey(String key, long windowStartMillis) {
    return keyPrefix + ":" + key + ":" + windowStartMillis;
  }

  private static String requireNonBlank(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
}
