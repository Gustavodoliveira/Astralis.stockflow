package com.astralis.flow.stockflow_api.model.mappers;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UpdateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.dtos.users.UserSummaryResponse;
import com.astralis.flow.stockflow_api.model.entities.User;

@Component
public class UserMapper {

  /**
   * Converte CreateUserDto para User Entity
   */
  public User toEntity(CreateUserDto createUserDto) {
    if (createUserDto == null) {
      return null;
    }

    return new User(
        null, // id será gerado pelo banco
        createUserDto.email(),
        createUserDto.password(), // será criptografada no service
        createUserDto.name(),
        createUserDto.role(),
        true, // enabled padrão true
        Instant.now(), // createdAt
        null // updatedAt será null na criação
    );
  }

  /**
   * Converte User Entity para UserResponse
   */
  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    return new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getEnabled(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  /**
   * Converte lista de User Entity para lista de UserResponse
   */
  public List<UserResponse> toResponseList(List<User> users) {
    if (users == null) {
      return null;
    }

    return users.stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * Converte User Entity para UserSummaryResponse
   */
  public UserSummaryResponse toSummaryResponse(User user) {
    if (user == null) {
      return null;
    }

    return new UserSummaryResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getEnabled());
  }

  /**
   * Converte lista de User Entity para lista de UserSummaryResponse
   */
  public List<UserSummaryResponse> toSummaryResponseList(List<User> users) {
    if (users == null) {
      return null;
    }

    return users.stream()
        .map(this::toSummaryResponse)
        .toList();
  }

  /**
   * Atualiza User Entity com dados do UpdateUserDto
   * Mantém campos não fornecidos com valores originais
   */
  public User updateEntity(User existingUser, UpdateUserDto updateUserDto) {
    if (existingUser == null || updateUserDto == null) {
      return existingUser;
    }

    return new User(
        existingUser.getId(),
        updateUserDto.email() != null ? updateUserDto.email() : existingUser.getEmail(),
        existingUser.getPassword(), // senha não é atualizada por este DTO
        updateUserDto.name() != null ? updateUserDto.name() : existingUser.getName(),
        updateUserDto.role() != null ? updateUserDto.role() : existingUser.getRole(),
        updateUserDto.enabled() != null ? updateUserDto.enabled() : existingUser.getEnabled(),
        existingUser.getCreatedAt(), // createdAt não muda
        Instant.now() // updatedAt é sempre atualizado
    );
  }

  /**
   * Cria uma cópia do User com senha atualizada
   * Útil para operações de alteração de senha
   */
  public User withPassword(User user, String newPassword) {
    if (user == null) {
      return null;
    }

    return new User(
        user.getId(),
        user.getEmail(),
        newPassword,
        user.getName(),
        user.getRole(),
        user.getEnabled(),
        user.getCreatedAt(),
        Instant.now() // updatedAt é atualizado
    );
  }

  /**
   * Cria uma cópia do User com status enabled alterado
   */
  public User withEnabledStatus(User user, Boolean enabled) {
    if (user == null) {
      return null;
    }

    return new User(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.getName(),
        user.getRole(),
        enabled,
        user.getCreatedAt(),
        Instant.now() // updatedAt é atualizado
    );
  }
}