package com.astralis.flow.stockflow_api.exception;

public class UserNotExistException extends BussinesException {

  public UserNotExistException(String userId) {
    super("User with id '" + userId + "' does not exist");
  }

  public UserNotExistException() {
    super("User does not exist");
  }

}
