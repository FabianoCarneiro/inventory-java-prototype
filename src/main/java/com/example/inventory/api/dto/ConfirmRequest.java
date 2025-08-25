package com.example.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmRequest {
  @NotBlank public String reservation_id;
}
