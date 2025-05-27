package com.example.application.security;

import com.example.application.security.domain.UserId;

import java.util.Optional;

/**
 * Service interface for looking up {@link AppUserInfo} details for any user in the system.
 * <p>
 * Unlike {@link CurrentUser} which provides access only to the currently authenticated user, this interface allows
 * retrieving information about any user by their ID. This capability is essential for features such as audit trails,
 * activity logs, and displaying user attribution information throughout the application.
 * </p>
 * <p>
 * Typical use cases include:
 * <ul>
 * <li>Displaying the name of a user who created or modified a record</li>
 * <li>Retrieving contact information for notification purposes</li>
 * <li>Resolving user references in data models to their full profile information</li>
 * </ul>
 * </p>
 * <p>
 * Implementations may choose to cache user information in memory for improved performance, especially for frequently
 * accessed user profiles. However, implementations should consider cache invalidation strategies to ensure that user
 * information remains reasonably up to date.
 * </p>
 *
 * @see AppUserInfo The application's user information model
 * @see CurrentUser For accessing the currently authenticated user
 */
public interface AppUserInfoLookup {

    /**
     * Finds the {@link AppUserInfo} for the specified user ID.
     * <p>
     * This method attempts to retrieve user information from the underlying user repository or identity provider. If
     * the user cannot be found (e.g., the user has been deleted or the ID is invalid), an empty Optional is returned
     * rather than throwing an exception.
     * </p>
     * <p>
     * Note that this method may involve remote calls or database lookups depending on the implementation, so callers
     * should consider potential performance implications when making multiple lookups.
     * </p>
     *
     * @param userId
     *            the user ID to look up (never {@code null})
     * @return an {@code Optional} containing the user information if found, or an empty {@code Optional} if the user
     *         does not exist or is no longer accessible
     */
    Optional<AppUserInfo> findUserInfo(UserId userId);
}
