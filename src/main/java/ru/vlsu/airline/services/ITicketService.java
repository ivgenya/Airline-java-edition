package ru.vlsu.airline.services;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.*;

import java.io.IOException;
import java.util.List;

public interface ITicketService {
    TicketModel buyTicket(PassengerModel passenger, int flightId, int seatId, User user);
    int updateTicket(Ticket ticket);
    boolean makePayment(int ticketId, PaymentModel paymentInfo);
    boolean makePaymentBooking(int bookingId, PaymentModel paymentInfo);
    TicketModel reserveTicket(int ticketId);
    boolean cancelBooking(int bookingId);
    Booking getBookingById(Integer bookingId);
    Booking findByIdWithTickets(Integer bookingId);
    Booking getBookingByCode(String code);
    int updateBooking(Booking booking);
    byte[] generateBoardingPass(BoardingPassModel model);
    byte[] generateTicket(BoardingPassModel model);
    BoardingPassModel getBoardingPass(int ticketId);
    Flight_seat getSeatById(int ticketId);
    int updateSeat(Flight_seat seat);
    List<Flight_seat> getSeatsByFlightId(int flightId);
    Ticket getTicketByCode(String code);
    List<BoardingPassModel> getTicketByUserId(User user);
    List<BookingModel> getBookingByUserId(User user);
    List<Ticket> getTicketsByBookingId(int bookngId);
}
