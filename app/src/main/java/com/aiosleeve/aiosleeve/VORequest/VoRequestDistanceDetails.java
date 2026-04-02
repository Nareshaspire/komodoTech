package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestDistanceDetails implements Serializable {

    String total_distance = "";
    String current_distance = "";
    String date_time = "";

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getCurrent_distance() {
        return current_distance;
    }

    public void setCurrent_distance(String current_distance) {
        this.current_distance = current_distance;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}