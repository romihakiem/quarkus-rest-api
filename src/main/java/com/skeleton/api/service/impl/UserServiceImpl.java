package com.skeleton.api.service.impl;

import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.dto.response.UserResponse;
import com.skeleton.api.entity.User;
import com.skeleton.api.exception.ResourceNotFoundException;
import com.skeleton.api.repository.UserRepository;
import com.skeleton.api.service.UserService;
import com.skeleton.api.util.PaginationUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserServiceImpl implements UserService {
    @Inject
    UserRepository userRepository;

    @Override
    public PageResponse<UserResponse> getAll(Integer page, Integer size, String sortBy, String direction) {
        Page pageable = PaginationUtil.buildPage(page, size);
        Sort sort = PaginationUtil.buildSort(sortBy, direction);

        PanacheQuery<User> query = userRepository.findAll(sort).page(pageable);
        List<UserResponse> content = query.list().stream().map(UserResponse::fromEntity).toList();

        return PageResponse.of(content, pageable.index, pageable.size, query.count());
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        boolean deleted = userRepository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
}
