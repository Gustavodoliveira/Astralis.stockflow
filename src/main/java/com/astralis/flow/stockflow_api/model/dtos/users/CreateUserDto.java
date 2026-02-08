package com.astralis.flow.stockflow_api.model.dtos.users;

import com.astralis.flow.stockflow_api.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
    @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ter formato válido") @Size(max = 150, message = "Email deve ter no máximo 150 caracteres") String email,

    @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

    @NotBlank(message = "Nome é obrigatório") @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres") String name,

    @NotNull(message = "Papel/Função é obrigatório") Role role) {
}
