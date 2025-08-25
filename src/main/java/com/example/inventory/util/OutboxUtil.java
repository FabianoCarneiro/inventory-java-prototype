package com.example.inventory.util;

import com.example.inventory.domain.Outbox;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;

public class OutboxUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Outbox buildOutbox(String type, Object payload) {
        try {
            Outbox outbox = new Outbox();
            Field typeField = Outbox.class.getDeclaredField("type");
            Field payloadField = Outbox.class.getDeclaredField("payload");
            typeField.setAccessible(true);
            payloadField.setAccessible(true);
            typeField.set(outbox, type);
            payloadField.set(outbox, mapper.writeValueAsString(payload));
            return outbox;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Outbox", e);
        }
    }
}