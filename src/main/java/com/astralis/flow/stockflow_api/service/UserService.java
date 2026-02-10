package com.astralis.flow.stockflow_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.BussinesException;
import com.astralis.flow.stockflow_api.exception.EmailAlreadyExistsException;
import com.astralis.flow.stockflow_api.model.dtos.users.ChangePasswordDto;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UpdateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.dtos.users.UserSummaryResponse;
import com.astralis.flow.stockflow_api.model.mappers.UserMapper;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

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

  public List<UserSummaryResponse> getAllUsers() {
    try {
      var users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
      return userMapper.toSummaryResponseList(users);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar usuários: " + e.getMessage());
    }
  }

  public Page<UserSummaryResponse> getAllUsersPageable(int page, int size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
      var usersPage = userRepository.findAll(pageable);
      return usersPage.map(userMapper::toSummaryResponse);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar usuários paginados: " + e.getMessage());
    }
  }

  public UserResponse getUserById(UUID id) {
    try {
      var user = userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
      return userMapper.toResponse(user);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage());
    }
  }

  public UserResponse updateUser(UUID id, UpdateUserDto updateUserDto) {
    try {
      var existingUser = userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

      // Verifica se o email está sendo alterado e se já existe
      if (updateUserDto.email() != null && !updateUserDto.email().equals(existingUser.getEmail())) {
        if (userRepository.existsByEmail(updateUserDto.email())) {
          throw new EmailAlreadyExistsException("Email já cadastrado");
        }
      }

      var updatedUser = userMapper.updateEntity(existingUser, updateUserDto);
      var savedUser = userRepository.save(updatedUser);
      return userMapper.toResponse(savedUser);
    } catch (EntityNotFoundException | EmailAlreadyExistsException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
    }
  }

  public void changePassword(UUID id, ChangePasswordDto changePasswordDto) {
    try {
      var user = userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

      if (!changePasswordDto.isPasswordConfirmationValid()) {
        throw new BussinesException("Nova senha e confirmação não coincidem");
      }

      if (!passwordEncoder.matches(changePasswordDto.currentPassword(), user.getPassword())) {
        throw new BussinesException("Senha atual incorreta");
      }

      var encodedNewPassword = passwordEncoder.encode(changePasswordDto.newPassword());
      var updatedUser = userMapper.withPassword(user, encodedNewPassword);
      userRepository.save(updatedUser);
    } catch (EntityNotFoundException | BussinesException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao alterar senha: " + e.getMessage());
    }
  }

  public void deleteUser(UUID id) {
    try {
      var user = userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

      // Soft delete - desabilita o usuário ao invés de deletar
      var disabledUser = userMapper.withEnabledStatus(user, false);
      userRepository.save(disabledUser);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao deletar usuário: " + e.getMessage());
    }
  }

  public void enableUser(UUID id) {
    try {
      var user = userRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

      var enabledUser = userMapper.withEnabledStatus(user, true);
      userRepository.save(enabledUser);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao habilitar usuário: " + e.getMessage());
    }
  }

  public UserResponse findByEmail(String email) {
    try {
      var user = userRepository.findByEmail(email)
          .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com email: " + email));
      return userMapper.toResponse(user);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar usuário por email: " + e.getMessage());
    }
  }
}
