package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.repositories.BookingRepository;
import ru.vlsu.airline.repositories.TicketRepository;
import ru.vlsu.airline.statemachine.model.TicketState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkTicketStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ticket> unpaidTickets = ticketRepository.findByStatus(TicketState.UNPAID.toString());
        logger.info("checking ticket's status");
        for (Ticket ticket : unpaidTickets) {
            Duration timeElapsed = Duration.between(ticket.getDateOfPurchase(), currentTime);
            if (timeElapsed.toMinutes() >= 30) {
                ticket.setStatus("UNABLE_TO_PAY");
                ticketRepository.save(ticket);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkBookingStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> unpaidBookings = bookingRepository.findByStatus("CONFIRMED");
        logger.info("cheking booking's status");
        for (Booking booking : unpaidBookings) {
            Duration timeElapsed = Duration.between(booking.getBookingDate(), currentTime);
            if (timeElapsed.toHours() >= 36) {
                booking.setStatus("EXPIRED");
                bookingRepository.save(booking);
                List<Ticket> unpaidTickets = ticketRepository.findByBookingId(booking.getId());
                for (Ticket ticket : unpaidTickets) {
                    ticket.setStatus("EXPIRED");
                    ticketRepository.save(ticket);
                }
            }
        }
    }
}

