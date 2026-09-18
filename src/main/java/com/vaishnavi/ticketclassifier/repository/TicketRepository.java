package com.vaishnavi.ticketclassifier.repository;

import com.vaishnavi.ticketclassifier.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
