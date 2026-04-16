package com.astralis.flow.stockflow_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.astralis.flow.stockflow_api.config.FilterSecurity;
import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.model.dtos.order_production_items.OrderProductionItemResponse;
import com.astralis.flow.stockflow_api.model.enums.ItemType;
import com.astralis.flow.stockflow_api.repository.UserRepository;
import com.astralis.flow.stockflow_api.service.JwtService;
import com.astralis.flow.stockflow_api.service.OrderProductionItemsService;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(OrderProductionItemsController.class)
@Import(FilterSecurity.class)
class OrderProductionItemsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderProductionItemsService orderProductionItemsService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UserRepository userRepository;

  private UUID itemId;
  private UUID orderId;
  private OrderProductionItemResponse itemResponse;

  @BeforeEach
  void setUp() {
    itemId = UUID.randomUUID();
    orderId = UUID.randomUUID();
    itemResponse = new OrderProductionItemResponse(
        itemId, orderId, ItemType.Input, "EXT-001", "Produto A",
        "kg", new BigDecimal("10.500"), new BigDecimal("2.300"),
        "LOTE-001", null, LocalDate.now(), LocalDate.now().plusDays(365));
  }

  // --- POST /order-production-items ---

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void createItems_Success_Returns201() throws Exception {
    when(orderProductionItemsService.createOrderProductionItems(any())).thenReturn(List.of(itemResponse));

    String body = """
        [
          {
            "orderId": "%s",
            "itemType": "Input",
            "externalProductId": "EXT-001",
            "productName": "Produto A",
            "unit": "kg",
            "quantity": 10.500,
            "unitWeight": 2.300,
            "lot": "LOTE-001",
            "dateFabrication": "%s",
            "dateValidity": "%s"
          }
        ]
        """.formatted(orderId, LocalDate.now(), LocalDate.now().plusDays(365));

    mockMvc.perform(post("/order-production-items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0].externalProductId").value("EXT-001"))
        .andExpect(jsonPath("$[0].itemType").value("Input"));
  }

  // --- GET /order-production-items/order/{orderId} ---

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void getByOrderId_Returns200() throws Exception {
    when(orderProductionItemsService.getItemsByOrderProductionId(orderId.toString()))
        .thenReturn(List.of(itemResponse));

    mockMvc.perform(get("/order-production-items/order/{orderId}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderId").value(orderId.toString()));
  }

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void getByOrderId_EmptyList_Returns200() throws Exception {
    when(orderProductionItemsService.getItemsByOrderProductionId(orderId.toString()))
        .thenReturn(List.of());

    mockMvc.perform(get("/order-production-items/order/{orderId}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  // --- GET /order-production-items/{id} ---

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void getById_Found_Returns200() throws Exception {
    when(orderProductionItemsService.getItemById(itemId.toString())).thenReturn(itemResponse);

    mockMvc.perform(get("/order-production-items/{id}", itemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemId.toString()));
  }

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void getById_NotFound_ThrowsException() throws Exception {
    when(orderProductionItemsService.getItemById(itemId.toString()))
        .thenThrow(new OrderProductionNotExist(itemId.toString()));

    // GlobalHandlerException trata BussinesException com 400
    mockMvc.perform(get("/order-production-items/{id}", itemId))
        .andExpect(status().isBadRequest());
  }

  // --- PUT /order-production-items/{id} ---

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void updateItem_Success_Returns200() throws Exception {
    when(orderProductionItemsService.updateOrderProductionItem(eq(itemId.toString()), any()))
        .thenReturn(itemResponse);

    String body = """
        {
          "orderId": "%s",
          "itemType": "Output",
          "externalProductId": "EXT-002",
          "productName": "Produto B",
          "unit": "un",
          "quantity": 5.000,
          "unitWeight": 1.000,
          "lot": "LOTE-002",
          "dateFabrication": "%s",
          "dateValidity": "%s"
        }
        """.formatted(orderId, LocalDate.now(), LocalDate.now().plusDays(180));

    mockMvc.perform(put("/order-production-items/{id}", itemId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemId.toString()));
  }

  // --- DELETE /order-production-items/{id} ---

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void deleteItem_Success_Returns204() throws Exception {
    mockMvc.perform(delete("/order-production-items/{id}", itemId))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "SUPERVISOR")
  void deleteItem_NotFound_ThrowsException() throws Exception {
    doThrow(new OrderProductionNotExist(itemId.toString()))
        .when(orderProductionItemsService).deleteItemById(itemId.toString());

    // GlobalHandlerException trata BussinesException com 400
    mockMvc.perform(delete("/order-production-items/{id}", itemId))
        .andExpect(status().isBadRequest());
  }
}
