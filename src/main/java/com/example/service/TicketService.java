package com.example.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class TicketService {
    // In-memory store: ticket -> {username, expiry timestamp}
    private final Map<String, TicketInfo> ticketStore = new ConcurrentHashMap<>();
    private static final long TICKET_EXPIRY_MINUTES = 30;

    public String generateTicket(String username) {
        String ticket = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(TICKET_EXPIRY_MINUTES);
        ticketStore.put(ticket, new TicketInfo(username, expiryTime));
        return ticket;
    }

    public String validateTicket(String ticket) {
        TicketInfo info = ticketStore.get(ticket);
        if (info != null && System.currentTimeMillis() <= info.getExpiryTime()) {
            return info.getUsername(); // Valid ticket
        }
        ticketStore.remove(ticket); // Remove expired/invalid ticket
        return null;
    }

    public void invalidateTicket(String ticket) {
        ticketStore.remove(ticket);
    }

    // Inner class to hold ticket info
    private static class TicketInfo {
        private final String username;
        private final long expiryTime;

        public TicketInfo(String username, long expiryTime) {
            this.username = username;
            this.expiryTime = expiryTime;
        }

        public String getUsername() {
            return username;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}