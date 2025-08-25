package com.example.inventory.repo;

import com.example.inventory.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositório JPA para a entidade {Outbox}.
 * Responsável pelo gerenciamento dos eventos do padrão Outbox para integração confiável com sistemas externos.
 *
 * @author Fabiano Carneiro
 */
@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
  @Query(value = "SELECT * FROM outbox WHERE status = 'PENDING' ORDER BY id LIMIT 1", nativeQuery = true)
  Optional<Outbox> findOnePending();
}
