package com.skeleton.api.repository;

import com.skeleton.api.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public Optional<User> findByEmail(String email) {
        return find("email", email.toLowerCase().trim()).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email.toLowerCase().trim()) > 0;
    }
}
