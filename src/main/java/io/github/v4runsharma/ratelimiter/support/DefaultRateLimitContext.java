package io.github.v4runsharma.ratelimiter.support;

import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import io.github.v4runsharma.ratelimiter.core.RateLimitContext;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Default immutable implementation of {@link RateLimitContext}.
 */
public final class DefaultRateLimitContext implements RateLimitContext {

  private final RateLimit annotation;
  private final Class<?> targetClass;
  private final Method method;
  private final Object[] arguments;
  private final Object target;

  public DefaultRateLimitContext(
      RateLimit annotation,
      Class<?> targetClass,
      Method method,
      Object[] arguments,
      Object target
  ) {
    this.annotation = Objects.requireNonNull(annotation, "annotation must not be null");
    this.targetClass = Objects.requireNonNull(targetClass, "targetClass must not be null");
    this.method = Objects.requireNonNull(method, "method must not be null");
    this.arguments = arguments == null ? new Object[0] : arguments.clone();
    this.target = target;
  }

  @Override
  public RateLimit getAnnotation() {
    return annotation;
  }

  @Override
  public Class<?> getTargetClass() {
    return targetClass;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  @Override
  public Object[] getArguments() {
    return arguments.clone();
  }

  @Override
  public Object getTarget() {
    return target;
  }
}
