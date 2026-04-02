package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestECGDetails implements Serializable {

    String ecg_start_time = "";
    String ecg_end_time = "";
    String ecg_values = "";

    public String getEcg_start_time() {
        return ecg_start_time;
    }

    public void setEcg_start_time(String ecg_start_time) {
        this.ecg_start_time = ecg_start_time;
    }

    public String getEcg_end_time() {
        return ecg_end_time;
    }

    public void setEcg_end_time(String ecg_end_time) {
        this.ecg_end_time = ecg_end_time;
    }

    String hrv_value = "";//2021
    String hrv_avg="";//2021 For average_hrv

    public String getHrv_value() {
        return hrv_value;
    }

    public void setHrv_value(String hrv_value) {
        this.hrv_value = hrv_value;
    }

    public String getHrv_avg() {
        return hrv_avg;
    }

    public void setHrv_avg(String hrv_avg) {
        this.hrv_avg = hrv_avg;
    }



    public String getEcg_values() {
        return ecg_values;
    }

    public void setEcg_values(String ecg_values) {
        this.ecg_values = ecg_values;
    }
}