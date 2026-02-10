package com.astralis.flow.stockflow_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.exception.BussinesException;
import com.astralis.flow.stockflow_api.model.dtos.products.CreateProductSnapshotDto;
import com.astralis.flow.stockflow_api.model.dtos.products.ProductSnapshotResponse;
import com.astralis.flow.stockflow_api.model.dtos.products.UpdateProductSnapshotDto;
import com.astralis.flow.stockflow_api.model.mappers.ProductSnapshotMapper;
import com.astralis.flow.stockflow_api.repository.ProductSnapshotRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductSnapshotService {
  private final ProductSnapshotRepository repository;
  private final ProductSnapshotMapper mapper;

  public ProductSnapshotResponse createProductSnapshot(CreateProductSnapshotDto dto) {
    try {
      if (repository.existsByExternalProductId(dto.externalProductId())) {
        throw new BussinesException("Produto já existe com este ID externo: " + dto.externalProductId());
      }

      var entity = mapper.toEntity(dto);
      var savedEntity = repository.save(entity);
      return mapper.toResponse(savedEntity);
    } catch (BussinesException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao criar produto snapshot: " + e.getMessage());
    }
  }

  public List<ProductSnapshotResponse> getAllProductSnapshots() {
    try {
      var entities = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
      return mapper.toResponseList(entities);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar produtos snapshot: " + e.getMessage());
    }
  }

  public Page<ProductSnapshotResponse> getAllProductSnapshotsPageable(int page, int size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
      var entitiesPage = repository.findAll(pageable);
      return entitiesPage.map(mapper::toResponse);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao listar produtos snapshot paginados: " + e.getMessage());
    }
  }

  public ProductSnapshotResponse getProductSnapshotById(Long id) {
    try {
      var entity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Produto snapshot não encontrado com ID: " + id));
      return mapper.toResponse(entity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar produto snapshot: " + e.getMessage());
    }
  }

  public ProductSnapshotResponse getProductSnapshotByExternalId(String externalProductId) {
    try {
      var entity = repository.findByExternalProductId(externalProductId)
          .orElseThrow(() -> new EntityNotFoundException(
              "Produto snapshot não encontrado com ID externo: " + externalProductId));
      return mapper.toResponse(entity);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao buscar produto snapshot por ID externo: " + e.getMessage());
    }
  }

  public ProductSnapshotResponse updateProductSnapshot(Long id, UpdateProductSnapshotDto dto) {
    try {
      var existingEntity = repository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Produto snapshot não encontrado com ID: " + id));

      // Verifica se o externalProductId está sendo alterado e se já existe
      if (dto.externalProductId() != null && !dto.externalProductId().equals(existingEntity.getExternalProductId())) {
        if (repository.existsByExternalProductId(dto.externalProductId())) {
          throw new BussinesException("Produto já existe com este ID externo: " + dto.externalProductId());
        }
      }

      var updatedEntity = mapper.updateEntity(existingEntity, dto);
      var savedEntity = repository.save(updatedEntity);
      return mapper.toResponse(savedEntity);
    } catch (EntityNotFoundException | BussinesException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao atualizar produto snapshot: " + e.getMessage());
    }
  }

  public void deleteProductSnapshot(Long id) {
    try {
      if (!repository.existsById(id)) {
        throw new EntityNotFoundException("Produto snapshot não encontrado com ID: " + id);
      }
      repository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao deletar produto snapshot: " + e.getMessage());
    }
  }
}