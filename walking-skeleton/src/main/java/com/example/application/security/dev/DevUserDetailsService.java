package com.example.application.security.dev;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.security.domain.UserId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link UserDetailsService} and {@link AppUserInfoLookup} for development environments.
 * <p>
 * This class provides a simple in-memory implementation of both Spring Security's {@link UserDetailsService} for
 * authentication and the application's {@link AppUserInfoLookup} for user information retrieval. It stores a collection
 * of {@link DevUser} instances and allows looking them up by either preferred username (for authentication) or user ID
 * (for information retrieval).
 * </p>
 * <p>
 * This implementation is specifically designed for development and testing purposes and should not be used in
 * production environments. It allows the application to function with predefined test users without needing external
 * authentication services or databases.
 * </p>
 * <p>
 * Example usage: <!-- spotless:off -->
 * <pre>
 * {@code
 * DevUserDetailsService userService = new DevUserDetailsService(List.of(
 *     DevUser.builder("admin", "Admin User")
 *         .password("password")
 *         .email("admin@example.com")
 *         .roles("ADMIN")
 *         .build(),
 *     DevUser.builder("user", "Regular User")
 *         .password("password")
 *         .email("user@example.com")
 *         .roles("USER")
 *         .build()
 * ));
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 *
 * @see DevUser The development user class stored in this service
 * @see UserDetailsService Spring Security's interface for loading user authentication details
 * @see AppUserInfoLookup The application's interface for looking up user information
 */
final class DevUserDetailsService implements UserDetailsService, AppUserInfoLookup {

    private final Map<String, UserDetails> userByUsername;
    private final Map<UserId, AppUserInfo> userInfoById;

    /**
     * Creates a new service with the specified development users.
     * <p>
     * This constructor stores the provided users in memory, indexing them by both email address and user ID for
     * efficient lookups.
     * </p>
     *
     * @param users
     *            the development users to include in this service
     */
    DevUserDetailsService(Collection<DevUser> users) {
        userByUsername = new HashMap<>();
        userInfoById = new HashMap<>();
        users.forEach(user -> {
            userByUsername.put(user.getAppUser().getPreferredUsername(), user);
            userInfoById.put(user.getAppUser().getUserId(), user.getAppUser());
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userByUsername.get(username))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public Optional<AppUserInfo> findUserInfo(UserId userId) {
        return Optional.ofNullable(userInfoById.get(userId));
    }
}
