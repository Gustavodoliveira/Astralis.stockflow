package com.astralis.flow.stockflow_api.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, String> validationErrors;

  // Construtor privado para uso do Builder
  private ErrorResponse(Builder builder) {
    this.timestamp = builder.timestamp;
    this.status = builder.status;
    this.error = builder.error;
    this.message = builder.message;
    this.path = builder.path;
    this.validationErrors = builder.validationErrors;
  }

  // Getters
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public Map<String, String> getValidationErrors() {
    return validationErrors;
  }

  // Método estático para criar o Builder
  public static Builder builder() {
    return new Builder();
  }

  // Classe Builder
  public static class Builder {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    public Builder timestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder status(int status) {
      this.status = status;
      return this;
    }

    public Builder error(String error) {
      this.error = error;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder validationErrors(Map<String, String> validationErrors) {
      this.validationErrors = validationErrors;
      return this;
    }

    public ErrorResponse build() {
      return new ErrorResponse(this);
    }
  }
}
