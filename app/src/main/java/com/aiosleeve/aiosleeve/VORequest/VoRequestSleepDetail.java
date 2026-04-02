package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestSleepDetail implements Serializable {

    String sleep_differenceTime = "";
    String total_sleep_time = "";
    String sleep_value = "";
    String rendom_number = "";
    String end_time = "";
    String date_data = "";
    String start_time = "";

    public String getSleep_differenceTime() {
        return sleep_differenceTime;
    }

    public void setSleep_differenceTime(String sleep_differenceTime) {
        this.sleep_differenceTime = sleep_differenceTime;
    }

    public String getTotal_sleep_time() {
        return total_sleep_time;
    }

    public void setTotal_sleep_time(String total_sleep_time) {
        this.total_sleep_time = total_sleep_time;
    }

    public String getSleep_value() {
        return sleep_value;
    }

    public void setSleep_value(String sleep_value) {
        this.sleep_value = sleep_value;
    }

    public String getRendom_number() {
        return rendom_number;
    }

    public void setRendom_number(String rendom_number) {
        this.rendom_number = rendom_number;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDate_data() {
        return date_data;
    }

    public void setDate_data(String date_data) {
        this.date_data = date_data;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
}