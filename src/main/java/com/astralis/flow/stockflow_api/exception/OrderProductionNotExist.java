package com.astralis.flow.stockflow_api.exception;

public class OrderProductionNotExist extends BussinesException {

  public OrderProductionNotExist(String orderProductionId) {
    super("Order production with id '" + orderProductionId + "' does not exist");
  }

  public OrderProductionNotExist() {
    super("Order production does not exist");
  }

}
