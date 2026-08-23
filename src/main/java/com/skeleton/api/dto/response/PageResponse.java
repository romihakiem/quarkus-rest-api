package com.skeleton.api.dto.response;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;
    public int pageNumber; // 0-based
    public int pageSize;
    public long totalElements;
    public int totalPages;
    public boolean first;
    public boolean last;

    public PageResponse() {
    }

    public PageResponse(List<T> content, int pageNumber, int pageSize,
            long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
        boolean first = pageNumber == 0;
        boolean last = pageNumber >= totalPages - 1;
        return new PageResponse<>(content, pageNumber, pageSize, totalElements, totalPages, first, last);
    }
}
