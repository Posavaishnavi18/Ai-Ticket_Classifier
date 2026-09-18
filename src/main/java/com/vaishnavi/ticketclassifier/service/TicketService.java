package com.vaishnavi.ticketclassifier.service;

import com.vaishnavi.ticketclassifier.model.Ticket;
import com.vaishnavi.ticketclassifier.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        Ticket existing = ticketRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (updatedTicket.getSubject() != null) {
            existing.setSubject(updatedTicket.getSubject());
        }
        if (updatedTicket.getDescription() != null) {
            existing.setDescription(updatedTicket.getDescription());
        }
        if (updatedTicket.getCategory() != null) {
            existing.setCategory(updatedTicket.getCategory());
        }
        if (updatedTicket.getUrgency() != null) {
            existing.setUrgency(updatedTicket.getUrgency());
        }
        return ticketRepository.save(existing);
    }
}
