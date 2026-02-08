package com.astralis.flow.stockflow_api.model.dtos.users;

import java.time.Instant;

import com.astralis.flow.stockflow_api.model.enums.Role;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    Boolean enabled,
    Instant createdAt,
    Instant updatedAt) {
}
