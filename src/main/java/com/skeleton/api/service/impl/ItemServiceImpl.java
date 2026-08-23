package com.skeleton.api.service.impl;

import com.skeleton.api.dto.request.ItemRequest;
import com.skeleton.api.dto.response.ItemResponse;
import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.entity.Item;
import com.skeleton.api.entity.ItemStatus;
import com.skeleton.api.entity.Role;
import com.skeleton.api.entity.User;
import com.skeleton.api.exception.ResourceNotFoundException;
import com.skeleton.api.exception.UnauthorizedException;
import com.skeleton.api.repository.ItemRepository;
import com.skeleton.api.repository.UserRepository;
import com.skeleton.api.service.ItemService;
import com.skeleton.api.util.PaginationUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ItemServiceImpl implements ItemService {
    @Inject
    ItemRepository itemRepository;

    @Inject
    UserRepository userRepository;

    @Override
    @Transactional
    public ItemResponse create(ItemRequest request, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);

        Item item = new Item();
        item.name = request.name;
        item.description = request.description != null ? request.description : "";
        item.category = request.category != null ? request.category : "Umum";
        item.price = request.price != null ? request.price : BigDecimal.ZERO;
        item.stock = request.stock != null ? request.stock : 0;
        item.status = request.status != null ? request.status : ItemStatus.ACTIVE;
        item.owner = owner;

        itemRepository.persist(item);
        return ItemResponse.fromEntity(item);
    }

    @Override
    public PageResponse<ItemResponse> getAll(Integer page, Integer size, String sortBy, String direction,
            String search, String category) {
        Page pageable = PaginationUtil.buildPage(page, size);
        Sort sort = PaginationUtil.buildSort(sortBy, direction);

        PanacheQuery<Item> query = itemRepository.search(search, category, sort).page(pageable);
        List<ItemResponse> content = query.list().stream().map(ItemResponse::fromEntity).toList();

        return PageResponse.of(content, pageable.index, pageable.size, query.count());
    }

    @Override
    public ItemResponse getById(Long id) {
        return ItemResponse.fromEntity(getItemOrThrow(id));
    }

    @Override
    @Transactional
    public ItemResponse update(Long id, ItemRequest request, String requesterEmail) {
        Item item = getItemOrThrow(id);
        assertOwnerOrAdmin(item, requesterEmail);

        if (request.name != null)
            item.name = request.name;
        if (request.description != null)
            item.description = request.description;
        if (request.category != null)
            item.category = request.category;
        if (request.price != null)
            item.price = request.price;
        if (request.stock != null)
            item.stock = request.stock;
        if (request.status != null)
            item.status = request.status;

        return ItemResponse.fromEntity(item);
    }

    @Override
    @Transactional
    public void delete(Long id, String requesterEmail) {
        Item item = getItemOrThrow(id);
        assertOwnerOrAdmin(item, requesterEmail);
        itemRepository.delete(item);
    }

    // ---------- helpers ----------

    private Item getItemOrThrow(Long id) {
        return itemRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertOwnerOrAdmin(Item item, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        boolean isOwner = item.owner.id.equals(requester.id);
        boolean isAdmin = requester.role == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to modify this item");
        }
    }
}
