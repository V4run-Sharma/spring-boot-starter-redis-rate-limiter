package io.github.v4runsharma.ratelimiter.core;

import io.github.v4runsharma.ratelimiter.exception.RateLimitExceededException;
import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;

/**
 * High-level entry point for enforcing rate limits against an invocation context.
 * Why this exists:
 * - Keeps orchestration logic (resolve key + resolve policy + evaluate) behind one contract.
 * - Allows AOP / web interceptors to depend on a single abstraction.
 */
public interface RateLimitEnforcer {

  /**
   * Evaluate the limit for the given context.
   * Typical flow in an implementation:
   * - resolve key from context
   * - resolve policy from context
   * - call RateLimiter.evaluate(key, policy)
   *
   * @return decision describing whether the invocation is allowed
   */
  RateLimitDecision evaluate(RateLimitContext context);

  /**
   * Enforce the limit for the given context.
   * Typical flow in an implementation:
   * - call {@link #evaluate(RateLimitContext)}
   * - if denied, throw {@link RateLimitExceededException}
   */
  void enforce(RateLimitContext context) throws RateLimitExceededException;
}
