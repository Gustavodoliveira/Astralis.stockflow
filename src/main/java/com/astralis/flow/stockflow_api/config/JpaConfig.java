package com.astralis.flow.stockflow_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
  // Esta configuração habilita o JPA Auditing para @CreatedDate e
  // @LastModifiedDate
}