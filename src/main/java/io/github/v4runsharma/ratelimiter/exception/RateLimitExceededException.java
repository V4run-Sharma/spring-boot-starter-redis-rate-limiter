package io.github.v4runsharma.ratelimiter.exception;

import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;

import java.io.Serial;
import java.util.Objects;

/**
 * Raised when an invocation is not allowed by the current rate limit policy.
 * Typical usage:
 * - Aspect/interceptor throws this when {@link RateLimitDecision#isAllowed()} is false.
 * - Applications can translate it to HTTP 429 (Too Many Requests).
 */
public final class RateLimitExceededException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String name;
  private final String key;
  private final RateLimitPolicy policy;
  private final RateLimitDecision decision;

  public RateLimitExceededException(String key, RateLimitPolicy policy, RateLimitDecision decision) {
    this(null, key, policy, decision);
  }

  public RateLimitExceededException(String name, String key, RateLimitPolicy policy, RateLimitDecision decision) {
    super(buildMessage(name, key, policy, decision));
    this.name = (name == null || name.isBlank()) ? null : name;
    this.key = requireNonBlank(key);
    this.policy = Objects.requireNonNull(policy, "policy must not be null");
    this.decision = Objects.requireNonNull(decision, "decision must not be null");
    if (decision.isAllowed()) {
      throw new IllegalArgumentException("decision must represent a denied request (allowed=false)");
    }
  }

  /** Optional logical name for the limit (may be null). */
  public String getName() {
    return name;
  }

  /** The resolved bucket key that was rate limited. */
  public String getKey() {
    return key;
  }

  /** Policy that was enforced. */
  public RateLimitPolicy getPolicy() {
    return policy;
  }

  /** Decision details (retryAfter/resetAfter/remaining if known). */
  public RateLimitDecision getDecision() {
    return decision;
  }

  private static String buildMessage(String name, String key, RateLimitPolicy policy, RateLimitDecision decision) {
    String id = (name == null || name.isBlank()) ? key : name;
    return "Rate limit exceeded: " + id + " (limit=" + policy.getLimit()
        + ", window=" + policy.getWindow()
        + ", remaining=" + decision.getRemainingTime()
        + ")";
  }

  private static String requireNonBlank(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Key must not be blank");
    }
    return value;
  }
}
