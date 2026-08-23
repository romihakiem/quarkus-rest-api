package com.skeleton.api.util;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Thin wrapper around Quarkus' built-in BcryptUtil (quarkus-elytron-security-common)
 * so hashing/verification logic lives in one place.
 */
@ApplicationScoped
public class PasswordUtil {
    public String hash(String rawPassword) {
        return BcryptUtil.bcryptHash(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return BcryptUtil.matches(rawPassword, hashedPassword);
    }
}
