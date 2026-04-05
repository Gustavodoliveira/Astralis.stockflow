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

import java.time.Instant;
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
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.enums.ProductionStatus;
import com.astralis.flow.stockflow_api.service.OrderProductionService;

@WebMvcTest(OrderProductionController.class)
@Import(FilterSecurity.class)
class OrderProductionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderProductionService orderProductionService;

  private UUID orderId;
  private UUID userId;
  private OrderProductionResponse response;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    userId = UUID.randomUUID();
    response = new OrderProductionResponse(orderId, userId, "User Name", ProductionStatus.PENDING, Instant.now(), null);
  }

  // --- POST /order-production/create ---

  @Test
  void createOrderProduction_Success_Returns200() throws Exception {
    when(orderProductionService.createOrderProduction(any())).thenReturn(response);

    String body = """
        {
          "userId": "%s",
          "productionStatus": "PENDING"
        }
        """.formatted(userId);

    mockMvc.perform(post("/order-production/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productionStatus").value("PENDING"));
  }

  // --- GET /order-production/getAll ---

  @Test
  void getAllOrderProductions_Returns200() throws Exception {
    when(orderProductionService.getAllOrderProductions()).thenReturn(List.of(response));

    mockMvc.perform(get("/order-production/getAll"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(orderId.toString()));
  }

  // --- GET /order-production/get/{id} ---

  @Test
  void getOrderProductionById_Found_Returns200() throws Exception {
    when(orderProductionService.getOrderProductionById(orderId.toString())).thenReturn(response);

    mockMvc.perform(get("/order-production/get/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId.toString()));
  }

  @Test
  void getOrderProductionById_NotFound_ThrowsException() throws Exception {
    when(orderProductionService.getOrderProductionById(orderId.toString()))
        .thenThrow(new OrderProductionNotExist(orderId.toString()));

    // GlobalHandlerException trata BussinesException com 400
    mockMvc.perform(get("/order-production/get/{id}", orderId))
        .andExpect(status().isBadRequest());
  }

  // --- PUT /order-production/update/{id} ---

  @Test
  void updateOrderProduction_Success_Returns200() throws Exception {
    when(orderProductionService.updateOrderProduction(eq(orderId.toString()), any())).thenReturn(response);

    String body = """
        {
          "userId": "%s",
          "productionStatus": "IN_PROGRESS"
        }
        """.formatted(userId);

    mockMvc.perform(put("/order-production/update/{id}", orderId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId.toString()));
  }

  // --- DELETE /order-production/delete/{id} ---

  @Test
  void deleteOrderProduction_Success_Returns200() throws Exception {
    mockMvc.perform(delete("/order-production/delete/{id}", orderId))
        .andExpect(status().isOk());
  }

  @Test
  void deleteOrderProduction_NotFound_ThrowsException() throws Exception {
    doThrow(new OrderProductionNotExist(orderId.toString()))
        .when(orderProductionService).deleteOrderProductionById(orderId.toString());

    // GlobalHandlerException trata BussinesException com 400
    mockMvc.perform(delete("/order-production/delete/{id}", orderId))
        .andExpect(status().isBadRequest());
  }
}
