package com.runnerfun.beans;

import java.io.Serializable;

/**
 * Created by lixiaoyang on 04/10/2016.
 */

public class UserInfo implements Serializable{
    public boolean isLogin;
    public String cellPhoneNumber;
    public int age = -1;
    public int height = -1;
    public int sex = -1;//0 male, 1 female
    public long userId;
    public String userName;
    public long money;
}
