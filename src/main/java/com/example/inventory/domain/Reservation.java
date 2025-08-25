package com.example.inventory.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name="reservations")
public class Reservation {
  @Id @Column(name="reservation_id", length=40) private String reservationId;
  @Column(length=128, nullable=false) private String sku;
  @Column(name="location_id", length=128, nullable=false) private String locationId;
  @Column(nullable=false) private int qty;
  @Column(nullable=false) private String status; // HOLD|CONFIRMED|CANCELLED|EXPIRED
  private OffsetDateTime expiresAt;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public static Reservation newHold(String sku,String loc,int qty,OffsetDateTime exp){
    Reservation r=new Reservation();
    r.reservationId=UUID.randomUUID().toString();
    r.sku=sku; r.locationId=loc; r.qty=qty;
    r.status="HOLD"; r.expiresAt=exp;
    r.createdAt=OffsetDateTime.now(); r.updatedAt=r.createdAt;
    return r;
  }

  public String getReservationId(){return reservationId;}
  public String getSku(){return sku;}
  public String getLocationId(){return locationId;}
  public int getQty(){return qty;}
  public String getStatus(){return status;}
  public void setStatus(String s){this.status=s; this.updatedAt=OffsetDateTime.now();}
  public OffsetDateTime getExpiresAt(){return expiresAt;}
  public OffsetDateTime getUpdatedAt(){return updatedAt;}
}
