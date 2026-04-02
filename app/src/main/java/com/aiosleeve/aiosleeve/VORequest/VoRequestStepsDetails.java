package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestStepsDetails implements Serializable {

    String date_time = "";
    String total_steps = "";
    String current_steps = "";

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getTotal_steps() {
        return total_steps;
    }

    public void setTotal_steps(String total_steps) {
        this.total_steps = total_steps;
    }

    public String getCurrent_steps() {
        return current_steps;
    }

    public void setCurrent_steps(String current_steps) {
        this.current_steps = current_steps;
    }
}