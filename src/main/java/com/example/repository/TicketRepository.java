// SsoTicketRepository.java
package com.example.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByToken(String token);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
