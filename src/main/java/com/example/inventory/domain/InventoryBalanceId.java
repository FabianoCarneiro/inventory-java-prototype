package com.example.inventory.domain;

import java.io.Serializable;
import java.util.Objects;

public class InventoryBalanceId implements Serializable {
    private String sku;
    private String locationId;

    public InventoryBalanceId() {}
    public InventoryBalanceId(String sku, String locationId) {
        this.sku = sku; this.locationId = locationId;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryBalanceId)) return false;
        InventoryBalanceId that = (InventoryBalanceId) o;
        return Objects.equals(sku, that.sku) && Objects.equals(locationId, that.locationId);
    }
    @Override public int hashCode() { return Objects.hash(sku, locationId); }
}
