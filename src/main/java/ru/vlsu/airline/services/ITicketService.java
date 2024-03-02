package ru.vlsu.airline.services;

import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight_seat;
import ru.vlsu.airline.entities.Passenger;
import ru.vlsu.airline.entities.Ticket;

import java.util.List;

public interface ITicketService {
    void checkTicketStatus();
    List<Ticket> getAllTickets();
    Ticket getTicketById(int ticketId);
    List<Ticket> getTicketsByBookingId(int bookingId);
    int createTicket(Ticket ticket);
    int updateTicket(Ticket ticket);
    int deleteTicket(int ticketId);
    int createPassenger(Passenger passenger);
    Ticket buyTicket(PassengerModel passenger, int flightId, int seatId);
    boolean makePayment(int ticketId, PaymentModel paymentInfo);
    int reserveTicket(int ticketId);
    int cancellBooking(int bookingId);
    Booking getBookingById(Integer bookingId);
    Booking getBookingByCode(String code);
    int updateBooking(Booking booking);
    byte[] generateBoardingPass(BoardingPassModel model);
    byte[] generateTicket(BoardingPassModel model);
    BoardingPassModel getBoardingPass(int ticketId);
    Flight_seat getSeatById(int ticketId);
    int updateSeat(Flight_seat seat);
    List<Flight_seat> getSeatsByFlightId(int flightId);
    Ticket getTicketByCode(String ticketCode);
}
