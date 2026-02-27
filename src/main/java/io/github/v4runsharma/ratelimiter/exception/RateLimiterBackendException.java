package io.github.v4runsharma.ratelimiter.exception;

/**
 * Raised when the backing rate-limit store cannot be reached or used reliably.
 */
public final class RateLimiterBackendException extends RuntimeException {

  public RateLimiterBackendException(String message) {
    super(message);
  }

  public RateLimiterBackendException(String message, Throwable cause) {
    super(message, cause);
  }
}
