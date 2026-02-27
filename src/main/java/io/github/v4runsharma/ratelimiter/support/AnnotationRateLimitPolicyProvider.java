package io.github.v4runsharma.ratelimiter.support;

import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import io.github.v4runsharma.ratelimiter.core.RateLimitContext;
import io.github.v4runsharma.ratelimiter.core.RateLimitPolicyProvider;
import io.github.v4runsharma.ratelimiter.model.RateLimitPolicy;
import io.github.v4runsharma.ratelimiter.model.RateLimitScope;
import java.time.Duration;
import java.util.Objects;

/**
 * Builds a policy directly from {@link RateLimit} annotation values.
 */
public final class AnnotationRateLimitPolicyProvider implements RateLimitPolicyProvider {

  @Override
  public RateLimitPolicy resolvePolicy(RateLimitContext context) {
    Objects.requireNonNull(context, "context must not be null");

    RateLimit annotation = Objects.requireNonNull(context.getAnnotation(), "annotation must not be null");
    Duration window = Duration.of(annotation.duration(), annotation.timeUnit().toChronoUnit());

    String scope = annotation.scope();
    if (scope == null || scope.isBlank()) {
      scope = RateLimitScope.GLOBAL.getScope();
    }

    return new RateLimitPolicy(annotation.limit(), window, scope);
  }
}
