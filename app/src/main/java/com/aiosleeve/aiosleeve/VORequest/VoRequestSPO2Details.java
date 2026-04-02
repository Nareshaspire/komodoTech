package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestSPO2Details implements Serializable {

    String spo2_value = "";
    String date_time = "";

    public String getSpo2_value() {
        return spo2_value;
    }

    public void setSpo2_value(String spo2_value) {
        this.spo2_value = spo2_value;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}