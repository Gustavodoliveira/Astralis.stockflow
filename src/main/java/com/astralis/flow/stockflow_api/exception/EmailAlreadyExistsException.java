package com.astralis.flow.stockflow_api.exception;

public class EmailAlreadyExistsException extends BussinesException {

  public EmailAlreadyExistsException(String email) {
    super("Email '" + email + "' já cadastrado");
  }

  public EmailAlreadyExistsException() {
    super("Este email ja esta cadastrado no sistema: ");
  }

}
