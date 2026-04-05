package com.astralis.flow.stockflow_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.exception.UserNotExistException;
import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;
import com.astralis.flow.stockflow_api.model.enums.Role;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionMapper;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderProductionServiceTest {

  @Mock
  private OrderProductionRepository orderProductionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private OrderProductionMapper orderProductionMapper;

  @InjectMocks
  private OrderProductionService orderProductionService;

  private UUID orderId;
  private UUID userId;
  private User user;
  private OrderProduction orderProduction;
  private CreateOrderProductionRequest request;
  private OrderProductionResponse response;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    userId = UUID.randomUUID();
    user = new User(userId, "user@email.com", "pass", "User Name", Role.PRODUCTION, true, null, null);
    orderProduction = new OrderProduction(orderId, user, ProductionStatus.PENDING, Instant.now(), null);
    request = new CreateOrderProductionRequest(userId, ProductionStatus.PENDING);
    response = new OrderProductionResponse(orderId, userId, "User Name", ProductionStatus.PENDING, Instant.now(), null);
  }

  // --- createOrderProduction ---

  @Test
  void createOrderProduction_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(orderProductionMapper.toEntity(request, user)).thenReturn(orderProduction);
    when(orderProductionRepository.save(orderProduction)).thenReturn(orderProduction);
    when(orderProductionMapper.toResponse(orderProduction)).thenReturn(response);

    OrderProductionResponse result = orderProductionService.createOrderProduction(request);

    assertThat(result).isNotNull();
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.productionStatus()).isEqualTo(ProductionStatus.PENDING);
    verify(orderProductionRepository).save(orderProduction);
  }

  @Test
  void createOrderProduction_UserNotFound_ThrowsUserNotExistException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionService.createOrderProduction(request))
        .isInstanceOf(UserNotExistException.class)
        .hasMessageContaining(userId.toString());
  }

  // --- getAllOrderProductions ---

  @Test
  void getAllOrderProductions_ReturnsList() {
    when(orderProductionRepository.findAll()).thenReturn(List.of(orderProduction));
    when(orderProductionMapper.toResponse(orderProduction)).thenReturn(response);

    List<OrderProductionResponse> result = orderProductionService.getAllOrderProductions();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).id()).isEqualTo(orderId);
  }

  @Test
  void getAllOrderProductions_EmptyList_ReturnsEmpty() {
    when(orderProductionRepository.findAll()).thenReturn(List.of());

    List<OrderProductionResponse> result = orderProductionService.getAllOrderProductions();

    assertThat(result).isEmpty();
  }

  // --- getOrderProductionById ---

  @Test
  void getOrderProductionById_Success() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.of(orderProduction));
    when(orderProductionMapper.toResponse(orderProduction)).thenReturn(response);

    OrderProductionResponse result = orderProductionService.getOrderProductionById(orderId.toString());

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(orderId);
  }

  @Test
  void getOrderProductionById_NotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionService.getOrderProductionById(orderId.toString()))
        .isInstanceOf(OrderProductionNotExist.class)
        .hasMessageContaining(orderId.toString());
  }

  // --- deleteOrderProductionById ---

  @Test
  void deleteOrderProductionById_Success() {
    when(orderProductionRepository.existsById(orderId)).thenReturn(true);

    orderProductionService.deleteOrderProductionById(orderId.toString());

    verify(orderProductionRepository).deleteById(orderId);
  }

  @Test
  void deleteOrderProductionById_NotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionRepository.existsById(orderId)).thenReturn(false);

    assertThatThrownBy(() -> orderProductionService.deleteOrderProductionById(orderId.toString()))
        .isInstanceOf(OrderProductionNotExist.class)
        .hasMessageContaining(orderId.toString());
  }

  // --- updateOrderProduction ---

  @Test
  void updateOrderProduction_Success() {
    CreateOrderProductionRequest updateRequest = new CreateOrderProductionRequest(userId, ProductionStatus.IN_PROGRESS);
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.of(orderProduction));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(orderProductionRepository.save(any(OrderProduction.class))).thenReturn(orderProduction);
    when(orderProductionMapper.toResponse(orderProduction)).thenReturn(response);

    OrderProductionResponse result = orderProductionService.updateOrderProduction(orderId.toString(), updateRequest);

    assertThat(result).isNotNull();
    verify(orderProductionRepository).save(orderProduction);
  }

  @Test
  void updateOrderProduction_OrderNotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionService.updateOrderProduction(orderId.toString(), request))
        .isInstanceOf(OrderProductionNotExist.class);
  }

  @Test
  void updateOrderProduction_UserNotFound_ThrowsUserNotExistException() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.of(orderProduction));
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionService.updateOrderProduction(orderId.toString(), request))
        .isInstanceOf(UserNotExistException.class);
  }
}
