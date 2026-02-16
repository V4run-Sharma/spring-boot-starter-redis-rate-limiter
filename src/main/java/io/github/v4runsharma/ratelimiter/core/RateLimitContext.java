package io.github.v4runsharma.ratelimiter.core;


import java.lang.reflect.Method;

/**
 * Invocation context used by the starter to resolve:
 * - which policy to enforce (via a policy provider)
 * - which key/bucket to charge (via a key resolver)
 * <p>
 * This is intentionally framework-agnostic so the same API can be used by AOP,
 * servlet filters, reactive interceptors, etc.
 */
public interface RateLimitContext {

  /**
   * The effective {@link RateLimit} annotation for this invocation.
   * Used to read declared defaults such as limit/window, name, enabled flag, etc.
   */
  RateLimit getAnnotation();

  /**
   * The target class that declares or is proxied for the invocation.
   * Useful for composing keys, tagging metrics, and annotation lookup strategies.
   */
  Class<?> getTargetClass();

  /**
   * The method being invoked.
   * Useful for building keys (e.g., per-endpoint) and attaching method-level metadata.
   */
  Method getMethod();

  /**
   * Arguments passed to the invocation.
   * Key resolvers can use this to pull identifiers (e.g., userId parameter).
   */
  Object[] getArguments();

  /**
   * The target object instance (may be {@code null} for static methods).
   * Useful when a resolver needs instance state or to detect proxy details.
   */
  Object getTarget();
}
