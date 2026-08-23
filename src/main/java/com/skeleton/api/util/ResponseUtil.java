package com.skeleton.api.util;

import com.skeleton.api.dto.response.ApiResponse;
import jakarta.ws.rs.core.Response;

/**
 * Shorthand helpers so JAX-RS resources don't repeat
 * Response.status(...).entity(ApiResponse...) boilerplate.
 */
public final class ResponseUtil {
    private ResponseUtil() {
    }

    public static <T> Response ok(String message, T data) {
        return Response.ok(ApiResponse.success(message, data)).build();
    }

    public static <T> Response ok(String message) {
        return Response.ok(ApiResponse.<T>success(message)).build();
    }

    public static <T> Response created(String message, T data) {
        return Response.status(Response.Status.CREATED).entity(ApiResponse.success(message, data)).build();
    }

    public static <T> Response error(Response.Status status, String message) {
        return Response.status(status).entity(ApiResponse.<T>error(message)).build();
    }

    public static <T> Response error(Response.Status status, String message, Object errors) {
        return Response.status(status).entity(ApiResponse.error(message, errors)).build();
    }
}
