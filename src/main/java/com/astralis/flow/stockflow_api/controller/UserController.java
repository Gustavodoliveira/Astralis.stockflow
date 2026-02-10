package com.astralis.flow.stockflow_api.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.users.ChangePasswordDto;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UpdateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserSummaryResponse;
import com.astralis.flow.stockflow_api.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @PostMapping("/create")
  public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
    logger.info("Iniciando criação de usuário com email: {}", createUserDto.email());
    logger.info("Iniciando criação de usuário com senha: {}", createUserDto.password());
    logger.info("Iniciando criação de usuário com nome: {}", createUserDto.name());
    var userResponse = userService.createUser(createUserDto);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  public ResponseEntity<Object> getAllUsers(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    logger.info("Buscando lista de usuários - page: {}, size: {}", page, size);

    if (page != null && size != null) {
      Page<UserSummaryResponse> usersPage = userService.getAllUsersPageable(page, size);
      return ResponseEntity.ok(usersPage);
    } else {
      var users = userService.getAllUsers();
      return ResponseEntity.ok(users);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
    logger.info("Buscando usuário por ID: {}", id);
    try {
      var userResponse = userService.getUserById(id);
      return ResponseEntity.ok(userResponse);
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<Object> getUserByEmail(@PathVariable String email) {
    logger.info("Buscando usuário por email: {}", email);
    try {
      var userResponse = userService.findByEmail(email);
      return ResponseEntity.ok(userResponse);
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto updateUserDto) {
    logger.info("Atualizando usuário com ID: {}", id);
    try {
      var userResponse = userService.updateUser(id, updateUserDto);
      return ResponseEntity.ok(userResponse);
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<Object> changePassword(@PathVariable UUID id,
      @Valid @RequestBody ChangePasswordDto changePasswordDto) {
    logger.info("Alterando senha do usuário com ID: {}", id);
    try {
      userService.changePassword(id, changePasswordDto);
      return ResponseEntity.ok().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
    logger.info("Deletando/desabilitando usuário com ID: {}", id);
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @PatchMapping("/{id}/enable")
  public ResponseEntity<Object> enableUser(@PathVariable UUID id) {
    logger.info("Habilitando usuário com ID: {}", id);
    try {
      userService.enableUser(id);
      return ResponseEntity.ok().build();
    } catch (EntityNotFoundException e) {
      logger.warn("Usuário não encontrado: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}
