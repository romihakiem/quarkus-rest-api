package com.skeleton.api.service;

import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.dto.response.UserResponse;

public interface UserService {
    PageResponse<UserResponse> getAll(Integer page, Integer size, String sortBy, String direction);

    UserResponse getById(Long id);

    void delete(Long id);
}
