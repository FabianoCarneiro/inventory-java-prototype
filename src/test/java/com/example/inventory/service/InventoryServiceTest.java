package com.example.inventory.service;

import com.example.inventory.api.dto.*;
import com.example.inventory.domain.*;
import com.example.inventory.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock InventoryBalanceRepository balanceRepo;
    @Mock ReservationRepository reservationRepo;
    @Mock LedgerRepository ledgerRepo;
    @Mock IdempotencyKeyRepository idemRepo;
    @Mock OutboxRepository outboxRepo;

    @InjectMocks InventoryService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new InventoryService(balanceRepo, reservationRepo, ledgerRepo, idemRepo, outboxRepo);
    }

    @Test
    void getInventory_notFound_throws() {
        when(balanceRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getInventory("sku", "loc"));
    }

    @Test
    void adjustStock_zeroQty_throws() {
        AdjustStockRequest req = new AdjustStockRequest();
        req.sku = "sku"; req.location_id = "loc"; req.qty = 0;
        assertThrows(ResponseStatusException.class, () -> service.adjustStock(req, "idem"));
    }

    // Adicione outros testes para createReservation, confirmReservation, cancelReservation conforme necessário
}