package com.example.inventory.domain;

import jakarta.persistence.*;

@Entity
@IdClass(InventoryBalanceId.class)
@Table(name = "inventory_balance")
public class InventoryBalance {
    @Id @Column(length = 128, nullable = false) private String sku;
    @Id @Column(length = 128, nullable = false) private String locationId;
    @Column(nullable = false) private int onHand = 0;
    @Column(nullable = false) private int reserved = 0;

    public InventoryBalance() {}
    public InventoryBalance(String sku, String locationId){this.sku=sku;this.locationId=locationId;}

    public String getSku(){return sku;}
    public String getLocationId(){return locationId;}
    public int getOnHand(){return onHand;}
    public int getReserved(){return reserved;}
    public void setOnHand(int v){this.onHand=v;}
    public void setReserved(int v){this.reserved=v;}
    @Transient public int getAvailable(){return onHand - reserved;}
}
