package com.astralis.flow.stockflow_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astralis.flow.stockflow_api.model.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);
}
