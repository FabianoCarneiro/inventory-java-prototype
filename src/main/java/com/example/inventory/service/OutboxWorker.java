package com.example.inventory.service;

import com.example.inventory.domain.Outbox;
import com.example.inventory.repo.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.nio.file.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class OutboxWorker {

  private final OutboxRepository outboxRepo;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Path cacheDir = Paths.get("cache");
  private final Path cacheFile = cacheDir.resolve("inventory_cache.json");

  public OutboxWorker(OutboxRepository outboxRepo){ this.outboxRepo = outboxRepo; }

  @Scheduled(fixedDelay = 1000)
  public void poll() {
    Optional<Outbox> next = outboxRepo.findOnePending();
    if (next.isEmpty()) return;
    Outbox o = next.get();
    try {
      if ("InventoryChanged".equals(o.getType())) {
        processInventoryChanged(o.getPayload());
      }
      o.setAttempts(o.getAttempts()+1);
      o.setStatus("SENT");
      outboxRepo.save(o);
    } catch (Exception e) {
      o.setAttempts(o.getAttempts()+1);
      o.setStatus("ERROR");
      o.setLastError(e.toString());
      outboxRepo.save(o);
    }
  }

  private void processInventoryChanged(String payloadJson) throws Exception {
    Map<?,?> payload = mapper.readValue(payloadJson, Map.class);
    String key = payload.get("sku") + "::" + payload.get("location_id");
    if (!Files.exists(cacheDir)) Files.createDirectories(cacheDir);
    String entry = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
      "sku", payload.get("sku"),
      "location_id", payload.get("location_id"),
      "cached_at", OffsetDateTime.now().toString()
    ));
    // naive append; for demo purpose
    Files.writeString(cacheFile, (Files.exists(cacheFile) ? Files.readString(cacheFile) : "") + "\n" + entry);
  }
}
