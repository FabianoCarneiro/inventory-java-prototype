package com.example.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdjustStockRequest {
  @NotBlank public String sku;
  @NotBlank public String location_id;
  @NotNull public Integer qty;
}
