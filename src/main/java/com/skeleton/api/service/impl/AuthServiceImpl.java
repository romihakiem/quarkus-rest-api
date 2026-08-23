package com.skeleton.api.service.impl;

import com.skeleton.api.dto.request.LoginRequest;
import com.skeleton.api.dto.request.RegisterRequest;
import com.skeleton.api.dto.response.JwtResponse;
import com.skeleton.api.dto.response.UserResponse;
import com.skeleton.api.entity.Role;
import com.skeleton.api.entity.User;
import com.skeleton.api.exception.BadRequestException;
import com.skeleton.api.exception.ResourceNotFoundException;
import com.skeleton.api.exception.UnauthorizedException;
import com.skeleton.api.repository.UserRepository;
import com.skeleton.api.service.AuthService;
import com.skeleton.api.util.JwtUtil;
import com.skeleton.api.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {
    @Inject
    UserRepository userRepository;

    @Inject
    PasswordUtil passwordUtil;

    @Inject
    JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.name = request.name;
        user.email = request.email.toLowerCase().trim();
        user.password = passwordUtil.hash(request.password);
        user.role = Role.USER;

        userRepository.persist(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordUtil.matches(request.password, user.password)) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        return new JwtResponse(token, jwtUtil.getLifespanSeconds(), UserResponse.fromEntity(user));
    }

    @Override
    public UserResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromEntity(user);
    }
}
