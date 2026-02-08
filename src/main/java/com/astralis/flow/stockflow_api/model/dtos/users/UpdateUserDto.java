package com.astralis.flow.stockflow_api.model.dtos.users;

import com.astralis.flow.stockflow_api.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
    @Email(message = "Email deve ter formato válido") @Size(max = 150, message = "Email deve ter no máximo 150 caracteres") String email,

    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String name,

    Role role,

    Boolean enabled) {
}