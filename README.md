# Distributed Inventory — Java/Spring Boot (Complete)

Spring Boot 3 + H2 + JPA with **consistent reservations (TCC)**, **idempotency**, **conditional updates**, and an **Outbox** worker.

## Run
```bash
mvn spring-boot:run
```

## Endpoints
- `GET /inventory?sku=...&location_id=...`
- `POST /stock/adjust` (header `Idempotency-Key`)
- `POST /reservations` (header `Idempotency-Key`)
- `POST /reservations/confirm`
- `DELETE /reservations/{reservation_id}`
```

## Notes
- Uses `@Transactional(isolation = SERIALIZABLE)` + conditional updates to ensure `available >= 0` under concurrency.
- Outbox worker writes/updates a simple JSON cache file in `./cache/` to simulate read-model invalidation.
- Replace H2 with Postgres/Cockroach/Yugabyte for production; keep the same repo methods.
