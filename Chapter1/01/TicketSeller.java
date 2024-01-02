package org.example.ticket;

import org.example.service.Audience;
public class TicketSeller {
    private TicketOffice ticketOffice;
    public TicketSeller(TicketOffice ticketOffice){
        this.ticketOffice=ticketOffice;
    }

    public TicketOffice getTicketOffice(){
        return ticketOffice;
    }
}