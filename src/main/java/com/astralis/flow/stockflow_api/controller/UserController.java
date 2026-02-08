package com.astralis.flow.stockflow_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.service.UserService;

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
}
