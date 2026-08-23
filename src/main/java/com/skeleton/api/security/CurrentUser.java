package com.skeleton.api.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Convenience accessor around the injected JsonWebToken so resources/services
 * don't need to know claim names ("sub", "uid", "groups") directly.
 */
@RequestScoped
public class CurrentUser {
    @Inject
    JsonWebToken jwt;

    public String email() {
        return jwt.getSubject();
    }

    public Long id() {
        return jwt.getClaim("uid") != null ? Long.valueOf(jwt.getClaim("uid").toString()) : null;
    }

    public String role() {
        return jwt.getGroups() != null && !jwt.getGroups().isEmpty()
                ? jwt.getGroups().iterator().next()
                : null;
    }
}
