package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by andrie on 16/12/9.
 */

public class RunSaveResultBean implements Serializable {

    private String id;
    private String coin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
