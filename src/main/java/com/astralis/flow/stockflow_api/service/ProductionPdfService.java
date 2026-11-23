package com.astralis.flow.stockflow_api.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astralis.flow.stockflow_api.exception.OrderProductionNotExist;
import com.astralis.flow.stockflow_api.model.entities.OrderProduction;
import com.astralis.flow.stockflow_api.model.entities.OrderProductionItems;
import com.astralis.flow.stockflow_api.model.entities.ProductionQualityCheck;
import com.astralis.flow.stockflow_api.model.enums.ItemType;
import com.astralis.flow.stockflow_api.repository.OrderProductionItemsRepository;
import com.astralis.flow.stockflow_api.repository.OrderProductionRepository;
import com.astralis.flow.stockflow_api.repository.ProductionQualityCheckRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

/** Reports drawn on A4 landscape in the same column order as the supplied paper forms. */
@Service
@RequiredArgsConstructor
public class ProductionPdfService {
  private static final float W = 841.92f, H = 595.32f, LEFT = 20f, RIGHT = 822f;
  private static final float ROW = 15.0f;
  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yy");
  private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

  private final OrderProductionRepository orders;
  private final OrderProductionItemsRepository items;
  private final ProductionQualityCheckRepository qualityChecks;

  @Transactional(readOnly = true)
  public byte[] orderPdf(UUID id) {
    OrderProduction order = load(id);
    List<OrderProductionItems> inputs = items.findByOrderProductionIdAndItemType(id, ItemType.Input);
    List<OrderProductionItems> outputs = items.findByOrderProductionIdAndItemType(id, ItemType.Output);
    return pdf((writer, doc) -> drawProduction(writer, doc, order, inputs, outputs));
  }

  @Transactional(readOnly = true)
  public byte[] qualityPdf(UUID id) {
    OrderProduction order = load(id);
    List<OrderProductionItems> outputs = items.findByOrderProductionIdAndItemType(id, ItemType.Output);
    // The supplied quality sheet lists all produced products; fall back to inputs
    // for historical orders that do not have any Output lines.
    if (outputs.isEmpty())
      outputs = items.findByOrderProductionIdAndItemType(id, ItemType.Input);
    List<OrderProductionItems> qualityItems = outputs;
    Map<UUID, ProductionQualityCheck> recorded = qualityChecks.findByOrderId(id).stream()
        .collect(Collectors.toMap(q -> q.getItem().getId(), Function.identity()));
    return pdf((writer, doc) -> drawQuality(writer, doc, order, qualityItems, recorded));
  }

  private OrderProduction load(UUID id) {
    return orders.findById(id).orElseThrow(
        () -> new OrderProductionNotExist("Order production with id '" + id + "' does not exist"));
  }

  private interface Pages { void render(PdfWriter writer, Document doc) throws Exception; }

  private byte[] pdf(Pages pages) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document doc = new Document(new Rectangle(W, H), 0, 0, 0, 0);
    try {
      PdfWriter writer = PdfWriter.getInstance(doc, out);
      doc.open();
      pages.render(writer, doc);
    } catch (Exception e) {
      throw new IllegalStateException("Could not generate production report PDF", e);
    } finally {
      doc.close();
    }
    return out.toByteArray();
  }

  private void drawProduction(PdfWriter writer, Document doc, OrderProduction order,
                              List<OrderProductionItems> inputs, List<OrderProductionItems> outputs) throws Exception {
    BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    BaseFont bold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    int page = 1, in = 0, out = 0;
    boolean first = true;
    do {
      if (!first) doc.newPage();
      first = false;
      PdfContentByte cb = writer.getDirectContent();
      header(cb, font, bold, order, page, true);
      float y = 149;
      if (in < inputs.size()) {
        y = sectionTitle(cb, bold, "INSUMO", y, true);
        while (in < inputs.size() && y + ROW < 550) {
          productionRow(cb, font, inputs.get(in++), y, true);
          y += ROW;
        }
      }
      if (in == inputs.size() && out < outputs.size()) {
        y += 16;
        y = sectionTitle(cb, bold, "PRODUTO FINAL", y, false);
        while (out < outputs.size() && y + ROW < 555) {
          productionRow(cb, font, outputs.get(out++), y, false);
          y += ROW;
        }
      }
      if (in == inputs.size() && out == outputs.size()) {
        if (y < 432) signatures(cb, bold, 421);
      }
      page++;
    } while (in < inputs.size() || out < outputs.size());
  }

  private void header(PdfContentByte cb, BaseFont font, BaseFont bold,
                      OrderProduction order, int page, boolean production) {
    text(cb, bold, 8.5f, 27, 34, "BRASIL ERVAS");
    text(cb, bold, 9.2f, 326, 44, "ORDEM DE PRODUÇÃO - Nº " + shortId(order));
    text(cb, font, 8, 352, 61, "Responsável: " + safe(order.getReportResponsible()));
    line(cb, 20, 88, RIGHT, 88, 0.6f);
    text(cb, font, 7.5f, 780, 40, FULL_DATE.format(created(order)));
    text(cb, font, 7.5f, 797, 60, "Pág.: " + page);
    text(cb, bold, 7.5f, 35, 102, "Requisição:");
    text(cb, font, 7.5f, 87, 102, safe(order.getRequisitionNumber()));
    text(cb, bold, 7.5f, 224, 102, "Data Início:");
    text(cb, font, 7.5f, 279, 102, fullDate(order.getStartedOn() != null ? order.getStartedOn() : created(order)));
    text(cb, bold, 7.5f, 412, 102, "Data Conclusão:");
    if (order.getCompletedOn() == null) line(cb, 491, 106, 554, 106, .5f);
    else text(cb,font,7.5f,493,102,fullDate(order.getCompletedOn()));
    text(cb, bold, 7.5f, 35, 120, "Descritivo:");
    text(cb, font, 7.5f, 89, 120, fit(order.getDescription(), 110));
  }

  private float sectionTitle(PdfContentByte cb, BaseFont bold, String title, float top, boolean input) {
    text(cb, bold, 8.5f, LEFT, top, title);
    float yy = top + 11;
    if (input) {
      label(cb, bold, LEFT, yy, "INSUMO");
      label(cb, bold, 349, yy, "Código");
      label(cb, bold, 442, yy, "Qtde");
      label(cb, bold, 490, yy, "Und.");
      label(cb, bold, 539, yy, "Dt.Fabricação");
      label(cb, bold, 630, yy, "Validade");
      label(cb, bold, 696, yy, "Lote");
    } else {
      label(cb, bold, LEFT, yy, "PRODUTO FINAL");
      label(cb, bold, 349, yy, "Código");
      label(cb, bold, 439, yy, "PCT 1kg");
      label(cb, bold, 541, yy, "Dt.Fabricação");
      label(cb, bold, 629, yy, "Validade");
      label(cb, bold, 697, yy, "Lote");
      label(cb, bold, 783, yy, "PCT 100g");
    }
    return yy + 4;
  }

  private void productionRow(PdfContentByte cb, BaseFont font, OrderProductionItems item,
                             float top, boolean input) {
    float[] xs = input ? new float[]{20,347,436,486,537,627,694,822}
        : new float[]{20,347,436,537,627,694,780,822};
    gridRow(cb, xs, top, ROW);
    text(cb, font, 7.6f, xs[0]+2, top+10, fit(item.getProductName(), 57));
    text(cb, font, 7.6f, xs[1]+3, top+10, item.getExternalProductId());
    text(cb, font, 7.6f, xs[2]+3, top+10, value(item.getQuantity()));
    if (input) {
      text(cb, font, 7.6f, xs[3]+3, top+10, item.getUnit());
      text(cb, font, 7.6f, xs[4]+3, top+10, date(item.getDateFabrication()));
      text(cb, font, 7.6f, xs[5]+3, top+10, date(item.getDateValidity()));
      text(cb, font, 7.6f, xs[6]+3, top+10, fit(item.getLot(), 23));
    } else {
      text(cb, font, 7.6f, xs[3]+3, top+10, date(item.getDateFabrication()));
      text(cb, font, 7.6f, xs[4]+3, top+10, date(item.getDateValidity()));
      text(cb, font, 7.6f, xs[5]+3, top+10, fit(item.getLot(), 14));
    }
  }

  private void signatures(PdfContentByte cb, BaseFont bold, float top) {
    float[] centers = {161, 411, 661};
    String[] titles = {"Controlador de Estoque", "Manipulador", "Supervisor"};
    for (int i=0;i<centers.length;i++) {
      line(cb, centers[i]-93, top, centers[i]+93, top, .6f);
      centered(cb, bold, 7.5f, centers[i], top+13, titles[i]);
    }
  }

  private void drawQuality(PdfWriter writer, Document doc, OrderProduction order,
                           List<OrderProductionItems> rows,
                           Map<UUID, ProductionQualityCheck> recorded) throws Exception {
    BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    BaseFont bold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    int index = 0;
    do {
      if (index > 0) doc.newPage();
      PdfContentByte cb = writer.getDirectContent();
      float[] xs = {20,243,313,502,570,639,700,822};
      gridRow(cb, new float[]{20,822}, 29, 16);
      centered(cb, bold, 8.5f, 421, 40, "PROCESSO FRACIONAMENTO - Ord.Prod. Nº " + shortId(order));
      // Keep the paper form's two-tier header and inspection columns.
      gridRow(cb, xs, 45, 47);
      centered(cb, bold, 8, 131, 75, "Produto");
      centered(cb, bold, 8, 278, 75, "Código");
      centered(cb, bold, 8, 407, 57, "Características Organolépticas");
      centered(cb, bold, 8, 407, 68, "APROVADO?");
      centered(cb, bold, 6.8f, 344, 87, "Aspecto");
      centered(cb, bold, 6.8f, 407, 87, "Cor");
      centered(cb, bold, 6.8f, 469, 87, "Odor");
      centered(cb, bold, 7, 537, 64, "Coletou");
      centered(cb, bold, 7, 537, 74, "Amostras?");
      centered(cb, bold, 7, 605, 64, "Verificação");
      centered(cb, bold, 7, 605, 74, "de Etiquetas");
      centered(cb, bold, 7, 670, 75, "Responsável");
      centered(cb, bold, 7, 760, 64, "Ação Corretiva");
      centered(cb, bold, 7, 760, 74, "Observação");
      float y = 92;
      while (index < rows.size() && y + 16 <= 547) {
        OrderProductionItems item = rows.get(index++);
        gridRow(cb, xs, y, 16);
        text(cb, font, 7.3f, 22, y+11, fit(item.getProductName(), 38));
        text(cb, font, 7.3f, 246, y+11, item.getExternalProductId());
        ProductionQualityCheck check = recorded.get(item.getId());
        Boolean[] approvals = check == null ? new Boolean[]{null,null,null}
            : new Boolean[]{check.getAspectApproved(),check.getColorApproved(),check.getOdorApproved()};
        for(int k=0;k<3;k++)
          text(cb, bold, 6.8f, 316 + k*62, y+11, mark(approvals[k]));
        if (check != null) {
          text(cb,font,7.0f,506,y+11,yesNo(check.getSamplesCollected()));
          text(cb,font,7.0f,574,y+11,yesNo(check.getLabelsVerified()));
          text(cb,font,7.0f,643,y+11,fit(check.getResponsible(),10));
          text(cb,font,7.0f,703,y+11,fit(check.getCorrectiveAction(),23));
        }
        y += 16;
      }
      // No inspection result is invented: empty cells are meant to be completed on the app or by hand.
    } while (index < rows.size());
  }

  private void gridRow(PdfContentByte cb, float[] xs, float top, float height) {
    float bottom = H-top-height;
    cb.saveState();
    cb.setLineWidth(.45f);
    for (float x : xs) {
      cb.moveTo(x, H-top);
      cb.lineTo(x, bottom);
    }
    cb.moveTo(xs[0], H-top); cb.lineTo(xs[xs.length-1], H-top);
    cb.moveTo(xs[0], bottom); cb.lineTo(xs[xs.length-1], bottom);
    cb.stroke(); cb.restoreState();
  }

  private void line(PdfContentByte cb, float x1, float y1, float x2, float y2, float width) {
    cb.saveState(); cb.setLineWidth(width);
    cb.moveTo(x1, H-y1); cb.lineTo(x2, H-y2); cb.stroke(); cb.restoreState();
  }

  private void label(PdfContentByte cb, BaseFont font, float x, float y, String s) {
    text(cb, font, 7.3f, x+2, y, s);
  }
  private void text(PdfContentByte cb, BaseFont f, float size, float x, float top, String s) {
    if (s == null) return;
    cb.beginText(); cb.setFontAndSize(f,size);
    cb.setTextMatrix(x, H-top); cb.showText(s); cb.endText();
  }
  private void centered(PdfContentByte cb, BaseFont f, float size, float x, float top, String s) {
    text(cb,f,size,x-f.getWidthPoint(s,size)/2,top,s);
  }
  private LocalDate created(OrderProduction order) {
    return order.getCreatedAt() == null ? LocalDate.now(ZONE)
        : order.getCreatedAt().atZone(ZONE).toLocalDate();
  }
  private String shortId(OrderProduction order) {
    // Until a numeric business sequence is introduced, use a stable UUID prefix.
    return order.getOrderNumber() == null ? order.getId().toString().substring(0,8).toUpperCase(Locale.ROOT)
        : order.getOrderNumber().toString();
  }
  private String fullDate(LocalDate value) { return value == null ? "" : FULL_DATE.format(value); }
  private String safe(String value) { return value == null ? "" : value; }
  private String yesNo(Boolean value) { return value == null ? "" : value ? "SIM" : "NÃO"; }
  private String mark(Boolean value) {
    if (value == null) return "SIM(  )  NÃO(  )";
    return value ? "SIM(X)  NÃO(  )" : "SIM(  )  NÃO(X)";
  }
  private String date(LocalDate value) { return value == null ? "" : DATE.format(value); }
  private String value(java.math.BigDecimal value) {
    return value == null ? "" : value.stripTrailingZeros().toPlainString();
  }
  private String fit(String value, int max) {
    return value == null ? "" : value.length() <= max ? value : value.substring(0,max-1)+"…";
  }
}
