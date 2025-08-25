package com.example.inventory.service;

import com.example.inventory.api.dto.*;
import com.example.inventory.domain.*;
import com.example.inventory.repo.*;
import com.example.inventory.util.OutboxUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

  private final InventoryBalanceRepository balanceRepo;
  private final ReservationRepository reservationRepo;
  private final LedgerRepository ledgerRepo;
  private final IdempotencyKeyRepository idemRepo;
  private final OutboxRepository outboxRepo;
  private final ObjectMapper mapper = new ObjectMapper();

  public InventoryService(InventoryBalanceRepository balanceRepo, ReservationRepository reservationRepo,
                          LedgerRepository ledgerRepo, IdempotencyKeyRepository idemRepo, OutboxRepository outboxRepo) {
    this.balanceRepo = balanceRepo;
    this.reservationRepo = reservationRepo;
    this.ledgerRepo = ledgerRepo;
    this.idemRepo = idemRepo;
    this.outboxRepo = outboxRepo;
  }

  private void ensureIdempotency(String key) {
    if (key == null || key.isBlank()) return;
    try { idemRepo.save(new IdempotencyKey(key)); }
    catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate request (idempotency key)");
    }
  }

  public InventoryResponse getInventory(String sku, String locationId) {
    var row = balanceRepo.findById(new InventoryBalanceId(sku, locationId)).orElse(null);
    if (row == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    InventoryResponse resp = new InventoryResponse();
    resp.sku = sku; resp.location_id = locationId;
    resp.on_hand = row.getOnHand(); resp.reserved = row.getReserved(); resp.available = row.getAvailable();
    resp.last_updated = null;
    return resp;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void adjustStock(AdjustStockRequest req, String idem) {
    if (req.qty == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty cannot be 0");
    ensureIdempotency(idem);
    var id = new InventoryBalanceId(req.sku, req.location_id);
    var row = balanceRepo.findById(id).orElseGet(() -> balanceRepo.save(new InventoryBalance(req.sku, req.location_id)));
    row.setOnHand(row.getOnHand() + req.qty);
    balanceRepo.save(row);
    ledgerRepo.save(Ledger.of(req.sku, req.location_id, req.qty, "ADJUST", "{\"idem\":\""+idem+"\"}"));
    publishOutboxInventoryChanged(req.sku, req.location_id);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ReservationResponse createReservation(ReservationRequest req, String idem) {
    if (req.qty <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty must be > 0");
    ensureIdempotency(idem);
    InventoryBalanceId id = new InventoryBalanceId(req.sku, req.location_id);
    balanceRepo.findById(id).orElseGet(() -> balanceRepo.save(new InventoryBalance(req.sku, req.location_id)));
    int updated = balanceRepo.tryReserve(req.sku, req.location_id, req.qty);
    if (updated == 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient available stock");
    OffsetDateTime exp = OffsetDateTime.now().plusSeconds(req.ttl_seconds);
    Reservation r = Reservation.newHold(req.sku, req.location_id, req.qty, exp);
    reservationRepo.save(r);
    ledgerRepo.save(Ledger.of(req.sku, req.location_id, -req.qty, "RESERVE_HOLD", "{\"reservation_ixd\":\""+r.getReservationId()+"\"}"));
    publishOutboxInventoryChanged(req.sku, req.location_id);
    ReservationResponse resp = new ReservationResponse();
    resp.reservation_id = r.getReservationId();
    resp.status = "HOLD";
    resp.expires_at = exp.toString();
    return resp;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Map<String,Object> confirmReservation(ConfirmRequest req) {
    var r = reservationRepo.findById(req.reservation_id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found"));
    if (!"HOLD".equals(r.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "cannot confirm from status "+r.getStatus());
    int updated = balanceRepo.confirm(r.getSku(), r.getLocationId(), r.getQty());
    if (updated == 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "inconsistent reservation state");
    r.setStatus("CONFIRMED"); reservationRepo.save(r);
    ledgerRepo.save(Ledger.of(r.getSku(), r.getLocationId(), -r.getQty(), "SALE", "{\"reservation_id\":\""+r.getReservationId()+"\"}"));
    publishOutboxInventoryChanged(r.getSku(), r.getLocationId());
    Map<String, Object> map = new HashMap<>();
    map.put("status", "CONFIRMED");
    map.put("reservation_id", r.getReservationId());
    return map;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Map<String,Object> cancelReservation(String reservationId) {
    var r = reservationRepo.findById(reservationId)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found"));
    if (!"HOLD".equals(r.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "cannot cancel from status "+r.getStatus());
    int updated = balanceRepo.release(r.getSku(), r.getLocationId(), r.getQty());
    if (updated == 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "inconsistent reservation state");
    r.setStatus("CANCELLED"); reservationRepo.save(r);
    ledgerRepo.save(Ledger.of(r.getSku(), r.getLocationId(), r.getQty(), "RESERVE_CANCEL", "{\"reservation_id\":\""+r.getReservationId()+"\"}"));
    publishOutboxInventoryChanged(r.getSku(), r.getLocationId());
    var map = new HashMap<String,Object>(); map.put("status","CANCELLED"); map.put("reservation_id", r.getReservationId()); return map;
  }

  private void publishOutboxInventoryChanged(String sku, String locationId) {
    Outbox outbox = OutboxUtil.buildOutbox("InventoryChanged", Map.of("sku", sku, "location_id", locationId));
    outboxRepo.save(outbox);
  }
}
