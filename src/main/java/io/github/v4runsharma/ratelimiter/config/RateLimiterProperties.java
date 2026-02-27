package io.github.v4runsharma.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * External configuration for the rate limiter starter.
 */
@ConfigurationProperties(prefix = "ratelimiter")
public class RateLimiterProperties {

  /**
   * Global switch for rate limiter auto-configuration.
   */
  private boolean enabled = true;

  /**
   * Redis key prefix used for rate-limit buckets.
   */
  private String redisKeyPrefix = "ratelimiter";

  /**
   * Failure strategy when Redis is unavailable.
   * <p>- false: fail-closed (throw exception)
   * <p>- true: fail-open (allow request)
   */
  private boolean failOpen = false;

  /**
   * Whether HTTP 429 responses should include rate-limit headers.
   */
  private boolean includeHttpHeaders = true;

  /**
   * Whether Micrometer metrics should be recorded when a MeterRegistry is present.
   */
  private boolean metricsEnabled = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getRedisKeyPrefix() {
    return redisKeyPrefix;
  }

  public void setRedisKeyPrefix(String redisKeyPrefix) {
    this.redisKeyPrefix = redisKeyPrefix;
  }

  public boolean isFailOpen() {
    return failOpen;
  }

  public void setFailOpen(boolean failOpen) {
    this.failOpen = failOpen;
  }

  public boolean isIncludeHttpHeaders() {
    return includeHttpHeaders;
  }

  public void setIncludeHttpHeaders(boolean includeHttpHeaders) {
    this.includeHttpHeaders = includeHttpHeaders;
  }

  public boolean isMetricsEnabled() {
    return metricsEnabled;
  }

  public void setMetricsEnabled(boolean metricsEnabled) {
    this.metricsEnabled = metricsEnabled;
  }
}
