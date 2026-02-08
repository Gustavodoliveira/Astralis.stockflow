package com.astralis.flow.stockflow_api.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.EmailAlreadyExistsException;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.mappers.UserMapper;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  private final UserMapper userMapper;

  private final PasswordEncoder passwordEncoder;

  public UserResponse createUser(CreateUserDto createUserDto) {
    try {
      var userEntity = userMapper.toEntity(createUserDto);
      if (userRepository.existsByEmail(userEntity.getEmail()) == true) {
        throw new EmailAlreadyExistsException("Email já cadastrado");
      }
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
      var savedUser = userRepository.save(userEntity);
      return userMapper.toResponse(savedUser);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
    }
  }
}
