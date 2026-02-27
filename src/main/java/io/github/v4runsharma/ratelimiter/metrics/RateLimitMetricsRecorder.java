package io.github.v4runsharma.ratelimiter.metrics;

import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;
import java.time.Duration;

/**
 * Records rate-limit metrics for successful and failed evaluations.
 */
public interface RateLimitMetricsRecorder {

  void recordDecision(String name, RateLimitPolicy policy, RateLimitDecision decision, Duration latency);

  void recordError(String name, RateLimitPolicy policy, Duration latency, Throwable error);
}
