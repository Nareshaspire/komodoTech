package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestActivityItem implements Serializable {

    ArrayList<VoRequestTimeDetails> time_details;
    ArrayList<VoRequestBPMDetails> bpm_details;
    ArrayList<VoRequestDistanceDetails> distance_details;
    ArrayList<VoRequestMetDetails> met_details;
    ArrayList<VoRequestSPO2Details> spo2_details;
    ArrayList<VoRequestHRVDetails> hrv_details;
    ArrayList<VoRequestECGDetails> ecg_details;

    String end_time = "";
    String total_time = "";
    String start_time = "";
    String date = "";
    ArrayList<VoRequestStepsDetails> steps_details;

    public ArrayList<VoRequestTimeDetails> getTime_details() {
        return time_details;
    }

    public void setTime_details(ArrayList<VoRequestTimeDetails> time_details) {
        this.time_details = time_details;
    }

    public ArrayList<VoRequestBPMDetails> getBpm_details() {
        return bpm_details;
    }

    public void setBpm_details(ArrayList<VoRequestBPMDetails> bpm_details) {
        this.bpm_details = bpm_details;
    }

    public ArrayList<VoRequestDistanceDetails> getDistance_details() {
        return distance_details;
    }

    public void setDistance_details(ArrayList<VoRequestDistanceDetails> distance_details) {
        this.distance_details = distance_details;
    }

    public ArrayList<VoRequestMetDetails> getMet_details() {
        return met_details;
    }

    public void setMet_details(ArrayList<VoRequestMetDetails> met_details) {
        this.met_details = met_details;
    }

    public ArrayList<VoRequestSPO2Details> getSpo2_details() {
        return spo2_details;
    }

    public void setSpo2_details(ArrayList<VoRequestSPO2Details> spo2_details) {
        this.spo2_details = spo2_details;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<VoRequestStepsDetails> getSteps_details() {
        return steps_details;
    }

    public void setSteps_details(ArrayList<VoRequestStepsDetails> steps_details) {
        this.steps_details = steps_details;
    }

    public ArrayList<VoRequestHRVDetails> getHrv_details() {
        return hrv_details;
    }

    public void setHrv_details(ArrayList<VoRequestHRVDetails> hrv_details) {
        this.hrv_details = hrv_details;
    }

    public ArrayList<VoRequestECGDetails> getEcg_details() {
        return ecg_details;
    }

    public void setEcg_details(ArrayList<VoRequestECGDetails> ecg_details) {
        this.ecg_details = ecg_details;
    }
}