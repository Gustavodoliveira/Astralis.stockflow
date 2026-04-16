package com.astralis.flow.stockflow_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.astralis.flow.stockflow_api.config.FilterSecurity;
import com.astralis.flow.stockflow_api.model.dtos.users.CreateUserWithTokenResponse;
import com.astralis.flow.stockflow_api.model.dtos.users.UserResponse;
import com.astralis.flow.stockflow_api.model.dtos.users.UserSummaryResponse;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.enums.Role;
import com.astralis.flow.stockflow_api.repository.UserRepository;
import com.astralis.flow.stockflow_api.service.JwtService;
import com.astralis.flow.stockflow_api.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(UserController.class)
@Import(FilterSecurity.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UserRepository userRepository;

  private UUID userId;
  private User userEntity;
  private UserResponse userResponse;
  private UserSummaryResponse userSummaryResponse;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userEntity = new User(userId, "test@email.com", "encodedPass", "Test User", Role.PICKER, true, null, null);
    userResponse = new UserResponse(userId, "Test User", "test@email.com", Role.PICKER, true, null, null);
    userSummaryResponse = new UserSummaryResponse(userId, "Test User", "test@email.com", Role.PICKER, true);
  }

  // --- POST /users/create ---

  @Test
  void createUser_Success_Returns200() throws Exception {
    var tokenResponse = new CreateUserWithTokenResponse(userResponse, "jwt-token");
    when(userService.createUser(any())).thenReturn(tokenResponse);

    String body = """
        {
          "email": "test@email.com",
          "password": "senha123",
          "name": "Test User",
          "role": "PICKER"
        }
        """;

    mockMvc.perform(post("/users/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value("test@email.com"))
        .andExpect(jsonPath("$.user.name").value("Test User"))
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  // --- POST /users/login ---

  @Test
  void login_Success_Returns200() throws Exception {
    var tokenResponse = new CreateUserWithTokenResponse(userResponse, "jwt-token");
    when(userService.login(any())).thenReturn(tokenResponse);

    String body = """
        {
          "email": "test@email.com",
          "password": "senha123"
        }
        """;

    mockMvc.perform(post("/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value("test@email.com"))
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  // --- GET /users/getAll ---

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllUsers_NoParams_ReturnsList() throws Exception {
    when(userService.getAllUsers()).thenReturn(List.of(userSummaryResponse));

    mockMvc.perform(get("/users/getAll"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("test@email.com"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getAllUsers_WithPageAndSize_ReturnsPage() throws Exception {
    var page = new PageImpl<>(List.of(userSummaryResponse));
    when(userService.getAllUsersPageable(0, 10)).thenReturn(page);

    mockMvc.perform(get("/users/getAll").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].email").value("test@email.com"));
  }

  // --- GET /users/{id} ---

  @Test
  void getUserById_Found_Returns200() throws Exception {
    when(userService.getUserById(userId)).thenReturn(userResponse);

    mockMvc.perform(get("/users/{id}", userId)
        .with(user(userEntity)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()));
  }

  @Test
  void getUserById_NotFound_Returns404() throws Exception {
    when(userService.getUserById(userId)).thenThrow(new EntityNotFoundException("not found"));

    mockMvc.perform(get("/users/{id}", userId)
        .with(user(userEntity)))
        .andExpect(status().isNotFound());
  }

  // --- GET /users/email/{email} ---

  @Test
  void getUserByEmail_Found_Returns200() throws Exception {
    when(userService.findByEmail("test@email.com")).thenReturn(userResponse);

    mockMvc.perform(get("/users/email/{email}", "test@email.com")
        .with(user(userEntity)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@email.com"));
  }

  @Test
  void getUserByEmail_NotFound_Returns404() throws Exception {
    User missingUser = new User(UUID.randomUUID(), "missing@email.com", "pass", "Missing", Role.PICKER, true, null,
        null);
    when(userService.findByEmail("missing@email.com"))
        .thenThrow(new EntityNotFoundException("not found"));

    mockMvc.perform(get("/users/email/{email}", "missing@email.com")
        .with(user(missingUser)))
        .andExpect(status().isNotFound());
  }

  // --- PUT /users/update/{id} ---

  @Test
  void updateUser_Success_Returns200() throws Exception {
    when(userService.updateUser(eq(userId), any())).thenReturn(userResponse);

    String body = """
        {
          "email": "updated@email.com",
          "name": "Updated Name",
          "role": "ADMIN",
          "enabled": true
        }
        """;

    mockMvc.perform(put("/users/update/{id}", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body)
        .with(user(userEntity)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  void updateUser_NotFound_Returns404() throws Exception {
    when(userService.updateUser(eq(userId), any()))
        .thenThrow(new EntityNotFoundException("not found"));

    String body = """
        {
          "email": "updated@email.com",
          "name": "Name",
          "role": "ADMIN",
          "enabled": true
        }
        """;

    mockMvc.perform(put("/users/update/{id}", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body)
        .with(user(userEntity)))
        .andExpect(status().isNotFound());
  }

  // --- PATCH /users/{id}/password ---

  @Test
  @WithMockUser
  void changePassword_Success_Returns200() throws Exception {
    String body = """
        {
          "currentPassword": "currentPass",
          "newPassword": "newPass123",
          "confirmNewPassword": "newPass123"
        }
        """;

    mockMvc.perform(patch("/users/{id}/password", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void changePassword_NotFound_Returns404() throws Exception {
    doThrow(new EntityNotFoundException("not found"))
        .when(userService).changePassword(eq(userId), any());

    String body = """
        {
          "currentPassword": "currentPass",
          "newPassword": "newPass123",
          "confirmNewPassword": "newPass123"
        }
        """;

    mockMvc.perform(patch("/users/{id}/password", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isNotFound());
  }

  // --- DELETE /users/delete/{id} ---

  @Test
  void deleteUser_Success_Returns204() throws Exception {
    mockMvc.perform(delete("/users/delete/{id}", userId)
        .with(user(userEntity)))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteUser_NotFound_Returns404() throws Exception {
    doThrow(new EntityNotFoundException("not found"))
        .when(userService).deleteUser(userId);

    mockMvc.perform(delete("/users/delete/{id}", userId)
        .with(user(userEntity)))
        .andExpect(status().isNotFound());
  }
}
