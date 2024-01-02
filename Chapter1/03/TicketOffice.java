package org.example;

import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;

public class TicketOffice {
    private Long amount;
    private List<Ticket> tickets=new ArrayList<>();

    public TicketOffice(Long amount,Ticket ... tickets){
        this.amount=amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }
    private Ticket getTicket(){
        return tickets.remove(0);
    }

    private void plusAmount(Long amount){
        this.amount+=amount;
    }
    public void sellTicketTo(Audience audience){
        plusAmount(audience.buy(getTicket()));

    }
}