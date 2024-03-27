package ru.vlsu.airline.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.airline.dto.BoardingPassModel;
import ru.vlsu.airline.dto.PassengerModel;
import ru.vlsu.airline.dto.PaymentModel;
import ru.vlsu.airline.dto.TicketModel;
import ru.vlsu.airline.entities.*;
import ru.vlsu.airline.repositories.*;
import ru.vlsu.airline.statemachine.model.TicketState;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private UserRepository userRepository;



    @Override
    @Transactional
    public TicketModel buyTicket(PassengerModel passenger, int flightId, int seatId, User user) {
        Passenger newPassenger = new Passenger(passenger);
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isPresent()) {
            Flight existFlight = flight.get();
            logger.info(existFlight.getStatus());
            Optional<Flight_seat> seat = seatRepository.findById(seatId);
            if(seat.isPresent() && seat.get().getStatus().equals("available")){
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
    public boolean makePayment(int ticketId, PaymentModel paymentInfo) {
        return true;
    }

    @Override
    @Transactional
    public TicketModel reserveTicket(int ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if(ticket.isPresent()){
            Ticket existTicket = ticket.get();
            if(existTicket.getStatus().equals("UNPAID")){
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
    public int cancelBooking(int bookingId) {
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
    public byte[] generateBoardingPass(BoardingPassModel model) throws IOException {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream templateStream = new ClassPathResource("templates/template_bp.pdf").getInputStream()){
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
    }

    @Override
    public byte[] generateTicket(BoardingPassModel model){
        return new byte[0];
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

    public TicketModel convertToTicketModel(Ticket ticket){
        TicketModel ticketModel = new TicketModel();
        ticketModel.setCode(ticket.getCode());
        ticketModel.setPassengerId(ticket.getPassenger().getId());
        ticketModel.setFlightId(ticket.getFlight().getId());
        ticketModel.setSeatId(ticket.getSeat().getId());
        ticketModel.setDateOfPurchase(ticket.getDateOfPurchase());
        if(ticket.getBooking() == null){
            ticketModel.setBookingId(null);
        }else{
            ticketModel.setBookingId(ticket.getBooking().getId());
        }
        ticketModel.setStatus(ticket.getStatus());
        ticketModel.setBaggageType(ticket.getBaggageType());
        if(ticket.getUser() == null){
            ticketModel.setUserId(null);
        }else{
            ticketModel.setUserId(ticket.getUser().getId());
        }
        return ticketModel;
    }
}
