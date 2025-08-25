package com.example.inventory.repo;

import com.example.inventory.domain.InventoryBalance;
import com.example.inventory.domain.InventoryBalanceId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, InventoryBalanceId> {

  @Modifying
  @Query("UPDATE InventoryBalance b SET b.reserved = b.reserved + :qty " +
         "WHERE b.sku = :sku AND b.locationId = :loc AND (b.onHand - b.reserved) >= :qty")
  int tryReserve(@Param("sku") String sku, @Param("loc") String loc, @Param("qty") int qty);

  @Modifying
  @Query("UPDATE InventoryBalance b SET b.onHand = b.onHand - :qty, b.reserved = b.reserved - :qty " +
         "WHERE b.sku = :sku AND b.locationId = :loc AND b.reserved >= :qty")
  int confirm(@Param("sku") String sku, @Param("loc") String loc, @Param("qty") int qty);

  @Modifying
  @Query("UPDATE InventoryBalance b SET b.reserved = b.reserved - :qty " +
         "WHERE b.sku = :sku AND b.locationId = :loc AND b.reserved >= :qty")
  int release(@Param("sku") String sku, @Param("loc") String loc, @Param("qty") int qty);
}
