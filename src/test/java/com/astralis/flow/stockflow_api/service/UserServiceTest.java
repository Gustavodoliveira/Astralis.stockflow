package com.astralis.flow.stockflow_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.astralis.flow.stockflow_api.exception.BussinesException;
import com.astralis.flow.stockflow_api.exception.EmailAlreadyExistsException;
import com.astralis.flow.stockflow_api.model.dtos.users.ChangePasswordDto;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UpdateUserDto;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.dtos.users.UserSummaryResponse;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.enums.Role;
import com.astralis.flow.stockflow_api.model.mappers.UserMapper;
import com.astralis.flow.stockflow_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User user;
  private UUID userId;
  private CreateUserDto createUserDto;
  private UserResponse userResponse;
  private UserSummaryResponse userSummaryResponse;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User(userId, "test@email.com", "encodedPass", "Test User", Role.PICKER, true, null, null);
    createUserDto = new CreateUserDto("test@email.com", "password123", "Test User", Role.PICKER);
    userResponse = new UserResponse(userId, "Test User", "test@email.com", Role.PICKER, true, null, null);
    userSummaryResponse = new UserSummaryResponse(userId, "Test User", "test@email.com", Role.PICKER, true);
  }

  // --- createUser ---

  @Test
  void createUser_Success() {
    when(userMapper.toEntity(createUserDto)).thenReturn(user);
    when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPass");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    UserResponse result = userService.createUser(createUserDto);

    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo("test@email.com");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUser_EmailAlreadyExists_ThrowsRuntimeException() {
    // EmailAlreadyExistsException é capturada internamente e relançada como
    // RuntimeException
    when(userMapper.toEntity(createUserDto)).thenReturn(user);
    when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(createUserDto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Email já cadastrado");
  }

  // --- getAllUsers ---

  @Test
  void getAllUsers_ReturnsSortedList() {
    when(userRepository.findAll(any(Sort.class))).thenReturn(List.of(user));
    when(userMapper.toSummaryResponseList(any())).thenReturn(List.of(userSummaryResponse));

    List<UserSummaryResponse> result = userService.getAllUsers();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).email()).isEqualTo("test@email.com");
  }

  // --- getAllUsersPageable ---

  @Test
  void getAllUsersPageable_ReturnsPage() {
    Page<User> userPage = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
    when(userMapper.toSummaryResponse(user)).thenReturn(userSummaryResponse);

    Page<UserSummaryResponse> result = userService.getAllUsersPageable(0, 10);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
  }

  // --- getUserById ---

  @Test
  void getUserById_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    UserResponse result = userService.getUserById(userId);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
  }

  @Test
  void getUserById_NotFound_ThrowsEntityNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(userId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(userId.toString());
  }

  // --- updateUser ---

  @Test
  void updateUser_Success() {
    UpdateUserDto updateDto = new UpdateUserDto("updated@email.com", "Updated Name", Role.ADMIN, true);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("updated@email.com")).thenReturn(false);
    when(userMapper.updateEntity(user, updateDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    UserResponse result = userService.updateUser(userId, updateDto);

    assertThat(result).isNotNull();
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_NotFound_ThrowsEntityNotFoundException() {
    UpdateUserDto updateDto = new UpdateUserDto("updated@email.com", "Name", Role.ADMIN, true);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.updateUser(userId, updateDto))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void updateUser_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
    UpdateUserDto updateDto = new UpdateUserDto("other@email.com", "Name", Role.ADMIN, true);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("other@email.com")).thenReturn(true);

    assertThatThrownBy(() -> userService.updateUser(userId, updateDto))
        .isInstanceOf(EmailAlreadyExistsException.class);
  }

  // --- changePassword ---

  @Test
  void changePassword_Success() {
    ChangePasswordDto dto = new ChangePasswordDto("currentPass", "newPass123", "newPass123");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("currentPass", user.getPassword())).thenReturn(true);
    when(passwordEncoder.encode("newPass123")).thenReturn("encodedNewPass");
    when(userMapper.withPassword(any(), any())).thenReturn(user);

    userService.changePassword(userId, dto);

    verify(userRepository).save(user);
  }

  @Test
  void changePassword_PasswordMismatch_ThrowsBussinesException() {
    // isPasswordConfirmationValid() retorna false → BussinesException é lançada e
    // relançada
    ChangePasswordDto dto = new ChangePasswordDto("currentPass", "newPass123", "differentPass");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.changePassword(userId, dto))
        .isInstanceOf(BussinesException.class)
        .hasMessageContaining("Nova senha e confirmação não coincidem");
  }

  @Test
  void changePassword_WrongCurrentPassword_ThrowsBussinesException() {
    ChangePasswordDto dto = new ChangePasswordDto("wrongPass", "newPass123", "newPass123");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongPass", user.getPassword())).thenReturn(false);

    assertThatThrownBy(() -> userService.changePassword(userId, dto))
        .isInstanceOf(BussinesException.class)
        .hasMessageContaining("Senha atual incorreta");
  }

  // --- deleteUser ---

  @Test
  void deleteUser_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.withEnabledStatus(user, false)).thenReturn(user);

    userService.deleteUser(userId);

    verify(userMapper).withEnabledStatus(user, false);
    verify(userRepository).save(user);
  }

  @Test
  void deleteUser_NotFound_ThrowsEntityNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.deleteUser(userId))
        .isInstanceOf(EntityNotFoundException.class);
  }

  // --- findByEmail ---

  @Test
  void findByEmail_Success() {
    when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    UserResponse result = userService.findByEmail("test@email.com");

    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo("test@email.com");
  }

  @Test
  void findByEmail_NotFound_ThrowsEntityNotFoundException() {
    when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findByEmail("missing@email.com"))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("missing@email.com");
  }
}
