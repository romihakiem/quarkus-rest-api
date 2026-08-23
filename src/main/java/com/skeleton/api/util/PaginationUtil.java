package com.skeleton.api.util;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

/**
 * Builds safe Panache Page/Sort objects from raw request params, guarding
 * against abusive page sizes and blank sort fields.
 */
public final class PaginationUtil {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private PaginationUtil() {
    }

    public static Page buildPage(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? DEFAULT_PAGE : page;
        int safeSize = (size == null || size <= 0) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return Page.of(safePage, safeSize);
    }

    public static Sort buildSort(String sortBy, String direction) {
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.Ascending
                : Sort.Direction.Descending;
        return Sort.by(safeSortBy, safeDirection);
    }
}
