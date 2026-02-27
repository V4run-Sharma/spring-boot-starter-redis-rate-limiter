package io.github.v4runsharma.ratelimiter.key;

import io.github.v4runsharma.ratelimiter.core.RateLimitContext;

/**
 * Resolves a stable rate limit key for an invocation.
 * What it’s used for:
 * - The returned key is what the {@code RateLimiter} charges against.
 * - Different resolvers allow per-user, per-IP, per-API-key, per-endpoint strategies.
 */
public interface RateLimitKeyResolver {

  /**
   * Compute a key identifying the caller/bucket.
   * <p>
   * Guidelines:
   * - Return a non-empty, stable string (same caller -> same key).
   * - Prefer predictable formats (e.g., "user:123", "ip:203.0.113.10").
   */
  String resolveKey(RateLimitContext context);
}
