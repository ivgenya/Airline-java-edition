package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.airline.services.ITicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private ITicketService ticketService;

    @GetMapping("/get")
    public String getLots() {
        return ticketService.getTicket();
    }
}
