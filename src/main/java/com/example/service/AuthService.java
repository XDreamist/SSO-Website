package com.example.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Ticket;
import com.example.repository.TicketRepository;

@Service
public class AuthService {

    @Autowired
    private TicketRepository ticketRepository;

    public String generateTicket(String username) {
        String token = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + (60 * 60 * 1000); // 1 hour expiry
        Ticket ticket = new Ticket(token, username, expiryTime);
        ticketRepository.save(ticket);
        return token;
    }

    public boolean validateTicket(String token) {
        return ticketRepository.findById(token)
            .map(ticket -> {
                if (ticket.getExpiryTime() > System.currentTimeMillis()) {
                    return true;
                } else {
                    ticketRepository.delete(ticket);
                    return false;
                }
            })
            .orElse(false);
    }
}