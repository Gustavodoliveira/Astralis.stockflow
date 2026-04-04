package com.astralis.flow.stockflow_api.service;

import org.springframework.stereotype.Service;

import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderProductionService {

  private final OrderProductionRepository orderProductionRepository;
}
