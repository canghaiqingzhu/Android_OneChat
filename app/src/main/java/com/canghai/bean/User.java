package com.canghai.bean;

/**
 * Created by dell on 2018/12/28.
 */

public class User {
    private String name;//姓名
    private String uuid;//id
    private String img;//头像
    private Character sex;//性别
    private String phone;//电话
    private String motto;//介绍
    private int msgNum;//消息数

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", img='" + img + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", motto='" + motto + '\'' +
                ", msgNum=" + msgNum +
                '}';
    }
}
