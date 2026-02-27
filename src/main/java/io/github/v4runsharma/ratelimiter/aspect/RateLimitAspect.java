package io.github.v4runsharma.ratelimiter.aspect;

import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import io.github.v4runsharma.ratelimiter.core.RateLimitEnforcer;
import io.github.v4runsharma.ratelimiter.support.DefaultRateLimitContext;
import java.lang.reflect.Method;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Method interceptor entrypoint that enforces {@link RateLimit} on methods and classes.
 */
public final class RateLimitAspect implements MethodInterceptor {

  private final RateLimitEnforcer rateLimitEnforcer;

  public RateLimitAspect(RateLimitEnforcer rateLimitEnforcer) {
    this.rateLimitEnforcer = Objects.requireNonNull(rateLimitEnforcer, "rateLimitEnforcer must not be null");
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Method interfaceMethod = invocation.getMethod();
    Class<?> targetClass = resolveTargetClass(invocation.getThis(), interfaceMethod.getDeclaringClass());
    Method method = AopUtils.getMostSpecificMethod(interfaceMethod, targetClass);
    RateLimit annotation = resolveAnnotation(method, targetClass);

    if (annotation == null || !annotation.enabled()) {
      return invocation.proceed();
    }

    DefaultRateLimitContext context = new DefaultRateLimitContext(
        annotation,
        targetClass,
        method,
        invocation.getArguments(),
        invocation.getThis()
    );

    rateLimitEnforcer.enforce(context);
    return invocation.proceed();
  }

  private static Class<?> resolveTargetClass(Object target, Class<?> fallback) {
    if (target == null) {
      return fallback;
    }
    return AopUtils.getTargetClass(target);
  }

  private static RateLimit resolveAnnotation(Method method, Class<?> targetClass) {
    RateLimit methodLevel = AnnotatedElementUtils.findMergedAnnotation(method, RateLimit.class);
    if (methodLevel != null) {
      return methodLevel;
    }
    return AnnotatedElementUtils.findMergedAnnotation(targetClass, RateLimit.class);
  }
}
