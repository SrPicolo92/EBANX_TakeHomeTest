package com.test.EbanxRafaelPicolo.model;

import java.math.BigDecimal;

public class EventRequest {
    private String type;
    private String destination;
    private String origin;
    private BigDecimal amount;

    public String getType() {
        return type;
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
