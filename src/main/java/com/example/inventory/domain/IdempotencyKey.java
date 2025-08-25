package com.example.inventory.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity @Table(name="idempotency_keys")
public class IdempotencyKey {
  @Id
  @Column(name="idempotency_key", length=128) // alterado aqui
  private String key;
  @Column(nullable=false) private OffsetDateTime createdAt;
  public IdempotencyKey(){}
  public IdempotencyKey(String key){this.key=key; this.createdAt=OffsetDateTime.now();}
}
