package com.appartment.model;

/**
 * Created by sumit on 4/10/16.
 */

public class Filter {

    public int sectionFirstPosition;

    public boolean isHeader;

    public Object objItem;

    public Filter(int sectionFirstPosition, boolean isHeader, Object objItem) {
        this.sectionFirstPosition = sectionFirstPosition;
        this.isHeader = isHeader;
        this.objItem = objItem;
    }
}
