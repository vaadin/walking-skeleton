package com.example.application.security;

import com.example.application.security.domain.UserId;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Caching decorator for {@link AppUserInfoLookup} that provides in-memory caching of user information.
 * <p>
 * This implementation wraps another {@link AppUserInfoLookup} and caches successful lookups
 * in memory to reduce the number of expensive remote calls. The cache has configurable
 * expiration and maximum size limits to prevent memory issues.
 * </p>
 * <p>
 * The cache is thread-safe and handles concurrent access properly. Cache misses and
 * lookup failures are not cached, ensuring that temporary failures don't prevent
 * future successful lookups.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * AppUserInfoLookup keycloakLookup = new KeycloakAppUserInfoLookup(credentials);
 * AppUserInfoLookup cachedLookup = CachingAppUserInfoLookup.builder()
 *     .delegate(keycloakLookup)
 *     .maxSize(1000)
 *     .expireAfterWrite(Duration.ofMinutes(15))
 *     .build();
 * }
 * </pre>
 * </p>
 *
 * @see AppUserInfoLookup The interface this decorator implements
 */
public final class CachingAppUserInfoLookup implements AppUserInfoLookup {

    private final AppUserInfoLookup delegate;
    private final Cache<UserId, AppUserInfo> cache;

    /**
     * Creates a new caching lookup with the specified delegate and cache
     * configuration.
     *
     * @param delegate
     *            the underlying lookup service to delegate to
     * @param cache
     *            the cache implementation to use for storing results
     */
    private CachingAppUserInfoLookup(AppUserInfoLookup delegate, Cache<UserId, AppUserInfo> cache) {
        this.delegate = requireNonNull(delegate, "delegate must not be null");
        this.cache = requireNonNull(cache, "cache must not be null");
    }

    @Override
    public Optional<AppUserInfo> findUserInfo(UserId userId) {
        var cached = cache.getIfPresent(userId);
        if (cached != null) {
            return Optional.of(cached);
        }

        var result = delegate.findUserInfo(userId);
        result.ifPresent(appUserInfo -> cache.put(userId, appUserInfo));

        return result;
    }

    /**
     * Creates a new builder for configuring a caching lookup.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link CachingAppUserInfoLookup} instances with custom
     * configuration.
     */
    public static class Builder {
        private @Nullable AppUserInfoLookup delegate;
        private long maxSize = 1000;
        private @Nullable Duration expireAfterWrite = Duration.ofMinutes(15);
        private @Nullable Duration expireAfterAccess = null;

        /**
         * Sets the delegate lookup service.
         *
         * @param delegate
         *            the underlying lookup service
         * @return this builder
         */
        public Builder delegate(AppUserInfoLookup delegate) {
            this.delegate = requireNonNull(delegate, "delegate must not be null");
            return this;
        }

        /**
         * Sets the maximum number of entries in the cache.
         *
         * @param maxSize
         *            the maximum cache size
         * @return this builder
         */
        public Builder maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Sets the expiration time after write.
         *
         * @param duration
         *            the expiration duration
         * @return this builder
         */
        public Builder expireAfterWrite(@Nullable Duration duration) {
            this.expireAfterWrite = duration;
            return this;
        }

        /**
         * Sets the expiration time after access.
         *
         * @param duration
         *            the expiration duration
         * @return this builder
         */
        public Builder expireAfterAccess(@Nullable Duration duration) {
            this.expireAfterAccess = duration;
            return this;
        }

        /**
         * Builds the caching lookup with the configured settings.
         *
         * @return a new caching lookup instance
         * @throws IllegalStateException
         *             if delegate is not set
         */
        public CachingAppUserInfoLookup build() {
            if (delegate == null) {
                throw new IllegalStateException("Delegate lookup must be set");
            }

            var cacheBuilder = CacheBuilder.newBuilder().maximumSize(maxSize);

            if (expireAfterWrite != null) {
                cacheBuilder.expireAfterWrite(expireAfterWrite);
            }

            if (expireAfterAccess != null) {
                cacheBuilder.expireAfterAccess(expireAfterAccess);
            }

            Cache<UserId, AppUserInfo> cache = cacheBuilder.build();

            return new CachingAppUserInfoLookup(delegate, cache);
        }
    }
}
