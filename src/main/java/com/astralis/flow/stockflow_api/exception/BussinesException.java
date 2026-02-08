package com.astralis.flow.stockflow_api.exception;

public class BussinesException extends RuntimeException {

  public BussinesException(String message) {
    super(message);
  }

  public BussinesException() {
    super("Erro de negócio");
  }

}
