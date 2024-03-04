package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.entities.*;
import ru.vlsu.airline.repositories.*;
import ru.vlsu.airline.statemachine.model.TicketEvent;
import ru.vlsu.airline.statemachine.model.TicketState;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TicketService implements ITicketService{

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightSeatRepository seatRepository;



    @Override
    public Ticket buyTicket(PassengerModel passenger, int flightId, int seatId) {
        Passenger newPassenger = passengerRepository.save(new Passenger(passenger));
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            Flight existFlight = flight.get();
            Optional<Flight_seat> seat = seatRepository.findById(seatId);
            if(seat.isPresent() && seat.get().getStatus().equals("available")){
                Flight_seat existSeat = seat.get();
                Ticket ticket = new Ticket();
                ticket.setCode(UUID.randomUUID().toString());
                ticket.setStatus("UNPAID");
                ticket.setBaggageType("economy");
                ticket.setDateOfPurchase(LocalDateTime.now());
                ticket.setPassengerId(newPassenger.getId());
                ticket.setFlightId(existFlight.getId());
                ticket.setSeatId(existSeat.getId());
                ticket.setBookingId(null);
                existSeat.setStatus("reserved");
                seatRepository.save(existSeat);
                return ticketRepository.save(ticket);
            }
        }
        return null;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkTicketStatus() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ticket> unpaidTickets = ticketRepository.findByStatus(TicketState.UNPAID.toString());
        logger.info("cheking ticket's status");
        for (Ticket ticket : unpaidTickets) {
            Duration timeElapsed = Duration.between(ticket.getDateOfPurchase(), currentTime);
            if (timeElapsed.toMinutes() >= 10) {
                ticket.setStatus("UNABLE_TO_PAY");
                ticketRepository.save(ticket);
            }
        }
    }


    @Override
    public boolean makePayment(int ticketId, PaymentModel paymentInfo) {
        return true;
    }

    @Override
    public Ticket reserveTicket(int ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if(ticket.isPresent()){
            Ticket existTicket = ticket.get();
            if(existTicket.getStatus().equals("UNPAID")){
                Booking booking = new Booking();
                booking.setBookingDate(LocalDateTime.now());
                booking.setCode(UUID.randomUUID().toString());
                booking.setStatus("CONFIRMED");
                Booking saved_booking = bookingRepository.save(booking);
                existTicket.setBookingId(saved_booking.getId());
                existTicket.setStatus("BOOKED");
                return ticketRepository.save(existTicket);
            }
            return null;
        }
        return null;
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
                for(Ticket ticket: unpaidTickets){
                    ticket.setStatus("EXPIRED");
                    Flight_seat seat = seatRepository.findById(ticket.getSeatId()).get();
                    seat.setStatus("available");
                    ticketRepository.save(ticket);
                    seatRepository.save(seat);
                }
            }
        }
    }

    @Override
    public int cancellBooking(int bookingId) {
        // TODO: переделать с репозиторием
        /* booking.cancel();
        ticketService.updateBooking(booking);

        List<Ticket> tickets = ticketService.getTicketsByBookingId(bookingId);
        for (Ticket ticket : tickets) {
            ticket.cancel();
            ticketService.updateTicket(ticket);

            Flight_seat seat = ticketService.getSeatById(ticket.getSeat().);
            seat.setStatus("available");
            ticketService.updateSeat(seat);
        }
         */
        return 0;
    }

    @Override
    public Booking getBookingById(Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if(booking.isPresent()){
            return booking.get();
        }
        return null;
    }

    @Override
    public Booking getBookingByCode(String code) {
        Optional<Booking> booking = bookingRepository.findByCode(code);
        if(booking.isPresent()){
            return booking.get();
        }
        return null;
    }

    @Override
    public int updateBooking(Booking booking) {
        if (bookingRepository.existsById(booking.getId())) {
            bookingRepository.save(booking);
            return booking.getId();
        } else {
            return -1;
        }
    }

    @Override
    public byte[] generateBoardingPass(BoardingPassModel model) {
        return new byte[0];
    }

    @Override
    public byte[] generateTicket(BoardingPassModel model) {
        return new byte[0];
    }

    @Override
    public BoardingPassModel getBoardingPass(int ticketId) {
        return null;
    }

    @Override
    public Flight_seat getSeatById(int seatId) {
        Optional<Flight_seat> seat = seatRepository.findById(seatId);
        if(seat.isPresent()){
            return seat.get();
        }
        return null;
    }

    @Override
    public int updateSeat(Flight_seat seat) {
        if (seatRepository.existsById(seat.getId())) {
            seatRepository.save(seat);
            return seat.getId();
        } else {
            return -1;
        }
    }

    @Override
    public List<Flight_seat> getSeatsByFlightId(int flightId) {
        List<Flight_seat> seats = seatRepository.findByFlightId(flightId);
        return seats;
    }

    @Override
    public Ticket getTicketByCode(String code) {
        Optional<Ticket> ticket = ticketRepository.findByCode(code);
        if(ticket.isPresent()){
            return ticket.get();
        }
        return null;
    }
}
