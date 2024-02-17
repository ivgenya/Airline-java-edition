package ru.vlsu.airline.services;

import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight_seat;
import ru.vlsu.airline.entities.Passenger;
import ru.vlsu.airline.entities.Ticket;

import java.util.List;

@Service
public class TicketService implements ITicketService{
    public String getTicket(){
        return "this is ticket";
    }

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
        Ticket ticket = new Ticket("ABC123", "Confirmed", "Checked");
        return ticket;
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
        /** booking.cancel();
        ticketService.updateBooking(booking);

        List<Ticket> tickets = ticketService.getTicketsByBookingId(bookingId);
        for (Ticket ticket : tickets) {
            ticket.cancel();
            ticketService.updateTicket(ticket);

            Flight_seat seat = ticketService.getSeatById(ticket.getSeat().);
            seat.setStatus("available");
            ticketService.updateSeat(seat);
        }
         **/
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
