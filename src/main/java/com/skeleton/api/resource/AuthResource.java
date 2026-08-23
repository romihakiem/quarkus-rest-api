package com.skeleton.api.resource;

import com.skeleton.api.dto.request.LoginRequest;
import com.skeleton.api.dto.request.RegisterRequest;
import com.skeleton.api.security.CurrentUser;
import com.skeleton.api.service.AuthService;
import com.skeleton.api.util.ResponseUtil;
import jakarta.annotation.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    AuthService authService;

    @Inject
    CurrentUser currentUser;

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request) {
        var response = authService.register(request);
        return ResponseUtil.created("User registered successfully", response);
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        var response = authService.login(request);
        return ResponseUtil.ok("Login successful", response);
    }

    @GET
    @Path("/me")
    @Authenticated
    public Response me() {
        var response = authService.me(currentUser.email());
        return ResponseUtil.ok("Current user fetched successfully", response);
    }
}
