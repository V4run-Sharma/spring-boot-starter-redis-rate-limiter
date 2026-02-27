package io.github.v4runsharma.ratelimiter.model;

public enum RateLimitScope {

  GLOBAL("GLOBAL"),
  USER("USER"),
  IP("IP");

  private final String scope;

  RateLimitScope(String scope) {
    this.scope = scope;
  }

  public static RateLimitScope from(String scope) {
    if (scope == null || scope.isBlank()) {
      throw new IllegalArgumentException("Scope cannot be null or blank");
    }

    try {
      return RateLimitScope.valueOf(scope.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid scope: " + scope);
    }
  }

  public String getScope() {
    return scope;
  }

  public String getKey() {
    return this.name();
  }
}
