package com.gop.lfg.security;

import com.gop.lfg.data.models.Token;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by VMPX4526 on 11/06/2015.
 */
@Slf4j
@Data
public class CustomAuthentication implements Authentication {
    private String accessToken;
    private String userId;
    private Set<GrantedAuthority> roles = new HashSet<>();

    public CustomAuthentication(final Token token) {
        accessToken = token.getAccessToken();
        userId = token.getUserId();
        token.getRoles().forEach(role -> roles.add(new SimpleGrantedAuthority(role)));
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return userId != null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return "";
    }
}
