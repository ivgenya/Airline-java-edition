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
import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.dto.TicketModel;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight_seat;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.entities.User;
import ru.vlsu.airline.services.ITicketService;
import ru.vlsu.airline.services.TicketService;

import javax.validation.Valid;
import java.util.List;

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
            return new ResponseEntity<>("Билет не найден", HttpStatus.NOT_FOUND);
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
/*
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

        if (ticket.getStatus().equals("paid")) {
             TODO: состояния
            //ticketService.updateTicket(ticket);
            //byte[] pdfBytes = ticketService.generateBoardingPass(boardingPass);
            //return ResponseEntity.ok().body(pdfBytes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Невозможно зарегистрировать билет");
    }

 */

    @PostMapping(value = "/cancel/{bookingId}", produces = "application/json")
    public ResponseEntity<String> cancelBooking(@PathVariable int bookingId) {
        Booking booking = ticketService.getBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Билет с таким номером не существует");
        }

        if (!booking.getStatus().equals("confirmed")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Невозможно отменить бронирование");
        }
        ticketService.cancelBooking(booking.getId());
        return ResponseEntity.ok("Бронирование отменено");
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

    @GetMapping(value = "/details/{ticketId}", produces = "application/json")
    public ResponseEntity<?> getTicketDetailsById(@PathVariable int ticketId) {
        BoardingPassModel ticketDetails = ticketService.getBoardingPass(ticketId);
        if (ticketDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Билет с таким номером не существует");
        }
        return ResponseEntity.ok(ticketDetails);
    }

    /*
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

     */
}
