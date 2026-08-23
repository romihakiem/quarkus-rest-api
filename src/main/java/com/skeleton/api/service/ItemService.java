package com.skeleton.api.service;

import com.skeleton.api.dto.request.ItemRequest;
import com.skeleton.api.dto.response.ItemResponse;
import com.skeleton.api.dto.response.PageResponse;

public interface ItemService {
    ItemResponse create(ItemRequest request, String ownerEmail);

    PageResponse<ItemResponse> getAll(Integer page, Integer size, String sortBy, String direction,
            String search, String category);

    ItemResponse getById(Long id);

    ItemResponse update(Long id, ItemRequest request, String requesterEmail);

    void delete(Long id, String requesterEmail);
}
