package io.github.v4runsharma.ratelimiter.core;

import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;

// Evaluates a rate limit for a given key and policy
public interface RateLimiter {

  RateLimitDecision evaluate(String key, RateLimitPolicy policy);
}

