package com.skeleton.api.exception;

import com.skeleton.api.dto.response.ApiResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        // Let framework-level exceptions (404 route not found, 405, etc.)
        // keep their original status/behavior instead of forcing 500.
        if (exception instanceof WebApplicationException wae) {
            return Response.status(wae.getResponse().getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(exception.getMessage()))
                    .build();
        }

        Response.Status status = switch (exception) {
            case ResourceNotFoundException e -> Response.Status.NOT_FOUND;
            case BadRequestException e -> Response.Status.BAD_REQUEST;
            case UnauthorizedException e -> Response.Status.UNAUTHORIZED;
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };

        String message = status == Response.Status.INTERNAL_SERVER_ERROR
                ? "Internal server error: " + exception.getMessage()
                : exception.getMessage();

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(message))
                .build();
    }
}
