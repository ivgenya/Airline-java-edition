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
    private StateMachine<TicketState, TicketEvent> stateMachine;
    @Autowired
    private JpaPersist jpaPersist;


    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightSeatRepository seatRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return null;
    }

    @Override
    public Ticket getTicketById(int ticketId) {
        return null;
    }

    @Override
    public List<Ticket> getTicketsByBookingId(int bookingId) {
        return null;
    }

    @Override
    public int createTicket(Ticket ticket) {
        return 0;
    }

    @Override
    public int updateTicket(Ticket ticket) {
        return 0;
    }

    @Override
    public int deleteTicket(int ticketId) {
        return 0;
    }

    @Override
    public int createPassenger(Passenger passenger) {
        return 0;
    }

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
                //Ticket saved_ticket = ticketRepository.save(ticket);
                //Integer contextObj = saved_ticket.getId();
                //StateMachineContext<TicketState, TicketEvent> context = new DefaultStateMachineContext<>(TicketState.UNPAID, null, null, null, null, "ticketStateMachine");
                //jpaPersist.write(context, contextObj);
                //return saved_ticket;
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
                //stateMachine.start();
                //stateMachine.getExtendedState().getVariables().put("ticketId", ticket.getId());
                //stateMachine.sendEvent(TicketEvent.UNABLE);
                //Integer contextObj = ticket.getId();
                //StateMachineContext<TicketState, TicketEvent> context = new DefaultStateMachineContext<>(stateMachine.getState().getId(), null, null, null, null, "ticketStateMachine");
                //jpaPersist.write(context, contextObj);
            }
        }
    }




    @Override
    public boolean makePayment(int ticketId, PaymentModel paymentInfo) {
        return false;
    }

    @Override
    public int reserveTicket(int ticketId) {
        return 1;
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
        return null;
    }

    @Override
    public Booking getBookingByCode(String code) {
        return null;
    }

    @Override
    public int updateBooking(Booking booking) {
        return 0;
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
    public Flight_seat getSeatById(int ticketId) {
        return null;
    }

    @Override
    public int updateSeat(Flight_seat seat) {
        return 0;
    }

    @Override
    public List<Flight_seat> getSeatsByFlightId(int flightId) {
        return null;
    }

    @Override
    public Ticket getTicketByCode(String ticketCode) {
        return null;
    }
}
