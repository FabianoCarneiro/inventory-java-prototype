package com.example.inventory.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReservationRequest {
  @NotBlank public String sku;
  @NotBlank public String location_id;
  @Min(1) public int qty;
  public int ttl_seconds = 600;
}
