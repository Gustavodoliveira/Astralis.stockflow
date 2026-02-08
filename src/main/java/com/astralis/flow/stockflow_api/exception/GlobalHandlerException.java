package com.astralis.flow.stockflow_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.micrometer.core.ipc.http.HttpSender.Response;

@RestControllerAdvice
public class GlobalHandlerException {

  /**
   * Trata exceções de validação
   */
  @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      Exception ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();

    if (ex instanceof MethodArgumentNotValidException) {
      ((MethodArgumentNotValidException) ex).getBindingResult()
          .getFieldErrors()
          .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    } else if (ex instanceof BindException) {
      ((BindException) ex).getBindingResult()
          .getFieldErrors()
          .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Erro de Validação")
        .message("Campos inválidos")
        .path(request.getDescription(false).replace("uri=", ""))
        .validationErrors(errors)
        .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Trata exceções de negócio
   */
  @ExceptionHandler(BussinesException.class)
  public ResponseEntity<ErrorResponse> handleBussinesException(
      BussinesException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Erro de Negócio")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Trata exceções genéricas não capturadas
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Erro Interno")
        .message("Ocorreu um erro inesperado no servidor")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  // Trata execeções de email já cadastrado
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex, WebRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Email já cadastrado")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

  }
}
