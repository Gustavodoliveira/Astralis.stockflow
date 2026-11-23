package com.astralis.flow.stockflow_api.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import com.astralis.flow.stockflow_api.service.ProductionPdfService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.CreateOrderProductionWithItemsRequest;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionResponse;
import com.astralis.flow.stockflow_api.model.dtos.order_production.OrderProductionWithItemsResponse;
import com.astralis.flow.stockflow_api.service.OrderProductionService;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/order-production")
@AllArgsConstructor
public class OrderProductionController {

  private final OrderProductionService orderProductionService;
  private final ProductionPdfService productionPdfService;

  @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> downloadProductionPdf(@PathVariable UUID id,
      @RequestParam(defaultValue = "false") boolean inline) {
    return file(productionPdfService.orderPdf(id), "ordem-producao-" + id + ".pdf", inline);
  }

  @GetMapping(value = "/{id}/quality-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> downloadQualityPdf(@PathVariable UUID id,
      @RequestParam(defaultValue = "false") boolean inline) {
    return file(productionPdfService.qualityPdf(id), "fracionamento-" + id + ".pdf", inline);
  }

  private ResponseEntity<byte[]> file(byte[] content, String name, boolean inline) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(content.length)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            (inline ? "inline" : "attachment") + "; filename=\"" + name + "\"")
        .body(content);
  }

  @PostMapping("/create-with-items")
  public ResponseEntity<OrderProductionWithItemsResponse> createOrderProductionWithItems(
      @RequestBody @Valid CreateOrderProductionWithItemsRequest request) {
    return ResponseEntity.ok(orderProductionService.createOrderProductionWithItems(request));
  }

  @PostMapping("/create")
  public ResponseEntity<OrderProductionResponse> createOrderProduction(
      @RequestBody CreateOrderProductionRequest orderProd) {
    OrderProductionResponse response = orderProductionService.createOrderProduction(orderProd);
    return ResponseEntity.ok(response);

  }

  @GetMapping("/get/{id}")
  public ResponseEntity<OrderProductionResponse> getOrderProductionById(@PathVariable String id) {
    OrderProductionResponse response = orderProductionService.getOrderProductionById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getAll")
  public ResponseEntity<List<OrderProductionResponse>> getAllOrderProductions() {
    List<OrderProductionResponse> response = orderProductionService.getAllOrderProductions();
    return ResponseEntity.ok(response);

  }

  @PutMapping("/update/{id}")
  public ResponseEntity<OrderProductionResponse> updateOrderProduction(@PathVariable String id,
      @RequestBody CreateOrderProductionRequest orderProd) {
    OrderProductionResponse response = orderProductionService.updateOrderProduction(id, orderProd);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteOrderProductionById(@PathVariable String id) {
    orderProductionService.deleteOrderProductionById(id);
    return ResponseEntity.ok().body("Deletado com sucesso");
  }
}
