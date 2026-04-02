package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class VoBPMListData implements Serializable {

    String bpm_id = "";
    String bpm_value = "";
    String date_time = "";
    String start_time = "";
    String end_time = "";
    String total_time = "";
    String date = "";
    String end_date_time = "";
    String start_date_time = "";

    public String getBpm_id() {
        return bpm_id;
    }

    public void setBpm_id(String bpm_id) {
        this.bpm_id = bpm_id;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
    }

    public String getStart_date_time() {
        return start_date_time;
    }

    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }
}
