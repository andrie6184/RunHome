package com.runnerfun.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CoinBean
 * Created by andrie on 16/11/1.
 */

public class CoinBean implements Serializable {

    private ArrayList<Coin> list;
    private CoinSummary summary;
    private String cnt;

    public List<Coin> getList() {
        return list;
    }

    public void setList(ArrayList<Coin> list) {
        this.list = list;
    }

    public CoinSummary getSummary() {
        return summary;
    }

    public void setSummary(CoinSummary summary) {
        this.summary = summary;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

}
