package org.example.theater;

import org.example.service.Audience;
import org.example.ticket.Ticket;
import org.example.ticket.TicketSeller;

public class Theater {
    private TicketSeller ticketSeller;

    public Theater(TicketSeller ticketSeller){
        this.ticketSeller=ticketSeller;
    }
    public void enter(Audience audience){
        if(audience.getBag().hasInvitation()){ //초대장이 있는 경우
            Ticket ticket=ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket); //가방에 티켓 넣어줌
        }
        else{
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().minusAmount(ticket.getFee()); // 돈 지불
            ticketSeller.getTicketOffice().plusAmount(ticket.getFee()); // 셀러 입장에서는 돈을 벌었음
            audience.getBag().setTicket(ticket); // 티켓 넣어줌
        }
    }
}