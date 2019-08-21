package com.canghai.bean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Messages {
    private String event;//视频流专用字段
    private Object data;//视频流专用字段
    private String type;//数据类型/* type:text文本  file文件 video视频 audio音频 mic语音 image图片*/
    private String stat;//状态
    private String ufrom;//来源
    private String uaimd;//目标对象
    private String msg;//文本消息体
    private Double latitude;
    private Double longitude;
    private Date time;//时间
    private List<User> users;
    public String getStat() {
        return stat;
    }
    public void setStat(String stat) {
        this.stat = stat;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUfrom() {
        return ufrom;
    }
    public void setUfrom(String ufrom) {
        this.ufrom = ufrom;
    }
    public String getUaimd() {
        return uaimd;
    }
    public void setUaimd(String uaimd) {
        this.uaimd = uaimd;
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

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return "Message{" +
                "event='" + event + '\'' +
                ", data=" + data +
                ", type='" + type + '\'' +
                ", stat='" + stat + '\'' +
                ", ufrom='" + ufrom + '\'' +
                ", uaimd='" + uaimd + '\'' +
                ", msg='" + msg + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", users=" + users +
                '}';
    }
}
