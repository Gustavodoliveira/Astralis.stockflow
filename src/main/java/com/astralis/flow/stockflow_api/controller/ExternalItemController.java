package com.astralis.flow.stockflow_api.controller;

import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemResponse;
import com.astralis.flow.stockflow_api.model.dtos.external.ExternalItemTypedResponse;
import com.astralis.flow.stockflow_api.service.ExternalItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-items")
@Tag(name = "External Items", description = "API para gerenciar itens da API externa")
public class ExternalItemController {

  private final ExternalItemService externalItemService;

  @Autowired
  public ExternalItemController(ExternalItemService externalItemService) {
    this.externalItemService = externalItemService;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar item por ID")
  public ResponseEntity<ExternalItemResponse> getItem(@PathVariable Long id) {
    try {
      ExternalItemResponse item = externalItemService.getItem(id);
      return ResponseEntity.ok(item);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}/typed")
  @Operation(summary = "Buscar item por ID com datas tipadas")
  public ResponseEntity<ExternalItemTypedResponse> getTypedItem(@PathVariable Long id) {
    try {
      ExternalItemTypedResponse item = externalItemService.getTypedItem(id);
      return ResponseEntity.ok(item);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  @Operation(summary = "Listar todos os itens")
  public ResponseEntity<List<ExternalItemResponse>> getAllItems() {
    try {
      List<ExternalItemResponse> items = externalItemService.getAllItems();
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/search")
  @Operation(summary = "Buscar itens com filtros")
  public ResponseEntity<List<ExternalItemResponse>> searchItems(
      @RequestParam(required = false) String identificacao,
      @RequestParam(required = false) Integer qtdeMinima) {
    try {
      List<ExternalItemResponse> items = externalItemService.searchItems(identificacao, qtdeMinima);
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping
  @Operation(summary = "Criar novo item")
  public ResponseEntity<ExternalItemResponse> createItem(
      @RequestParam String identificacao,
      @RequestParam String descricao,
      @RequestParam Integer qtde,
      @RequestParam(required = false) String localizacao) {
    try {
      ExternalItemResponse item = externalItemService.createItem(identificacao, descricao, qtde, localizacao);
      return ResponseEntity.ok(item);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/quantity")
  @Operation(summary = "Atualizar quantidade do item")
  public ResponseEntity<ExternalItemResponse> updateQuantity(
      @PathVariable Long id,
      @RequestParam Integer quantidade) {
    try {
      ExternalItemResponse item = externalItemService.updateQuantity(id, quantidade);
      return ResponseEntity.ok(item);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}/has-stock")
  @Operation(summary = "Verificar se item tem estoque")
  public ResponseEntity<Boolean> hasStock(@PathVariable Long id) {
    try {
      ExternalItemResponse item = externalItemService.getItem(id);
      boolean hasStock = externalItemService.hasStock(item);
      return ResponseEntity.ok(hasStock);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}/is-expired")
  @Operation(summary = "Verificar se item está vencido")
  public ResponseEntity<Boolean> isExpired(@PathVariable Long id) {
    try {
      ExternalItemTypedResponse item = externalItemService.getTypedItem(id);
      boolean isExpired = externalItemService.isExpired(item);
      return ResponseEntity.ok(isExpired);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}