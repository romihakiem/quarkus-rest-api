package com.skeleton.api.resource;

import com.skeleton.api.dto.request.ItemRequest;
import com.skeleton.api.security.CurrentUser;
import com.skeleton.api.service.ItemService;
import com.skeleton.api.util.ResponseUtil;
import jakarta.annotation.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ItemResource {
    @Inject
    ItemService itemService;

    @Inject
    CurrentUser currentUser;

    @POST
    public Response create(@Valid ItemRequest request) {
        var response = itemService.create(request, currentUser.email());
        return ResponseUtil.created("Item created successfully", response);
    }

    @GET
    public Response getAll(@QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("direction") @DefaultValue("desc") String direction,
            @QueryParam("search") String search,
            @QueryParam("category") String category) {
        var result = itemService.getAll(page, size, sortBy, direction, search, category);
        return ResponseUtil.ok("Items fetched successfully", result);
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var result = itemService.getById(id);
        return ResponseUtil.ok("Item fetched successfully", result);
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid ItemRequest request) {
        var result = itemService.update(id, request, currentUser.email());
        return ResponseUtil.ok("Item updated successfully", result);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        itemService.delete(id, currentUser.email());
        return ResponseUtil.ok("Item deleted successfully");
    }
}
