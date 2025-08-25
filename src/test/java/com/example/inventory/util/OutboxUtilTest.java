package com.example.inventory.util;

import com.example.inventory.domain.Outbox;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OutboxUtilTest {

    @Test
    void buildOutbox_setsTypeAndPayload() {
        Outbox outbox = OutboxUtil.buildOutbox("InventoryChanged", Map.of("sku", "A", "location_id", "L"));
        assertEquals("InventoryChanged", outbox.getType());
        assertTrue(outbox.getPayload().contains("\"sku\""));
        assertTrue(outbox.getPayload().contains("\"location_id\""));
    }
}