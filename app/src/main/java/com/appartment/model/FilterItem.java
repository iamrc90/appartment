package com.appartment.model;

import com.appartment.model.enums.TicketType;

/**
 * Created by Sumit on 11/19/2016.
 */

public class FilterItem {

    String headerName, name, id ;

    TicketType ticketType;

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }
}
