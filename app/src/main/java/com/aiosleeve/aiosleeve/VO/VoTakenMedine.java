package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 1/3/18.
 */

public class VoTakenMedine implements Serializable {
    private String mID = "";
    private String DateNTIme = "";
    private String Date = "";
    private String UserId = "";
    private String medicine_name = "";
    private boolean isChecked;

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getDateNTIme() {
        return DateNTIme;
    }

    public void setDateNTIme(String dateNTIme) {
        DateNTIme = dateNTIme;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
