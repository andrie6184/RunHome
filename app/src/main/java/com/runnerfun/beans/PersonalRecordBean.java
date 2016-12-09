package com.runnerfun.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by andrie on 16/12/9.
 */

public class PersonalRecordBean implements Serializable {

    private String cnt;
    private ArrayList<RunRecordBean> list;

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public ArrayList<RunRecordBean> getList() {
        return list;
    }

    public void setList(ArrayList<RunRecordBean> list) {
        this.list = list;
    }
}
