package com.astralis.flow.stockflow_api.model.enums;

/**
 * Enum que representa os papéis/funções dos usuários no sistema
 */
public enum Role {
  PICKER("Separador", "Responsável por separar produtos nos pedidos"),
  SUPERVISOR("Supervisor", "Supervisiona as operações e equipes"),
  PRODUCTION("Produção", "Responsável pela produção e manufatura"),
  ADMIN("Administrador", "Acesso total ao sistema");

  private final String displayName;
  private final String description;

  Role(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Verifica se o role tem permissões administrativas
   */
  public boolean isAdmin() {
    return this == ADMIN;
  }

  /**
   * Verifica se o role tem permissões de supervisão
   */
  public boolean canSupervise() {
    return this == ADMIN || this == SUPERVISOR;
  }
}
