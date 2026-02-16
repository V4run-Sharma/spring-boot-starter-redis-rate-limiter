package io.github.v4runsharma.ratelimiter.core;

import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;

/**
 * Resolves the effective {@link RateLimitPolicy} for an invocation.
 *
 * Why this exists:
 * - Keeps policy resolution (annotation defaults, config overrides later) separate from enforcement.
 * - Allows swapping strategies without changing the {@code RateLimiter} implementation.
 */
public interface RateLimitPolicyProvider {

  /**
   * Resolve the policy to enforce for the given invocation.
   *
   * Typical usage:
   * - Read {@code @RateLimit} values from {@link RateLimitContext#getAnnotation()}.
   * - Optionally apply overrides (e.g., properties, dynamic config) based on annotation name/method/class.
   */
  RateLimitPolicy resolvePolicy(RateLimitContext context);
}
