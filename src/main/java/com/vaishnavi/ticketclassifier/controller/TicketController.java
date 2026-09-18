package com.vaishnavi.ticketclassifier.controller;

import com.vaishnavi.ticketclassifier.model.Ticket;
import com.vaishnavi.ticketclassifier.service.GeminiClassifierService;
import com.vaishnavi.ticketclassifier.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final GeminiClassifierService geminiClassifierService;
    private final TicketService ticketService;

    public TicketController(GeminiClassifierService geminiClassifierService, TicketService ticketService) {
        this.geminiClassifierService = geminiClassifierService;
        this.ticketService = ticketService;
    }

    @PostMapping
    public Ticket createAndClassifyTicket(@RequestBody Ticket ticket) {
        String[] result = geminiClassifierService.classify(ticket.getSubject(), ticket.getDescription());
        ticket.setCategory(result[0]);
        ticket.setUrgency(result[1]);
        return ticketService.createTicket(ticket);
    }

    @GetMapping
    public java.util.List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
        if (ticketService.getTicketById(id) == null) {
            return ResponseEntity.status(404).body("Ticket not found with id " + id);
        }
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Ticket deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        Ticket updated = ticketService.updateTicket(id, ticket);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
