package com.appartment.model;

/**
 * Created by sumit on 4/10/16.
 */

public class FilterHeader {

    private String eventHeaderTitle;

    public String getEventHeaderTitle() {
        return eventHeaderTitle;
    }

    public void setEventHeaderTitle(String eventHeaderTitle) {
        this.eventHeaderTitle = eventHeaderTitle;
    }

    public boolean equalsTo(FilterHeader obj) {
        return this.eventHeaderTitle.equals(obj.eventHeaderTitle);
    }
}
