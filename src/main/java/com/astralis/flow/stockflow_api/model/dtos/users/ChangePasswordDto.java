package com.astralis.flow.stockflow_api.model.dtos.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
    @NotBlank(message = "Senha atual é obrigatória") String currentPassword,

    @NotBlank(message = "Nova senha é obrigatória") @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres") String newPassword,

    @NotBlank(message = "Confirmação da nova senha é obrigatória") String confirmNewPassword) {

  /**
   * Valida se a nova senha e confirmação são iguais
   */
  public boolean isPasswordConfirmationValid() {
    return newPassword != null && newPassword.equals(confirmNewPassword);
  }
}