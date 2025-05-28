package com.example.application.security;

import com.vaadin.hilla.BrowserCallable;
import jakarta.annotation.security.PermitAll;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@BrowserCallable
@PermitAll
class CurrentUserService {

    public record UserInfo(@NonNull String userId, @NonNull String preferredUsername, @NonNull String fullName,
            @Nullable String profileUrl, @Nullable String pictureUrl, @Nullable String email, @NonNull String zoneId,
            @NonNull String locale, @NonNull Collection<String> authorities) {
    }

    public @NonNull UserInfo getUserInfo() {
        var principal = CurrentUser.requirePrincipal();
        var user = principal.getAppUser();
        var authorities = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new UserInfo(user.getUserId().toString(), user.getPreferredUsername(), user.getFullName(),
                user.getProfileUrl(), user.getPictureUrl(), user.getEmail(), user.getZoneId().toString(),
                user.getLocale().toString(), authorities);
    }
}
