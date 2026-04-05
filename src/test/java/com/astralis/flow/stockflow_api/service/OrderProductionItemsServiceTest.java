package com.astralis.flow.stockflow_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.CreateOrderProductionItemRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.entities.User;
import com.astralis.flow.stockflow_api.model.enums.ItemType;
import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;
import com.astralis.flow.stockflow_api.model.enums.Role;
import com.astralis.flow.stockflow_api.model.mappers.OrderProductionItemsMapper;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;

@ExtendWith(MockitoExtension.class)
class OrderProductionItemsServiceTest {

  @Mock
  private OrderProductionRepository orderProductionRepository;

  @Mock
  private OrderProductionItemsRepository orderProductionItemsRepository;

  @Mock
  private OrderProductionItemsMapper orderProductionItemsMapper;

  @InjectMocks
  private OrderProductionItemsService orderProductionItemsService;

  private UUID itemId;
  private UUID orderId;
  private OrderProduction orderProduction;
  private OrderProductionItems item;
  private CreateOrderProductionItemRequest dto;
  private OrderProductionItemResponse itemResponse;

  @BeforeEach
  void setUp() {
    itemId = UUID.randomUUID();
    orderId = UUID.randomUUID();
    User user = new User(UUID.randomUUID(), "u@email.com", "pass", "User", Role.PRODUCTION, true, null, null);
    orderProduction = new OrderProduction(orderId, user, ProductionStatus.PENDING, Instant.now(), null);

    dto = new CreateOrderProductionItemRequest(
        orderId, ItemType.Input, "EXT-001", "Produto A", "kg",
        new BigDecimal("10.500"), new BigDecimal("2.300"),
        "LOTE-001", LocalDate.now(), LocalDate.now().plusDays(365));

    item = new OrderProductionItems(
        itemId, orderProduction, ItemType.Input, "EXT-001", "Produto A",
        "kg", new BigDecimal("10.500"), new BigDecimal("2.300"),
        "LOTE-001", LocalDate.now(), LocalDate.now().plusDays(365));

    itemResponse = new OrderProductionItemResponse(
        itemId, orderId, ItemType.Input, "EXT-001", "Produto A",
        "kg", new BigDecimal("10.500"), new BigDecimal("2.300"),
        "LOTE-001", LocalDate.now(), LocalDate.now().plusDays(365));
  }

  // --- createOrderProductionItems ---

  @Test
  void createOrderProductionItems_Success() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.of(orderProduction));
    when(orderProductionItemsMapper.toEntity(dto, orderProduction)).thenReturn(item);
    when(orderProductionItemsRepository.save(item)).thenReturn(item);
    when(orderProductionItemsMapper.toResponse(item)).thenReturn(itemResponse);

    List<OrderProductionItemResponse> result = orderProductionItemsService.createOrderProductionItems(List.of(dto));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).externalProductId()).isEqualTo("EXT-001");
    verify(orderProductionItemsRepository).save(item);
  }

  @Test
  void createOrderProductionItems_OrderNotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionItemsService.createOrderProductionItems(List.of(dto)))
        .isInstanceOf(OrderProductionNotExist.class)
        .hasMessageContaining(orderId.toString());
  }

  // --- getItemsByOrderProductionId ---

  @Test
  void getItemsByOrderProductionId_ReturnsList() {
    when(orderProductionItemsRepository.findByOrderProductionId(orderId))
        .thenReturn(List.of(item));
    when(orderProductionItemsMapper.toResponse(item)).thenReturn(itemResponse);

    List<OrderProductionItemResponse> result = orderProductionItemsService
        .getItemsByOrderProductionId(orderId.toString());

    assertThat(result).hasSize(1);
    assertThat(result.get(0).orderId()).isEqualTo(orderId);
  }

  @Test
  void getItemsByOrderProductionId_NoItems_ReturnsEmpty() {
    when(orderProductionItemsRepository.findByOrderProductionId(orderId)).thenReturn(List.of());

    List<OrderProductionItemResponse> result = orderProductionItemsService
        .getItemsByOrderProductionId(orderId.toString());

    assertThat(result).isEmpty();
  }

  // --- getItemById ---

  @Test
  void getItemById_Success() {
    when(orderProductionItemsRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(orderProductionItemsMapper.toResponse(item)).thenReturn(itemResponse);

    OrderProductionItemResponse result = orderProductionItemsService.getItemById(itemId.toString());

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(itemId);
  }

  @Test
  void getItemById_NotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionItemsRepository.findById(itemId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionItemsService.getItemById(itemId.toString()))
        .isInstanceOf(OrderProductionNotExist.class)
        .hasMessageContaining(itemId.toString());
  }

  // --- deleteItemById ---

  @Test
  void deleteItemById_Success() {
    when(orderProductionItemsRepository.existsById(itemId)).thenReturn(true);

    orderProductionItemsService.deleteItemById(itemId.toString());

    verify(orderProductionItemsRepository).deleteById(itemId);
  }

  @Test
  void deleteItemById_NotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionItemsRepository.existsById(itemId)).thenReturn(false);

    assertThatThrownBy(() -> orderProductionItemsService.deleteItemById(itemId.toString()))
        .isInstanceOf(OrderProductionNotExist.class)
        .hasMessageContaining(itemId.toString());
  }

  // --- updateOrderProductionItem ---

  @Test
  void updateOrderProductionItem_Success() {
    when(orderProductionItemsRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.of(orderProduction));
    when(orderProductionItemsRepository.save(any(OrderProductionItems.class))).thenReturn(item);
    when(orderProductionItemsMapper.toResponse(item)).thenReturn(itemResponse);

    OrderProductionItemResponse result = orderProductionItemsService.updateOrderProductionItem(itemId.toString(), dto);

    assertThat(result).isNotNull();
    verify(orderProductionItemsRepository).save(item);
  }

  @Test
  void updateOrderProductionItem_ItemNotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionItemsRepository.findById(itemId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionItemsService.updateOrderProductionItem(itemId.toString(), dto))
        .isInstanceOf(OrderProductionNotExist.class);
  }

  @Test
  void updateOrderProductionItem_OrderNotFound_ThrowsOrderProductionNotExist() {
    when(orderProductionItemsRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(orderProductionRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderProductionItemsService.updateOrderProductionItem(itemId.toString(), dto))
        .isInstanceOf(OrderProductionNotExist.class);
  }
}
