package ru.vlsu.airline.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight_seat;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.services.ITicketService;
import ru.vlsu.airline.services.TicketService;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    @Autowired
    private ITicketService ticketService;

    @PostMapping(value = "/buy",  produces = "application/json")
    public ResponseEntity<?> buyTicket(@Valid @RequestBody PassengerModel passengerModel,
                                            @RequestParam int flightId,
                                            @RequestParam int seatId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        User user = (principal instanceof User) ? (User) principal : null;
        TicketModel ticket = ticketService.buyTicket(passengerModel, flightId, seatId, user);
        if(ticket == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Произошла ошибка при покупке");
        }
        return ResponseEntity.ok(ticket);
    }

    @PostMapping(value = "/book/{ticketId}", produces = "application/json")
    public ResponseEntity<?> bookTicket(@PathVariable int ticketId) {
        TicketModel ticket = ticketService.reserveTicket(ticketId);
        if (ticket == null) {
            return new ResponseEntity<>("Ошибка", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ticket);
    }

    @PostMapping(value = "/pay/{ticketId}", produces = "application/json")
    public ResponseEntity<?> payForTicket(@PathVariable int ticketId, @RequestBody PaymentModel paymentInfo) {
        boolean paymentResult = ticketService.makePayment(ticketId, paymentInfo);
        if (paymentResult) {
            BoardingPassModel ticket = ticketService.getBoardingPass(ticketId);
            byte[] pdfBytes = ticketService.generateTicket(ticket);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ticket.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Произошла ошибка при оплате");
        }
    }

    @GetMapping(value = "/booking/{code}", produces = "application/json")
    public ResponseEntity<?> getBookingByCode(@PathVariable String code) {
        Booking booking = ticketService.getBookingByCode(code);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронирование с таким кодом не найдено");
        }

    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<?> registerForFlight(@RequestParam String ticketCode) {
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        if(ticket == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Билет с таким кодом не найден");
        }
        BoardingPassModel boardingPass = ticketService.getBoardingPass(ticket.getId());
        LocalDate flightDate = boardingPass.getDate();
        LocalTime departureTime = boardingPass.getDepartureTime();

        LocalDateTime registrationStartDateTime = LocalDateTime.of(flightDate.minusDays(1), departureTime);
        LocalDateTime registrationEndDateTime = LocalDateTime.of(flightDate, departureTime).minusHours(1);

        if (LocalDateTime.now().isBefore(registrationStartDateTime)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Регистрация на рейс не началась");
        }

        if (LocalDateTime.now().isAfter(registrationEndDateTime)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Регистрация на рейс закончилась");
        }

        if (ticket.getStatus().equals("PAID")) {
            ticket.setStatus("USED");
            ticketService.updateTicket(ticket);
            byte[] pdfBytes = ticketService.generateBoardingPass(boardingPass);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ticket.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Невозможно зарегистрировать билет");
    }


    @PostMapping(value = "/cancel/{bookingId}", produces = "application/json")
    public ResponseEntity<String> cancelBooking(@PathVariable int bookingId) {
        boolean bookingCancelled = ticketService.cancelBooking(bookingId);
        if(bookingCancelled){
            return ResponseEntity.ok("Бронирование отменено");
        }
        return ResponseEntity.ok("Произошла ошибка при отмене бронирования");
    }

    @GetMapping(value = "all-tickets", produces = "application/json")
    public ResponseEntity<List<BoardingPassModel>> getUserTickets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        User user = (principal instanceof User) ? (User) principal : null;
        List<BoardingPassModel> tickets = ticketService.getTicketByUserId(user);
        return ResponseEntity.ok(tickets);
    }
    @GetMapping(value = "all-bookings", produces = "application/json")
    public ResponseEntity<List<BookingModel>> getUserBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        User user = (principal instanceof User) ? (User) principal : null;
        List<BookingModel> bookings = ticketService.getBookingByUserId(user);
        return ResponseEntity.ok(bookings);
    }
    @GetMapping(value = "seats/{flightId}", produces = "application/json")
    public ResponseEntity<List<Flight_seat>> getSeatsByFlightId(@PathVariable int flightId) {
        List<Flight_seat> seats = ticketService.getSeatsByFlightId(flightId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping(value = "/details-by-code/{ticketCode}", produces = "application/json")
    public ResponseEntity<?> getTicketDetails(@PathVariable String ticketCode) {
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Билет с таким кодом не существует");
        }
        BoardingPassModel ticketDetails = ticketService.getBoardingPass(ticket.getId());
        return ResponseEntity.ok(ticketDetails);
    }

    @GetMapping(value = "/details-by-id/{ticketId}", produces = "application/json")
    public ResponseEntity<?> getTicketDetailsById(@PathVariable int ticketId) {
        BoardingPassModel ticketDetails = ticketService.getBoardingPass(ticketId);
        if (ticketDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Билет с таким номером не существует");
        }
        return ResponseEntity.ok(ticketDetails);
    }


    @GetMapping(value = "/details-by-booking/{code}", produces = "application/json")
    public ResponseEntity<?> getTicketDetailsByBooking(@PathVariable String code) {
        Booking booking = ticketService.getBookingByCode(code);
        if (booking != null) {
            List<Ticket> tickets = ticketService.getTicketsByBookingId(booking.getId());
            List<BoardingPassModel> ticketDetailsList = new ArrayList<>();
            for (Ticket ticket : tickets) {
                BoardingPassModel ticketDetails = ticketService.getBoardingPass(ticket.getId());
                ticketDetailsList.add(ticketDetails);
            }
            return ResponseEntity.ok(ticketDetailsList);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронирование с таким кодом не существует");
    }
}
