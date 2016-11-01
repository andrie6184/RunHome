package com.runnerfun.beans;

import java.io.Serializable;

/**
 * CoinSummary
 * Created by andrie on 16/11/1.
 */

public class CoinSummary implements Serializable {

    private String amount;
    private String canGive;
    private String overdue;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCanGive() {
        return canGive;
    }

    public void setCanGive(String canGive) {
        this.canGive = canGive;
    }

    public String getOverdue() {
        return overdue;
    }

    public void setOverdue(String overdue) {
        this.overdue = overdue;
    }
}
