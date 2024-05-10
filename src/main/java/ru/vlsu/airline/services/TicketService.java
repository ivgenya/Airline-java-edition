package ru.vlsu.airline.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.*;
import ru.vlsu.airline.repositories.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TicketService implements ITicketService {

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public List<BoardingPassModel> getTicketByUserId(User user) {
        List<BoardingPassModel> ticketsModels = new ArrayList<BoardingPassModel>();
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            User managedUser = optionalUser.get();
            List<Ticket> tickets = ticketRepository.findByUserId(managedUser.getId());
            for (Ticket t : tickets) {
                ticketsModels.add(getBoardingPass(t.getId()));
            }
        }
        return ticketsModels;
    }

    @Override
    public List<BookingModel> getBookingByUserId(User user) {
        List<BookingModel> bookingModels = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findByUser(user);
        for (Booking booking : bookings) {
            BookingModel bookingModel = new BookingModel();
            bookingModel.setId(booking.getId());
            bookingModel.setCode(booking.getCode());
            bookingModel.setBookingDate(booking.getBookingDate());
            bookingModel.setStatus(booking.getStatus());
            List<Ticket> tickets = booking.getTickets();
            List<BoardingPassModel> boardingPassModels = new ArrayList<>();
            for (Ticket ticket : tickets) {
                boardingPassModels.add(getBoardingPass(ticket.getId()));
            }
            bookingModel.setTickets(boardingPassModels);
            bookingModels.add(bookingModel);
        }
        return bookingModels;
    }

    @Override
    @Transactional
    public TicketModel buyTicket(PassengerModel passenger, int flightId, int seatId, User user) {
        Passenger newPassenger = new Passenger(passenger);
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            Flight existFlight = flight.get();
            logger.info(existFlight.getStatus());
            Optional<Flight_seat> seat = seatRepository.findById(seatId);
            if (seat.isPresent() && seat.get().getStatus().equals("available")) {
                Optional<User> optionalUser = userRepository.findById(user.getId());
                if (optionalUser.isPresent()) {
                    User managedUser = optionalUser.get();
                    Flight_seat existSeat = seat.get();
                    Ticket ticket = new Ticket();
                    ticket.setCode(UUID.randomUUID().toString());
                    ticket.setStatus("UNPAID");
                    ticket.setBaggageType("economy");
                    ticket.setDateOfPurchase(LocalDateTime.now());
                    ticket.setPassenger(newPassenger);
                    logger.info(newPassenger.getName());
                    ticket.setFlight(existFlight);
                    ticket.setSeat(existSeat);
                    ticket.setUser(managedUser);
                    ticket.setBooking(null);
                    existSeat.setStatus("reserved");
                    seatRepository.save(existSeat);
                    Ticket savedTicket = ticketRepository.save(ticket);
                    return convertToTicketModel(savedTicket);
                }
            }
        }
        return null;
    }


    @Override
    public int updateTicket(Ticket ticket) {
        if (ticketRepository.existsById(ticket.getId())) {
            ticketRepository.save(ticket);
            return ticket.getId();
        } else {
            return -1;
        }
    }

    @Override
    public boolean makePayment(int ticketId, PaymentModel paymentInfo) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            if (ticket.get().getStatus().equals("UNPAID") || ticket.get().getStatus().equals("BOOKED")) {
                Ticket existTicket = ticket.get();
                existTicket.setStatus("PAID");
                existTicket.setDateOfPurchase(LocalDateTime.now());
                ticketRepository.save(existTicket);
                if (existTicket.getBooking() != null) {
                    existTicket.getBooking().setStatus("PAID"); //TODO:
                    bookingRepository.save(existTicket.getBooking());
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean makePaymentBooking(int bookingId, PaymentModel paymentInfo) {
        Optional<Booking> booking = bookingRepository.findByIdWithTickets(bookingId);
        if (booking.isPresent()) {
            if (booking.get().getStatus().equals("CONFIRMED")) {
                booking.get().setStatus("PAID");
                List<Ticket> tickets = booking.get().getTickets();
                for (Ticket t : tickets) {
                    t.setStatus("PAID");
                    ticketRepository.save(t);
                }
                bookingRepository.save(booking.get());
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    @Override
    @Transactional
    public TicketModel reserveTicket(int ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            Ticket existTicket = ticket.get();
            if (existTicket.getStatus().equals("UNPAID")) {
                Booking booking = new Booking();
                booking.setBookingDate(LocalDateTime.now());
                booking.setCode(UUID.randomUUID().toString());
                booking.setStatus("CONFIRMED");
                Booking saved_booking = bookingRepository.save(booking);
                existTicket.setBooking(saved_booking);
                existTicket.setStatus("BOOKED");
                Ticket savedTicket = ticketRepository.save(existTicket);
                return convertToTicketModel(savedTicket);
            }
            return null;
        }
        return null;
    }


    @Override
    @Transactional
    public boolean cancelBooking(int bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking existBooking = booking.get();
            existBooking.setStatus("CANCELLED");
            List<Ticket> tickets = existBooking.getTickets();
            for (Ticket ticket : tickets) {
                ticket.setStatus("CANCELLED");
                Flight_seat seat = ticket.getSeat();
                seat.setStatus("available");
                ticketRepository.save(ticket);
                seatRepository.save(seat);
            }
            bookingRepository.save(existBooking);
            return true;
        }
        return false;
    }

    @Override
    public Booking getBookingById(Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        }
        return null;
    }

    @Override
    public Booking findByIdWithTickets(Integer bookingId) {
        Optional<Booking> booking = bookingRepository.findByIdWithTickets(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        }
        return null;
    }

    @Override
    public Booking getBookingByCode(String code) {
        Optional<Booking> booking = bookingRepository.findByCode(code);
        if (booking.isPresent()) {
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
        try {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 InputStream templateStream = new ClassPathResource("pdf/template_bp.pdf").getInputStream()) {
                PDDocument pdfDocument = PDDocument.load(templateStream);
                PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
                PDAcroForm form = docCatalog.getAcroForm();
                form.getField("passenger").setValue(model.getSurname().toUpperCase() + " " + model.getName().toUpperCase());
                form.getField("from").setValue(model.getDepShortName());
                form.getField("to").setValue(model.getArrShortName());
                form.getField("date").setValue(model.getDate().toString());
                form.getField("dep_time").setValue(model.getDepartureTime().toString());
                form.getField("arr_time").setValue(model.getArrivalTime().toString());
                form.getField("flight").setValue(model.getShortName() + model.getNumber());
                form.getField("seat").setValue(model.getSeat());
                form.getField("gate").setValue(Integer.toString(model.getGate()));
                form.getField("from_small").setValue(model.getDepShortName());
                form.getField("to_small").setValue(model.getArrShortName());
                form.getField("date_small").setValue(model.getDate().toString() + " " + model.getDepartureTime().toString());
                form.getField("flight_small").setValue(model.getShortName() + model.getNumber());
                form.getField("seat_small").setValue(model.getSeat());
                form.getField("gate_small").setValue(Integer.toString(model.getGate()));
                form.flatten();
                pdfDocument.save(bos);
                pdfDocument.close();
                return bos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] generateTicket(BoardingPassModel model) {
        try {
            Resource resource = resourceLoader.getResource("classpath:pdf/template_ticket.pdf");
            if (!resource.exists()) {
                logger.info("Файл 'pdf/template_ticket.pdf' не найден.");
                return null;
            }
            File file = resource.getFile();
            PDDocument document = PDDocument.load(file);
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            if (acroForm != null) {
                acroForm.getField("ticket_number").setValue(model.getTicketCode());
                acroForm.getField("booking_number").setValue(model.getBookingCode() != null ? model.getBookingCode() : "-");
                acroForm.getField("date").setValue(model.getDateOfPurchase().format(formatter));
                acroForm.getField("document").setValue(model.getDocumentNumber());
                acroForm.getField("name").setValue(model.getName().toUpperCase());
                acroForm.getField("surname").setValue(model.getSurname().toUpperCase());
                acroForm.getField("date_of_birth").setValue(model.getDateOfBirth().toString());
                acroForm.getField("email").setValue(model.getEmail());
                acroForm.getField("number").setValue(model.getShortName() + model.getNumber());
                acroForm.getField("dep_short_name").setValue(model.getDepShortName());
                acroForm.getField("arr_short_name").setValue(model.getArrShortName());
                acroForm.getField("flight_date").setValue(model.getDate().toString());
                acroForm.getField("dep_time").setValue(model.getDepartureTime().toString());
                acroForm.getField("arr_time").setValue(model.getArrivalTime().toString());
                acroForm.getField("dep_city").setValue(model.getDepCity());
                acroForm.getField("arr_city").setValue(model.getArrCity());
                acroForm.getField("duration").setValue("In the way: " + model.getFlightDuration().toString());
                acroForm.getField("class").setValue(model.getFlightClass());
                acroForm.getField("baggage").setValue(model.getBaggageType());
                acroForm.getField("price").setValue(model.getPrice() + " RUB.");
                acroForm.getField("total_price").setValue(model.getPrice() + " RUB.");
                acroForm.getField("seat").setValue(model.getSeat());
                acroForm.getField("terminal").setValue(model.getTerminal());
                acroForm.flatten();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BoardingPassModel getBoardingPass(int ticketId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            BoardingPassModel boardingPass = new BoardingPassModel();
            boardingPass.setTicketId(ticket.getId());
            boardingPass.setTicketCode(ticket.getCode());
            boardingPass.setBookingCode(ticket.getBooking() != null ? ticket.getBooking().getCode() : null);
            boardingPass.setBookingId(ticket.getBooking() != null ? ticket.getBooking().getId() : null);
            boardingPass.setSurname(ticket.getPassenger().getSurname());
            boardingPass.setName(ticket.getPassenger().getName());
            boardingPass.setDocumentNumber(ticket.getPassenger().getDocumentNumber());
            boardingPass.setDateOfBirth(ticket.getPassenger().getDateOfBirth());
            boardingPass.setGender(ticket.getPassenger().getGender());
            boardingPass.setEmail(ticket.getPassenger().getEmail());
            boardingPass.setSeat(ticket.getSeat().getPlaneSeat().getNumber());
            boardingPass.setPrice(ticket.getSeat().getPrice());
            boardingPass.setTicketStatus(ticket.getStatus());
            boardingPass.setFlightClass(ticket.getSeat().getPlaneSeat().getSeatClass());
            boardingPass.setBaggageType(ticket.getBaggageType());
            boardingPass.setDate(ticket.getFlight().getDate());
            boardingPass.setStatus(ticket.getFlight().getStatus());
            boardingPass.setDateOfPurchase(ticket.getDateOfPurchase());
            boardingPass.setGate(ticket.getFlight().getGate());
            boardingPass.setShortName(ticket.getFlight().getSchedule().getAirline().getShortName());
            boardingPass.setNumber(ticket.getFlight().getSchedule().getNumber());
            boardingPass.setDepShortName(ticket.getFlight().getSchedule().getDepartureAirport().getShortName());
            boardingPass.setDepCity(ticket.getFlight().getSchedule().getDepartureAirport().getCity());
            boardingPass.setArrShortName(ticket.getFlight().getSchedule().getArrivalAirport().getShortName());
            boardingPass.setArrCity(ticket.getFlight().getSchedule().getArrivalAirport().getCity());
            boardingPass.setDepartureTime(ticket.getFlight().getSchedule().getDepartureTime());
            boardingPass.setArrivalTime(ticket.getFlight().getSchedule().getArrivalTime());
            boardingPass.setFlightDuration(ticket.getFlight().getSchedule().getFlightDuration());
            boardingPass.setTerminal(ticket.getFlight().getSchedule().getTerminal().getName());
            boardingPass.setBookingDate(ticket.getBooking() != null ? ticket.getBooking().getBookingDate() : null);
            boardingPass.setBookingStatus(ticket.getBooking() != null ? ticket.getBooking().getStatus() : null);
            return boardingPass;
        }
        return null;
    }


    @Override
    public Flight_seat getSeatById(int seatId) {
        Optional<Flight_seat> seat = seatRepository.findById(seatId);
        if (seat.isPresent()) {
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
        if (ticket.isPresent()) {
            return ticket.get();
        }
        return null;
    }

    public TicketModel convertToTicketModel(Ticket ticket) {
        TicketModel ticketModel = new TicketModel();
        ticketModel.setId(ticket.getId());
        ticketModel.setCode(ticket.getCode());
        ticketModel.setPassengerId(ticket.getPassenger().getId());
        ticketModel.setFlightId(ticket.getFlight().getId());
        ticketModel.setSeatId(ticket.getSeat().getId());
        ticketModel.setDateOfPurchase(ticket.getDateOfPurchase());
        if (ticket.getBooking() == null) {
            ticketModel.setBookingId(null);
        } else {
            ticketModel.setBookingId(ticket.getBooking().getId());
        }
        ticketModel.setStatus(ticket.getStatus());
        ticketModel.setBaggageType(ticket.getBaggageType());
        if (ticket.getUser() == null) {
            ticketModel.setUserId(null);
        } else {
            ticketModel.setUserId(ticket.getUser().getId());
        }
        return ticketModel;
    }

    @Override
    public List<Ticket> getTicketsByBookingId(int bookngId) {
        return ticketRepository.findByBookingId(bookngId);
    }
}
