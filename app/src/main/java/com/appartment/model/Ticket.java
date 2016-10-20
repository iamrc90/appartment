package com.appartment.model;

import java.io.Serializable;

/**
 * Created by ripulchhabra on 27/09/16.
 */
public class Ticket implements Serializable{
    private String priority;
    private String ticketNumber;
    private String ticketDate;
    private String summary;
    private String address;

    public Ticket(String priority, String ticketNumber, String ticketDate, String summary, String address) {
        this.priority = priority;
        this.ticketNumber = ticketNumber;
        this.ticketDate = ticketDate;
        this.summary = summary;
        this.address = address;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
