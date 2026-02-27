package io.github.v4runsharma.ratelimiter.key;

import io.github.v4runsharma.ratelimiter.annotation.RateLimit;
import io.github.v4runsharma.ratelimiter.core.RateLimitContext;
import io.github.v4runsharma.ratelimiter.model.RateLimitScope;
import java.util.Objects;

/**
 * Default resolver that creates stable keys from scope + annotation/static method metadata.
 */
public final class DefaultRateLimitKeyResolver implements RateLimitKeyResolver {

  @Override
  public String resolveKey(RateLimitContext context) {
    Objects.requireNonNull(context, "context must not be null");
    RateLimit annotation = Objects.requireNonNull(context.getAnnotation(), "annotation must not be null");

    String scope = normalizeScope(annotation.scope());
    if (annotation.key() != null && !annotation.key().isBlank()) {
      return scope + ":" + annotation.key().trim();
    }

    return scope + ":" + context.getTargetClass().getName() + "#" + context.getMethod().getName();
  }

  private static String normalizeScope(String rawScope) {
    if (rawScope == null || rawScope.isBlank()) {
      return RateLimitScope.GLOBAL.getScope().toLowerCase();
    }
    return RateLimitScope.from(rawScope).getScope().toLowerCase();
  }
}
