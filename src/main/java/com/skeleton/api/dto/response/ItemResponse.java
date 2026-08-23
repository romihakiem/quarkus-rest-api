package com.skeleton.api.dto.response;

import com.skeleton.api.entity.Item;
import com.skeleton.api.entity.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemResponse {
    public Long id;
    public String name;
    public String description;
    public String category;
    public BigDecimal price;
    public Integer stock;
    public ItemStatus status;
    public Long ownerId;
    public String ownerName;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static ItemResponse fromEntity(Item item) {
        ItemResponse res = new ItemResponse();
        res.id = item.id;
        res.name = item.name;
        res.description = item.description;
        res.category = item.category;
        res.price = item.price;
        res.stock = item.stock;
        res.status = item.status;
        res.ownerId = item.owner != null ? item.owner.id : null;
        res.ownerName = item.owner != null ? item.owner.name : null;
        res.createdAt = item.createdAt;
        res.updatedAt = item.updatedAt;
        return res;
    }
}
