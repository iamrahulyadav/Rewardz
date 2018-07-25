package com.letswecode.harsha.rewardz.modal;

public class Transactions {

  private   String coupon_code,
     expires_on,
      points,
   publisher_name,
   time_stamp,
      user_id;

    public Transactions(String coupon_code, String expires_on, String points, String publisher_name, String time_stamp, String user_id) {
        this.coupon_code = coupon_code;
        this.expires_on = expires_on;
        this.points = points;
        this.publisher_name = publisher_name;
        this.time_stamp = time_stamp;
        this.user_id = user_id;
    }

    public Transactions() {
    }


    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getExpires_on() {
        return expires_on;
    }

    public void setExpires_on(String expires_on) {
        this.expires_on = expires_on;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



}
