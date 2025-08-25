package com.example.inventory.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="outbox")
public class Outbox {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(length=40, nullable=false) private String eventId;
  @Column(length=64, nullable=false) private String type;
  @Column(columnDefinition="CLOB", nullable=false) private String payload;
  @Column(length=16, nullable=false) private String status = "PENDING";
  @Column(nullable=false) private int attempts = 0;
  @Column(columnDefinition="CLOB") private String lastError;

  // Construtor padrão que gera o eventId automaticamente
  public Outbox() {
    this.eventId = UUID.randomUUID().toString();
  }

  public Long getId(){return id;}
  public String getEventId() { return eventId; }
  public String getType(){return type;}
  public String getPayload(){return payload;}
  public int getAttempts(){return attempts;}

  public void setStatus(String s){this.status=s;}
  public void setAttempts(int a){this.attempts=a;}
  public void setLastError(String e){this.lastError=e;}
}
