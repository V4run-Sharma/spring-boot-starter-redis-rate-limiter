package io.github.v4runsharma.ratelimiter.model;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

// Result of a single rate limit evaluation.
public class RateLimitDecision {

  public static final long REMAINING_TIME_UNKNOWN = -1L;

  private final boolean isAllowed; // Whether the request is allowed
  private final long remainingTime; // Time until the next allowed request (in milliseconds)
  private final Duration retryAfter; // Optional duration until the next allowed request
  private final Duration resetAfter; // Optional duration until the rate limit resets

  public RateLimitDecision(boolean isAllowed, long remainingTime, Duration retryAfter, Duration resetAfter) {
    if (remainingTime < REMAINING_TIME_UNKNOWN) {
      throw new IllegalArgumentException("Remaining time cannot be less than " + REMAINING_TIME_UNKNOWN);
    }
    if (retryAfter != null && retryAfter.isNegative()) {
      throw new IllegalArgumentException("Retry after duration cannot be negative");
    }
    if (resetAfter != null && resetAfter.isNegative()) {
      throw new IllegalArgumentException("Reset after duration cannot be negative");
    }

    this.isAllowed = isAllowed;
    this.remainingTime = remainingTime;
    this.retryAfter = retryAfter;
    this.resetAfter = resetAfter;
  }

  public boolean isAllowed() {
    return isAllowed;
  }

  public long getRemainingTime() {
    return remainingTime;
  }

  public Optional<Duration> getRetryAfter() {
    return Optional.ofNullable(retryAfter);
  }

  public Optional<Duration> getResetAfter() {
    return Optional.ofNullable(resetAfter);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof RateLimitDecision that)) return false;

    return this.isAllowed == that.isAllowed
        && this.remainingTime == that.remainingTime
        && Objects.equals(this.retryAfter, that.retryAfter)
        && Objects.equals(this.resetAfter, that.resetAfter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isAllowed, remainingTime, retryAfter, resetAfter);
  }

  @Override
  public String toString() {
    return "RateLimitDecision{" +
        "allowed=" + isAllowed +
        ", remaining=" + remainingTime +
        ", retryAfter=" + retryAfter +
        ", resetAfter=" + resetAfter +
        '}';
  }

}
