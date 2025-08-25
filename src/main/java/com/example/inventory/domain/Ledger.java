package com.example.inventory.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name="ledger")
public class Ledger {
  @Id @Column(name="event_id", length=40) private String eventId;
  @Column(length=128, nullable=false) private String sku;
  @Column(name="location_id", length=128, nullable=false) private String locationId;
  @Column(nullable=false) private int qtyChange;
  @Column(nullable=false) private String eventType; // ADJUST|RESERVE_HOLD|SALE|RESERVE_CANCEL
  @Column(nullable=false) private OffsetDateTime ts;
  @Column(columnDefinition="CLOB") private String metadata;

  public static Ledger of(String sku,String loc,int qty,String type,String meta){
    Ledger l=new Ledger(); l.eventId=UUID.randomUUID().toString();
    l.sku=sku; l.locationId=loc; l.qtyChange=qty; l.eventType=type;
    l.ts=OffsetDateTime.now(); l.metadata=meta; return l;
  }
}
