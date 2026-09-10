package com.ewsaly.ewsaly.security.user;

import com.ewsaly.ewsaly.enums.UserAccStatus;
import com.ewsaly.ewsaly.models.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public Long getId() {
        return user.getId();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public @NonNull String getUsername() {
        return user.getPhoneNumber();
    }

    public UserAccStatus getAccountStatus() {
        return user.getAccountStatus();
    }

    @Override
    public boolean isEnabled() {
        return user.getIsVerified();
    }
}

