package com.example.inventory.repo;

import com.example.inventory.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade { Reservation}.
 * Responsável pela persistência e consulta de reservas de estoque.
 *
 * @author Fabiano Carneiro
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {}
