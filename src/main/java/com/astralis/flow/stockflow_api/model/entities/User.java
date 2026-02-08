package com.astralis.flow.stockflow_api.model.entities;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.astralis.flow.stockflow_api.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 120)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Role role;

  @Column(nullable = false)
  private Boolean enabled = true;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column
  private Instant updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
  }

  @Override
  public String getUsername() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
  }
}
