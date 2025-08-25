package com.example.inventory.api.dto;

public class InventoryResponse {
  public String sku;
  public String location_id;
  public int on_hand;
  public int reserved;
  public int available;
  public String last_updated;
}
