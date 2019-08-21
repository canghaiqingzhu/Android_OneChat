package com.canghai.bean.wswrtc;

/**
 * Created by dell on 2018/12/28.
 */

public class WsUser {
    private String name;//姓名
    private String uuid;//id
    private Double latitude;//lat
    private Double longitude;//lng
    private String markImg;
    //所登录操作系统
    private boolean isPclogin = false;
    private boolean isPhonelogin = false;
    //手机是否直播
    private boolean isPhoneVideo = false;
    public void setCoordinate(Double latitude,Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMarkImg() {
        return markImg;
    }

    public boolean isPclogin() {
        return isPclogin;
    }

    public void setPclogin(boolean pclogin) {
        isPclogin = pclogin;
    }

    public boolean isPhonelogin() {
        return isPhonelogin;
    }

    public void setPhonelogin(boolean phonelogin) {
        isPhonelogin = phonelogin;
    }

    public void setMarkImg(String markImg) {
        this.markImg = markImg;
    }

    public boolean isPhoneVideo() {
        return isPhoneVideo;
    }

    public void setPhoneVideo(boolean phoneVideo) {
        isPhoneVideo = phoneVideo;
    }
}
