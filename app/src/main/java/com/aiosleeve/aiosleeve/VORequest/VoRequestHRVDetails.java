package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestHRVDetails implements Serializable {

    String hrv_value = "";
    String date_time = "";

    String event="";//2021- For event_type
    String event_comment="";//2021
    String hrv_avg="";//2021

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent_comment() {
        return event_comment;
    }

    public void setEvent_comment(String event_comment) {
        this.event_comment = event_comment;
    }

    public String getHrv_avg() {
        return hrv_avg;
    }

    public void setHrv_avg(String hrv_avg) {
        this.hrv_avg = hrv_avg;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getHrv_value() {
        return hrv_value;
    }

    public void setHrv_value(String hrv_value) {
        this.hrv_value = hrv_value;
    }
}