package ru.vlsu.airline.services;

import org.springframework.stereotype.Service;

@Service
public class TicketService implements ITicketService{
    public String getTicket(){
        return "this is ticket";
    }
}
