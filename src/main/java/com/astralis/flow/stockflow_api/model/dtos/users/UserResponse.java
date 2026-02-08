package com.astralis.flow.stockflow_api.model.dtos.users;

import java.time.Instant;
import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.Role;

public record UserResponse(
    UUID id,
    String name,
    String email,
    Role role,
    Boolean enabled,
    Instant createdAt,
    Instant updatedAt) {
}
