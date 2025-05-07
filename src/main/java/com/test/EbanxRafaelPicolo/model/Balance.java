package com.test.EbanxRafaelPicolo.model;

import java.math.BigDecimal;


public class Balance {
    private final int id;
    private BigDecimal balance;

    public Balance(int id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal balance) {
        this.balance = this.balance.add(balance);
    }

    public void withdraw(BigDecimal balance){
        this.balance = this.balance.subtract(balance);
    }
}
