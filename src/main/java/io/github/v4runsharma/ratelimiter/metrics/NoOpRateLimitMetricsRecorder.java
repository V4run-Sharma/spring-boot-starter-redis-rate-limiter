package io.github.v4runsharma.ratelimiter.metrics;

import io.github.v4runsharma.ratelimiter.model.RateLimitDecision;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;
import java.time.Duration;

/**
 * Default metrics recorder used when metrics are disabled/unavailable.
 */
public final class NoOpRateLimitMetricsRecorder implements RateLimitMetricsRecorder {

  @Override
  public void recordDecision(String name, RateLimitPolicy policy, RateLimitDecision decision, Duration latency) {
    // intentionally no-op
  }

  @Override
  public void recordError(String name, RateLimitPolicy policy, Duration latency, Throwable error) {
    // intentionally no-op
  }
}
