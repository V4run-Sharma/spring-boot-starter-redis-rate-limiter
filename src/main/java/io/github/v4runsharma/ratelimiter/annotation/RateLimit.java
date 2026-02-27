package io.github.v4runsharma.ratelimiter.annotation;

import io.github.v4runsharma.ratelimiter.key.RateLimitKeyResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Declares a rate limit for a method or a type.
 * This annotation is part of the public API only:
 * - It carries configuration metadata.
 * - Enforcement is done elsewhere (aspect/interceptor + RateLimiter).
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RateLimit {

  /**
   * Optional logical name for the limit.
   * Common uses: metrics tags, configuration override lookup, or documentation.
   */
  String name() default "";

  /**
   * Optional scope hint carried into the policy (e.g., "user", "ip").
   * How this is interpreted is up to the policy provider / key resolver strategy.
   */
  String scope() default "";

  /**
   * Maximum number of allowed requests within the window.
   */
  int limit();

  /**
   * Window size (in {@link #timeUnit()} units).
   */
  long duration();

  /**
   * Time unit for {@link #duration()}.
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * Key resolver type to compute the rate limit key for this annotation.
   * Note: defaulting to the interface type acts as a sentinel meaning
   * "not explicitly set"; the starter can substitute a default implementation.
   */
  Class<? extends RateLimitKeyResolver> keyResolver() default RateLimitKeyResolver.class;

  /**
   * Optional static key suffix to disambiguate limits without writing a custom resolver.
   */
  String key() default "";

  /**
   * Feature flag to disable enforcement without removing the annotation.
   */
  boolean enabled() default true;
}
