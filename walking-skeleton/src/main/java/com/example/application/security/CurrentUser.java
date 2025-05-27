package com.example.application.security;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for retrieving the currently authenticated user from the Spring Security context.
 * <p>
 * This class provides methods to safely access user information stored in the authentication principal, supporting
 * principals that implement {@link AppUserPrincipal}. It serves as a bridge between Spring Security's authentication
 * model and the application's user information model.
 * </p>
 * <p>
 * Usage examples:
 *
 * <!-- spotless:off -->
 * <pre>
 * {@code
 * // Get the current user if available
 * Optional<AppUserInfo> currentUser = CurrentUser.get();
 *
 * // Get the current user, throwing an exception if not authenticated
 * AppUserInfo user = CurrentUser.require();
 *
 * // Access user properties
 * String fullName = CurrentUser.require().fullName();
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 *
 * @see AppUserInfo The application's user information model
 * @see AppUserPrincipal The principal interface that provides access to user information
 * @see AppUserInfoLookup For looking up information about any user, not just the current one
 */
public final class CurrentUser {

    private static final Logger log = LoggerFactory.getLogger(CurrentUser.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CurrentUser() {
    }

    /**
     * Returns the currently authenticated user from the security context.
     * <p>
     * This method safely extracts user information from the current security context without throwing exceptions for
     * unauthenticated requests or incompatible principal types.
     * </p>
     * <p>
     * The method expects the authentication principal to implement {@link AppUserPrincipal}. If the principal doesn't
     * implement this interface, a warning is logged and an empty Optional is returned.
     * </p>
     *
     * @return an {@code Optional} containing the current user if authenticated and accessible, or an empty
     *         {@code Optional} if there is no authenticated user or the principal doesn't implement
     *         {@link AppUserPrincipal}
     * @see #require() For cases where authentication is required
     */
    public static Optional<AppUserInfo> get() {
        return Optional.ofNullable(getUserFromAuthentication(SecurityContextHolder.getContext().getAuthentication()));
    }

    /**
     * Extracts user information from the provided authentication object.
     *
     * @param authentication
     *            the authentication object from which to extract user information, may be {@code null}
     * @return the user information if available, or {@code null} if it cannot be extracted
     */
    private static @Nullable AppUserInfo getUserFromAuthentication(@Nullable Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof AppUserPrincipal appUserPrincipal) {
            return appUserPrincipal.getAppUser();
        }

        log.warn("Unexpected principal type: {}", principal.getClass().getName());

        return null;
    }

    /**
     * Returns the currently authenticated user from the security context.
     * <p>
     * Unlike {@link #get()}, this method throws an exception if no user is authenticated, making it suitable for
     * endpoints that require authentication.
     * </p>
     *
     * @return the currently authenticated user (never {@code null})
     * @throws AuthenticationCredentialsNotFoundException
     *             if there is no authenticated user, or the authenticated principal doesn't implement
     *             {@link AppUserPrincipal}
     */
    public static AppUserInfo require() {
        return get().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No current user"));
    }
}
