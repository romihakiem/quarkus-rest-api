package com.skeleton.api.dto.request;

import com.skeleton.api.entity.ItemStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class ItemRequest {
    @NotBlank(message = "Name is required")
    public String name;

    public String description;

    public String category;

    @DecimalMin(value = "0.0", message = "Price must not be negative")
    public BigDecimal price;

    @Min(value = 0, message = "Stock must not be negative")
    public Integer stock;

    public ItemStatus status;
}
