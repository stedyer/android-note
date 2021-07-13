package com.shixun.zz_shixun01;

import java.util.Date;

public class ChatMessage {
    private String msg;
    private Date date;
    private Type type;

    public ChatMessage() {
    }

    public ChatMessage(String msg, Date date, Type type) {
        this.msg = msg;
        this.date = date;
        this.type = type;
    }

    //表示消息的接收和发送
    public enum Type {
        INCOMIMG, OUTCOMIMG
    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}