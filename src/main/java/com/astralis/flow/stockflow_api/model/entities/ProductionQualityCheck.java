package com.astralis.flow.stockflow_api.model.entities;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="production_quality_check",
    uniqueConstraints=@UniqueConstraint(columnNames={"order_id","item_id"}))
@Getter @Setter
public class ProductionQualityCheck {
  @Id
  private UUID id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false)
  @JoinColumn(name="order_id",nullable=false)
  private OrderProduction order;
  @ManyToOne(fetch=FetchType.LAZY,optional=false)
  @JoinColumn(name="item_id",nullable=false)
  private OrderProductionItems item;
  @Column(name="aspect_approved")
  private Boolean aspectApproved;
  @Column(name="color_approved")
  private Boolean colorApproved;
  @Column(name="odor_approved")
  private Boolean odorApproved;
  @Column(name="samples_collected")
  private Boolean samplesCollected;
  @Column(name="labels_verified")
  private Boolean labelsVerified;
  private String responsible;
  @Column(name="corrective_action",columnDefinition="text")
  private String correctiveAction;
}
