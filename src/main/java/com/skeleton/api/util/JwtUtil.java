package com.skeleton.api.util;

import com.skeleton.api.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

/**
 * Issues signed JWTs using SmallRye JWT Build (backed by the private key
 * configured in `smallrye.jwt.sign.key.location`). Verification of incoming
 * tokens is handled declaratively by quarkus-smallrye-jwt via
 * `@RolesAllowed` / injected `JsonWebToken` - no manual parsing needed.
 */
@ApplicationScoped
public class JwtUtil {
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "smallrye.jwt.new-token.lifespan", defaultValue = "86400")
    long lifespanSeconds;

    public String generateToken(User user) {
        return Jwt.issuer(issuer)
                .subject(user.email)
                .groups(Set.of(user.role.name()))
                .claim("uid", user.id)
                .claim("name", user.name)
                .expiresIn(Duration.ofSeconds(lifespanSeconds))
                .sign();
    }

    public long getLifespanSeconds() {
        return lifespanSeconds;
    }
}
