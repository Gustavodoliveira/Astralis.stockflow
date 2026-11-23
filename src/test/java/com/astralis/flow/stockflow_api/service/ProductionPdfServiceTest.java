package com.astralis.flow.stockflow_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import com.lowagie.text.pdf.PdfReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.enums.ItemType;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.ProductionQualityCheckRepository;

@ExtendWith(MockitoExtension.class)
class ProductionPdfServiceTest {
  @Mock OrderProductionRepository orders;
  @Mock OrderProductionItemsRepository items;
  @Mock ProductionQualityCheckRepository qualityChecks;
  @InjectMocks ProductionPdfService service;

  @Test
  void generatesProductionPdfWithItems() {
    UUID id = UUID.randomUUID();
    when(orders.findById(id)).thenReturn(Optional.of(order(id)));
    when(items.findByOrderProductionIdAndItemType(id, ItemType.Input)).thenReturn(List.of(item("INSUMO TESTE")));
    when(items.findByOrderProductionIdAndItemType(id, ItemType.Output)).thenReturn(List.of(item("PRODUTO TESTE")));
    byte[] result = service.orderPdf(id);
    assertTrue(result.length > 100);
    assertEquals("%PDF", new String(result, 0, 4, StandardCharsets.US_ASCII));
  }

  @Test
  void generatesFractionationPdfEvenWithoutCompletedChecks() {
    UUID id = UUID.randomUUID();
    when(orders.findById(id)).thenReturn(Optional.of(order(id)));
    when(items.findByOrderProductionIdAndItemType(id, ItemType.Output)).thenReturn(List.of(item("PRODUTO TESTE")));
    when(qualityChecks.findByOrderId(id)).thenReturn(List.of());
    byte[] result = service.qualityPdf(id);
    assertTrue(result.length > 100);
    assertEquals("%PDF", new String(result, 0, 4, StandardCharsets.US_ASCII));
  }

  @Test
  void completePdfContainsProductionFollowedByFractionation() throws Exception {
    UUID id = UUID.randomUUID();
    when(orders.findById(id)).thenReturn(Optional.of(order(id)));
    when(items.findByOrderProductionIdAndItemType(id, ItemType.Input)).thenReturn(List.of(item("INSUMO TESTE")));
    when(items.findByOrderProductionIdAndItemType(id, ItemType.Output)).thenReturn(List.of(item("PRODUTO TESTE")));
    when(qualityChecks.findByOrderId(id)).thenReturn(List.of());

    byte[] result = service.completePdf(id);
    assertEquals("%PDF", new String(result, 0, 4, StandardCharsets.US_ASCII));
    PdfReader reader = new PdfReader(result);
    try {
      assertEquals(2, reader.getNumberOfPages());
      String first = new String(reader.getPageContent(1), StandardCharsets.ISO_8859_1);
      String second = new String(reader.getPageContent(2), StandardCharsets.ISO_8859_1);
      assertNotEquals(first, second);
    } finally {
      reader.close();
    }
  }

  private OrderProduction order(UUID id) {
    OrderProduction order = new OrderProduction();
    order.setId(id);
    order.setCreatedAt(Instant.parse("2026-11-03T12:00:00Z"));
    order.setRequisitionNumber("26110303");
    order.setDescription("PALETE A-82");
    order.setStartedOn(LocalDate.of(2026, 11, 3));
    return order;
  }

  private OrderProductionItems item(String name) {
    OrderProductionItems item = new OrderProductionItems();
    item.setId(UUID.randomUUID());
    item.setProductName(name);
    item.setExternalProductId("0000829");
    item.setUnit("KG");
    item.setQuantity(BigDecimal.TEN);
    item.setLot("031126BRI");
    item.setDateFabrication(LocalDate.of(2026,11,3));
    item.setDateValidity(LocalDate.of(2028,10,1));
    return item;
  }
}
