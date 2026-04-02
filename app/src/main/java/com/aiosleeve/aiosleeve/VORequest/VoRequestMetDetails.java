package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestMetDetails implements Serializable {

    String met_value = "";
    String date_time = "";

    String met_avg="";//2021
    String activity="";//2021 - For activity_type

    public String getMet_avg() {
        return met_avg;
    }

    public void setMet_avg(String met_avg) {
        this.met_avg = met_avg;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getMet_value() {
        return met_value;
    }

    public void setMet_value(String met_value) {
        this.met_value = met_value;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}