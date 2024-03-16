package ru.vlsu.airline.services;

import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.dto.TicketModel;
import ru.vlsu.airline.entities.*;

import java.util.List;

public interface ITicketService {
    TicketModel buyTicket(PassengerModel passenger, int flightId, int seatId, User user);
    boolean makePayment(int ticketId, PaymentModel paymentInfo);
    TicketModel reserveTicket(int ticketId);
    int cancelBooking(int bookingId);
    Booking getBookingById(Integer bookingId);
    Booking getBookingByCode(String code);
    int updateBooking(Booking booking);
    byte[] generateBoardingPass(BoardingPassModel model);
    byte[] generateTicket(BoardingPassModel model);
    BoardingPassModel getBoardingPass(int ticketId);
    Flight_seat getSeatById(int ticketId);
    int updateSeat(Flight_seat seat);
    List<Flight_seat> getSeatsByFlightId(int flightId);
    Ticket getTicketByCode(String code);
}
