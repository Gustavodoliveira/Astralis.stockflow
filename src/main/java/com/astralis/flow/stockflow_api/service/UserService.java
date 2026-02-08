package com.astralis.flow.stockflow_api.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.EmailAlreadyExistsException;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.mappers.UserMapper;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  private final UserMapper userMapper;

  private final BCryptPasswordEncoder passwordEncoder;

  public UserResponse createUser(CreateUserDto createUserDto) {
    var userEntity = userMapper.toEntity(createUserDto);
    if (userRepository.existsByEmail(userEntity.getEmail()) == true) {
      throw new EmailAlreadyExistsException("Email já cadastrado");
    }
    userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
    var savedUser = userRepository.save(userEntity);
    return new UserMapper().toResponse(savedUser);

  }
}
