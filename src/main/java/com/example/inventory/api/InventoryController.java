package com.example.inventory.api;

import com.example.inventory.api.dto.*;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Controlador REST responsável pelos endpoints da API de inventário.
 * Gerencia as requisições para consulta, ajuste, reserva, confirmação e cancelamento de estoque.
 * As operações são delegadas ao {src/main/java/com/example/inventory/service/InventoryService.java}.
 * @author Fabiano Carneiro
 */
@RestController
public class InventoryController {

  private final InventoryService svc;
  public InventoryController(InventoryService svc){ this.svc = svc; }

  @GetMapping("/healthz") public Map<String,Object> health(){ return Map.of("ok", true); }

  @GetMapping("/inventory")
  public InventoryResponse getInventory(@RequestParam("sku") String sku, @RequestParam("location_id") String locationId) {
    return svc.getInventory(sku, locationId);
  }

  @PostMapping("/stock/adjust")
  @ResponseStatus(HttpStatus.OK)
  public Map<String,String> adjust(@Valid @RequestBody AdjustStockRequest req,
                                   @RequestHeader(value="Idempotency-Key",required=false) String idem) {
    svc.adjustStock(req, idem);
    return Map.of("status","OK");
  }

  @PostMapping("/reservations")
  public ReservationResponse hold(@Valid @RequestBody ReservationRequest req,
                                  @RequestHeader(value="Idempotency-Key",required=false) String idem) {
    return svc.createReservation(req, idem);
  }

  @PostMapping("/reservations/confirm")
  public Map<String,Object> confirm(@Valid @RequestBody ConfirmRequest req) {
    return svc.confirmReservation(req);
  }

  @DeleteMapping("/reservations/{reservation_id}")
  public Map<String,Object> cancel(@PathVariable("reservation_id") String reservationId) {
    return svc.cancelReservation(reservationId);
  }
}
