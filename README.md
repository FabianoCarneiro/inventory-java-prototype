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

## Visão Geral do Projeto

Este projeto é uma API REST para controle de estoque e reservas de produtos, implementada em Java com Spring Boot. Ele permite:

Consultar o saldo de estoque de um produto em um local específico.
Ajustar o estoque (entrada/saída).
Criar reservas temporárias de estoque (hold).
Confirmar reservas.
Gerenciar idempotência e eventos de alteração de estoque (Outbox Pattern).
O sistema utiliza banco de dados relacional (H2 por padrão) e segue boas práticas de arquitetura, como Service Layer, Repository Pattern e Outbox para integração confiável com outros sistemas.

## Explicação dos Endpoints
1. Health Check
   
GET /healthz
Descrição: Verifica se a API está no ar.
Retorno:
{ "ok": true }

2. Consultar Estoque

GET /inventory?sku={sku}&location_id={location_id}
Descrição: Retorna o saldo de estoque disponível para um SKU (produto) em um local específico.
Parâmetros:
sku: Código do produto.
location_id: Identificador do local/filial.
Retorno:
{  "sku": "ABC-123",  "location_id": "STORE-001",  "available": 100,  "reserved": 10}

3. Ajustar Estoque

POST /stock/adjustHeaders: Idempotency-Key: <chave>Body:{  "sku": "ABC-123",  "location_id": "STORE-001",  "qty": 10}
Descrição: Ajusta o saldo de estoque de um produto em um local (entrada ou saída).
Cabeçalho:
Idempotency-Key (opcional): Garante que requisições repetidas não causem efeitos colaterais duplicados.
Retorno:
{ "status": "OK" }

4. Criar Reserva de Estoque (Hold)

POST /reservationsHeaders: Idempotency-Key: <chave>Body:{  "sku": "ABC-123",  "location_id": "STORE-001",  "qty": 5,  "ttl_seconds": 600}
Descrição: Cria uma reserva temporária de estoque (hold) para um produto em um local, por um tempo determinado.
Retorno:
{  "reservation_id": "uuid",  "status": "HOLD",  "expires_at": "2024-06-01T12:00:00Z"}

5. Confirmar Reserva

POST /reservations/confirmBody:{  "reservation_id": "uuid"}
Descrição: Confirma uma reserva, transformando o estoque reservado em saída definitiva.
Retorno:
{  "status": "CONFIRMED",  "reservation_id": "uuid"}

Outros Componentes Importantes
Outbox Pattern:
O sistema registra eventos de alteração de estoque em uma tabela outbox, garantindo que integrações externas possam ser feitas de forma confiável e resiliente.

Idempotency:
O uso do cabeçalho Idempotency-Key permite que operações críticas (como ajuste de estoque e reservas) sejam seguras contra repetições acidentais.

## Resumo
Este projeto é uma API robusta para controle de estoque, com endpoints para consulta, ajuste, reserva e confirmação, além de mecanismos de idempotência e integração confiável via Outbox.
```
