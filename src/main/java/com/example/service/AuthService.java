// AuthService.java
package com.example.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.model.Ticket;
import com.example.repository.TicketRepository;

@Service
public class AuthService {

    private final TicketRepository ticketRepo;

    public AuthService(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public String generateTicket(String username) {
        String ticket = UUID.randomUUID().toString();
        Ticket ssoTicket = new Ticket();
        ssoTicket.setToken(ticket);
        ssoTicket.setUsername(username);
        ssoTicket.setCreatedAt(LocalDateTime.now());
        ssoTicket.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour validity

        ticketRepo.save(ssoTicket);
        return ticket;
    }

    public boolean hasValidTicketForUser(String username) {
        return ticketRepo.findAll().stream()
            .anyMatch(t -> t.getUsername().equals(username)
                    && t.getExpiresAt().isAfter(LocalDateTime.now()));
    }    

    public Optional<Ticket> getAnyValidTicket() {
        return ticketRepo.findAll().stream()
            .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
            .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt())) // most recent first
            .findFirst();
    }

    public String validateTicket(String ticket) {
        return ticketRepo.findByToken(ticket)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(Ticket::getUsername)
                .orElse(null);
    }

    public void invalidateTicket(String ticket) {
        ticketRepo.deleteById(ticket);
    }
}
