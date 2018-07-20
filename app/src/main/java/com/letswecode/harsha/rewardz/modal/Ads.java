package com.letswecode.harsha.rewardz.modal;

public class Ads {

    String ad_banner;
    String ad_type;
    String ad_description;
    String ad_url;
    String city;
    String created_on;
    String expires_on;
    String points;
    String publisher_image;
    String publisher_name;
    String video_url;
    String coupon_code;

    public Ads(String ad_banner, String ad_type, String ad_description, String ad_url, String city, String created_on, String expires_on, String points, String publisher_image, String publisher_name, String video_url, String coupon_code) {
        this.ad_banner = ad_banner;
        this.ad_type = ad_type;
        this.ad_description = ad_description;
        this.ad_url = ad_url;
        this.city = city;
        this.created_on = created_on;
        this.expires_on = expires_on;
        this.points = points;
        this.publisher_image = publisher_image;
        this.publisher_name = publisher_name;
        this.video_url = video_url;
        this.coupon_code = coupon_code;
    }



    public Ads() {
    }




    public String getAd_banner() {
        return ad_banner;
    }

    public void setAd_banner(String ad_banner) {
        this.ad_banner = ad_banner;
    }

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
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

    public String getPublisher_image() {
        return publisher_image;
    }

    public void setPublisher_image(String publisher_image) {
        this.publisher_image = publisher_image;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getAd_description() {
        return ad_description;
    }

    public void setAd_description(String ad_description) {
        this.ad_description = ad_description;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getCoupon_code(){
        return coupon_code;
    }
    public void setCoupon_code(String coupon_code){
        this.coupon_code = coupon_code;
    }

}
