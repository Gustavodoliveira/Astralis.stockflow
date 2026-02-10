package com.astralis.flow.stockflow_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.model.dtos.production.items.CreateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.dtos.production.items.ProductionOrderItemResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.items.UpdateProductionOrderItemDto;
import com.astralis.flow.stockflow_api.model.enums.Kind;
import com.astralis.flow.stockflow_api.model.mappers.ProductionOrderItemMapper;
import com.astralis.flow.stockflow_api.repository.ProductionOrderItemRepository;
import com.astralis.flow.stockflow_api.repository.ProductionOrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductionOrderItemService {
  private final ProductionOrderItemRepository repository;
  private final ProductionOrderRepository productionOrderRepository;
  private final ProductionOrderItemMapper mapper;

  public ProductionOrderItemResponse createProductionOrderItem(CreateProductionOrderItemDto dto) {
    try {
      // Verifica se a ordem de produção existe
      var productionOrder = productionOrderRepository.findById(dto.productionOrderId())
          .orElseThrow(
              () -> new EntityNotFoundException("Ordem de produção não encontrada com ID: " + dto.productionOrderId()));

      var entity = mapper.toEntity(dto);
      entity.setProductionOrder(productionOrder);
      var savedEntity = repository.save(entity);
      return mapper.toResponse(savedEntity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao criar item da ordem de produção: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getAllProductionOrderItems() {
    try {
      var entities = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar itens da ordem de produção: " + e.getMessage());
    }
  }

  public Page<ProductionOrderItemResponse> getAllProductionOrderItemsPageable(int page, int size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
      var entitiesPage = repository.findAll(pageable);
      return entitiesPage.map(mapper::toResponse);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar itens da ordem de produção paginados: " + e.getMessage());
    }
  }

  public ProductionOrderItemResponse getProductionOrderItemById(Long id) {
    try {
      var entity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Item da ordem de produção não encontrado com ID: " + id));
      return mapper.toResponse(entity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar item da ordem de produção: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getItemsByProductionOrderId(Long productionOrderId) {
    try {
      var entities = repository.findByProductionOrderId(productionOrderId);
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar itens por ordem de produção: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getItemsByExternalProductId(String externalProductId) {
    try {
      var entities = repository.findByExternalProductId(externalProductId);
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar itens por produto externo: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getItemsByKind(Kind kind) {
    try {
      var entities = repository.findByKind(kind);
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar itens por tipo: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getItemsByLotCode(String lotCode) {
    try {
      var entities = repository.findByLotCode(lotCode);
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar itens por lote: " + e.getMessage());
    }
  }

  public List<ProductionOrderItemResponse> getItemsByProductionOrderIdAndKind(Long productionOrderId, Kind kind) {
    try {
      var entities = repository.findByProductionOrderIdAndKind(productionOrderId, kind);
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar itens por ordem de produção e tipo: " + e.getMessage());
    }
  }

  public ProductionOrderItemResponse updateProductionOrderItem(Long id, UpdateProductionOrderItemDto dto) {
    try {
      var existingEntity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Item da ordem de produção não encontrado com ID: " + id));

      var updatedEntity = mapper.updateEntity(existingEntity, dto);
      var savedEntity = repository.save(updatedEntity);
      return mapper.toResponse(savedEntity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao atualizar item da ordem de produção: " + e.getMessage());
    }
  }

  public void deleteProductionOrderItem(Long id) {
    try {
      if (!repository.existsById(id)) {
        throw new EntityNotFoundException("Item da ordem de produção não encontrado com ID: " + id);
      }
      repository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao deletar item da ordem de produção: " + e.getMessage());
    }
  }
}