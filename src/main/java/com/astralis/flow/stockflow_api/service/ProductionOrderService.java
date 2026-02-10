package com.astralis.flow.stockflow_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.BussinesException;
import com.astralis.flow.stockflow_api.model.dtos.production.CreateProductionOrderDto;
import com.astralis.flow.stockflow_api.model.dtos.production.ProductionOrderResponse;
import com.astralis.flow.stockflow_api.model.dtos.production.UpdateProductionOrderDto;
import com.astralis.flow.stockflow_api.model.mappers.ProductionOrderMapper;
import com.astralis.flow.stockflow_api.repository.ProductionOrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductionOrderService {
  private final ProductionOrderRepository repository;
  private final ProductionOrderMapper mapper;

  public ProductionOrderResponse createProductionOrder(CreateProductionOrderDto dto) {
    try {
      if (repository.existsByOrderNumber(dto.orderNumber())) {
        throw new BussinesException("Ordem de produção já existe com este número: " + dto.orderNumber());
      }

      var entity = mapper.toEntity(dto);
      var savedEntity = repository.save(entity);
      return mapper.toResponse(savedEntity);
    } catch (BussinesException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao criar ordem de produção: " + e.getMessage());
    }
  }

  public List<ProductionOrderResponse> getAllProductionOrders() {
    try {
      var entities = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar ordens de produção: " + e.getMessage());
    }
  }

  public Page<ProductionOrderResponse> getAllProductionOrdersPageable(int page, int size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
      var entitiesPage = repository.findAll(pageable);
      return entitiesPage.map(mapper::toResponse);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar ordens de produção paginadas: " + e.getMessage());
    }
  }

  public ProductionOrderResponse getProductionOrderById(Long id) {
    try {
      var entity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Ordem de produção não encontrada com ID: " + id));
      return mapper.toResponse(entity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar ordem de produção: " + e.getMessage());
    }
  }

  public ProductionOrderResponse getProductionOrderByOrderNumber(String orderNumber) {
    try {
      var entity = repository.findByOrderNumber(orderNumber)
          .orElseThrow(
              () -> new EntityNotFoundException("Ordem de produção não encontrada com número: " + orderNumber));
      return mapper.toResponse(entity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar ordem de produção por número: " + e.getMessage());
    }
  }

  public ProductionOrderResponse updateProductionOrder(Long id, UpdateProductionOrderDto dto) {
    try {
      var existingEntity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Ordem de produção não encontrada com ID: " + id));

      // Verifica se o orderNumber está sendo alterado e se já existe
      if (dto.orderNumber() != null && !dto.orderNumber().equals(existingEntity.getOrderNumber())) {
        if (repository.existsByOrderNumber(dto.orderNumber())) {
          throw new BussinesException("Ordem de produção já existe com este número: " + dto.orderNumber());
        }
      }

      var updatedEntity = mapper.updateEntity(existingEntity, dto);
      var savedEntity = repository.save(updatedEntity);
      return mapper.toResponse(savedEntity);
    } catch (EntityNotFoundException | BussinesException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao atualizar ordem de produção: " + e.getMessage());
    }
  }

  public void deleteProductionOrder(Long id) {
    try {
      if (!repository.existsById(id)) {
        throw new EntityNotFoundException("Ordem de produção não encontrada com ID: " + id);
      }
      repository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao deletar ordem de produção: " + e.getMessage());
    }
  }
}