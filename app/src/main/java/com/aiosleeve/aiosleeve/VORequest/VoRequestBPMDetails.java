package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestBPMDetails implements Serializable {

    String bpm_value = "";
    String date_time = "";

    String max_bpm="";//2021
    String bpm_avg="";//2021

    public String getMax_bpm() {
        return max_bpm;
    }

    public void setMax_bpm(String max_bpm) {
        this.max_bpm = max_bpm;
    }

    public String getBpm_avg() {
        return bpm_avg;
    }

    public void setBpm_avg(String bpm_avg) {
        this.bpm_avg = bpm_avg;
    }

    public String getBpm_value() {
        return bpm_value;
    }

    public void setBpm_value(String bpm_value) {
        this.bpm_value = bpm_value;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}