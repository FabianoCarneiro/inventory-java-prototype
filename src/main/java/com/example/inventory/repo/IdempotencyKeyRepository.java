package com.example.inventory.repo;

import com.example.inventory.domain.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade { IdempotencyKey}.
 * Gerencia as chaves de idempotência para garantir que operações críticas não sejam executadas múltiplas vezes.
 *
 * @author Fabiano Carneiro
 */
@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {}
