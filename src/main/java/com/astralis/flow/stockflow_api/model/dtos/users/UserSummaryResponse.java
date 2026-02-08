package com.astralis.flow.stockflow_api.model.dtos.users;

import java.util.UUID;

import com.astralis.flow.stockflow_api.model.enums.Role;

/**
 * DTO para resposta simplificada do usuário
 * Usado em listagens ou quando não se precisa de todos os detalhes
 */
public record UserSummaryResponse(
    UUID id,
    String name,
    String email,
    Role role,
    Boolean enabled) {
}