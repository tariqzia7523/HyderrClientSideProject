package com.map.hyderrclientsideproject;

import java.io.Serializable;

public class HistoryModel implements Serializable {
    String id;
    String userID;
    String resturentid;
    String diliverymanId;
    String price;
    String date;

    public HistoryModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getResturentid() {
        return resturentid;
    }

    public void setResturentid(String resturentid) {
        this.resturentid = resturentid;
    }

    public String getDiliverymanId() {
        return diliverymanId;
    }

    public void setDiliverymanId(String diliverymanId) {
        this.diliverymanId = diliverymanId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
