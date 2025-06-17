package com.example.application.security.controlcenter;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.security.domain.UserId;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.NotFoundException;
import org.jspecify.annotations.Nullable;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link AppUserInfoLookup} that retrieves user information from Keycloak.
 * <p>
 * This service uses Keycloak's Admin REST API to look up user information by user ID. It requires client credentials
 * with appropriate permissions to read user information from the Keycloak realm.
 * </p>
 * <p>
 * The service can automatically parse Keycloak OIDC issuer URIs to extract the server URL and realm name, making
 * configuration easier when used with OIDC authentication.
 * </p>
 *
 * @see AppUserInfoLookup The interface this class implements
 * @see KeycloakCredentials Configuration record for Keycloak connection
 */
class KeycloakAppUserInfoLookup implements AppUserInfoLookup, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAppUserInfoLookup.class);

    private final Keycloak keycloak;
    private final KeycloakCredentials credentials;

    /**
     * Creates a new Keycloak user info lookup service with the specified credentials.
     * <p>
     * This constructor initializes a Keycloak client using the provided credentials and configures it for client
     * credentials grant type authentication. The client will be used to make authenticated requests to Keycloak's Admin
     * REST API for user lookups.
     * </p>
     * <p>
     * The credentials must include a client ID and secret that have sufficient permissions in the Keycloak realm to
     * read user information. Typically, this requires the "view-users" role in the realm management client.
     * </p>
     *
     * @param credentials
     *            the Keycloak connection credentials including server URL, realm name, client ID, and client secret
     */
    KeycloakAppUserInfoLookup(KeycloakCredentials credentials) {
        requireNonNull(credentials, "credentials must not be null");
        log.info("Looking up users from serverUrl '{}' and realm '{}'", credentials.serverUrl, credentials.realm);
        keycloak = KeycloakBuilder.builder().serverUrl(credentials.serverUrl).realm(credentials.realm)
                .grantType("client_credentials").clientId(credentials.clientId).clientSecret(credentials.clientSecret)
                .build();
        this.credentials = credentials;
    }

    @Override
    public Optional<AppUserInfo> findUserInfo(UserId userId) {
        try {
            log.debug("Looking up user info for userId: {}", userId);
            var user = keycloak.realm(credentials.realm).users().get(userId.toString()).toRepresentation();
            return Optional.of(new KeycloakAppUserInfo(user));
        } catch (NotFoundException ex) {
            log.debug("User not found in Keycloak: {}", userId);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to lookup user info for userId: {}", userId, ex);
            throw new RuntimeException("Failed to retrieve user information from Keycloak", ex);
        }
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        keycloak.close();
    }

    /**
     * Configuration record containing the connection details for Keycloak integration.
     * <p>
     * This record encapsulates all the necessary information required to establish a connection to a Keycloak server
     * and authenticate using client credentials. It provides a type-safe way to pass Keycloak configuration parameters
     * throughout the application.
     * </p>
     * <p>
     * The credentials are typically used with Keycloak's client credentials grant type for server-to-server
     * authentication, allowing the application to access Keycloak's Admin REST API for user management operations.
     * </p>
     * <p>
     * Example usage:
     * 
     * <pre>
     * {@code
     * var credentials = new KeycloakCredentials("https://keycloak.example.com", "my-realm", "my-client-id",
     *         "my-client-secret");
     *
     * // Or create from OIDC issuer URI
     * var credentials = KeycloakAppUserInfoLookup.createCredentials("https://keycloak.example.com/realms/my-realm",
     *         "my-client-id", "my-client-secret");
     * }
     * </pre>
     * </p>
     *
     * @param serverUrl
     *            the base URL of the Keycloak server (e.g., "https://keycloak.example.com"; never {@code null})
     * @param realm
     *            the name of the Keycloak realm to connect to (never {@code null})
     * @param clientId
     *            the client ID for authentication (must have appropriate permissions; never {@code null})
     * @param clientSecret
     *            the client secret for authentication (never {@code null})
     * @see KeycloakAppUserInfoLookup#createCredentials(String, String, String) Factory method for creating from OIDC
     *      issuer URI
     */
    record KeycloakCredentials(String serverUrl, String realm, String clientId, String clientSecret) {
        KeycloakCredentials {
            requireNonNull(serverUrl, "serverUrl must not be null");
            requireNonNull(realm, "realm must not be null");
            requireNonNull(clientId, "clientId must not be null");
            requireNonNull(clientSecret, "clientSecret must not be null");
        }
    }

    /**
     * Creates Keycloak credentials by parsing connection details from an OIDC issuer URI.
     * <p>
     * This utility method extracts the Keycloak server URL and realm name from a standard OIDC issuer URI, making it
     * easier to configure Keycloak integration when using OIDC authentication. The method assumes the issuer URI
     * follows Keycloak's standard format: {@code https://keycloak.example.com/realms/my-realm}
     * </p>
     * <p>
     * The parsing logic:
     * <ul>
     * <li>Extracts the server URL (everything before "/realms/")</li>
     * <li>Extracts the realm name (the path segment after "/realms/")</li>
     * <li>Combines these with the provided client credentials</li>
     * </ul>
     * </p>
     * <p>
     * Example usage:
     * 
     * <pre>
     * {@code
     * String issuerUri = "https://keycloak.example.com/realms/my-app";
     * KeycloakCredentials credentials = KeycloakAppUserInfoLookup.createCredentials(issuerUri, "my-client",
     *         "client-secret");
     * // Results in: serverUrl="https://keycloak.example.com", realm="my-app"
     * }
     * </pre>
     * </p>
     *
     * @param oidcIssuerUri
     *            the OIDC issuer URI from Keycloak (must contain "/realms/")
     * @param clientId
     *            the Keycloak client ID for authentication (never {@code null})
     * @param clientSecret
     *            the Keycloak client secret for authentication (never {@code null})
     * @return credentials configured with the parsed server URL and realm
     * @throws IllegalArgumentException
     *             if the issuer URI is not a valid Keycloak format or if the realm name is empty
     */
    static KeycloakCredentials createCredentials(String oidcIssuerUri, String clientId, String clientSecret) {
        requireNonNull(oidcIssuerUri, "oidcIssuerUri must not be null");
        requireNonNull(clientId, "clientId must not be null");
        requireNonNull(clientSecret, "clientSecret must not be null");

        var realmsIndex = oidcIssuerUri.indexOf("/realms/");
        if (realmsIndex < 0) {
            throw new IllegalArgumentException("OIDC issuer does not appear to be Keycloak: " + oidcIssuerUri);
        }

        var serverUrl = oidcIssuerUri.substring(0, realmsIndex);
        var realmStart = realmsIndex + "/realms/".length();
        var realmEnd = oidcIssuerUri.indexOf("/", realmStart);
        if (realmEnd < 0) {
            realmEnd = oidcIssuerUri.length();
        }

        var realm = oidcIssuerUri.substring(realmStart, realmEnd);
        if (realm.isEmpty()) {
            throw new IllegalArgumentException("Realm name is empty in OIDC issuer: " + oidcIssuerUri);
        }

        return new KeycloakCredentials(serverUrl, realm, clientId, clientSecret);
    }

    private static class KeycloakAppUserInfo implements AppUserInfo {

        private final UserId userId;
        private final String preferredUsername;
        private final String fullName;
        private final String email;
        private final String profileUrl;
        private final String pictureUrl;
        private final ZoneId zoneId;
        private final Locale locale;

        KeycloakAppUserInfo(UserRepresentation user) {
            userId = UserId.of(user.getId());
            preferredUsername = requireNonNull(user.getUsername());
            fullName = buildFullName(user);
            email = user.getEmail();
            profileUrl = user.firstAttribute(StandardClaimNames.PROFILE);
            pictureUrl = user.firstAttribute(StandardClaimNames.PICTURE);
            zoneId = OidcUserAdapter.parseZoneInfo(user.firstAttribute(StandardClaimNames.ZONEINFO));
            locale = OidcUserAdapter.parseLocale(user.firstAttribute(StandardClaimNames.LOCALE));
        }

        private static String buildFullName(UserRepresentation user) {
            var firstName = user.getFirstName();
            var lastName = user.getLastName();

            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            } else {
                return user.getUsername(); // Fallback to username
            }
        }

        @Override
        public UserId getUserId() {
            return userId;
        }

        @Override
        public String getPreferredUsername() {
            return preferredUsername;
        }

        @Override
        public String getFullName() {
            return fullName;
        }

        @Override
        public @Nullable String getEmail() {
            return email;
        }

        @Override
        public @Nullable String getProfileUrl() {
            return profileUrl;
        }

        @Override
        public @Nullable String getPictureUrl() {
            return pictureUrl;
        }

        @Override
        public ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }
    }
}
