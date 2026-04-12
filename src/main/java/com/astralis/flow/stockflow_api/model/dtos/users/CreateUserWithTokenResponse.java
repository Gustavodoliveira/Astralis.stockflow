package com.astralis.flow.stockflow_api.model.dtos.users;

public record CreateUserWithTokenResponse(
    UserResponse user,
    String token) {
}
