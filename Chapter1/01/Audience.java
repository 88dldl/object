package org.example.service;

import org.example.ticket.Ticket;

public class Audience {
    private Bag bag;
    public Audience(Bag bag){
        this.bag=bag;
    }
    public Bag getBag(){
        return bag;
    }
}