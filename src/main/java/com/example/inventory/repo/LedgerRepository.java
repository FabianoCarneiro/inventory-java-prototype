package com.example.inventory.repo;

import com.example.inventory.domain.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade { Ledger}.
 * Utilizado para registrar e consultar movimentações de estoque no livro razão.
 *
 * @author Fabiano Carneiro
 */
@Repository
public interface LedgerRepository extends JpaRepository<Ledger, String> {}
