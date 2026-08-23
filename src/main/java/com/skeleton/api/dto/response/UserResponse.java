package com.skeleton.api.dto.response;

import com.skeleton.api.entity.Role;
import com.skeleton.api.entity.User;

import java.time.LocalDateTime;

public class UserResponse {
    public Long id;
    public String name;
    public String email;
    public Role role;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static UserResponse fromEntity(User user) {
        UserResponse res = new UserResponse();
        res.id = user.id;
        res.name = user.name;
        res.email = user.email;
        res.role = user.role;
        res.createdAt = user.createdAt;
        res.updatedAt = user.updatedAt;
        return res;
    }
}
