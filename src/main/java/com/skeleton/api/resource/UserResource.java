package com.skeleton.api.resource;

import com.skeleton.api.service.UserService;
import com.skeleton.api.util.ResponseUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Admin-only endpoints for managing users. Enforced declaratively via
 * @RolesAllowed, backed by the "groups" claim set at login time (JwtUtil).
 */
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class UserResource {
    @Inject
    UserService userService;

    @GET
    public Response getAll(@QueryParam("page") Integer page,
                           @QueryParam("size") Integer size,
                           @QueryParam("sortBy") String sortBy,
                           @QueryParam("direction") @DefaultValue("desc") String direction) {
        var result = userService.getAll(page, size, sortBy, direction);
        return ResponseUtil.ok("Users fetched successfully", result);
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var result = userService.getById(id);
        return ResponseUtil.ok("User fetched successfully", result);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        userService.delete(id);
        return ResponseUtil.ok("User deleted successfully");
    }
}
