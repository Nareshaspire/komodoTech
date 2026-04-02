package com.aiosleeve.aiosleeve.VO;

import android.util.Log;

import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.fragments.FragmentActivityMode.ActivityEvent;

import java.io.Serializable;

/**
 * Created by oneclickpc001 on 18/1/18.
 */

public class VoActivityHistoryListData implements Serializable {

    String bpm_id = "";
    String bpm_value = "";
    String date_time = "";
    String start_time = "";
    String end_time = "";
    String total_time = "";
    String date = "";
    //2021
    String average_bpm_value = "";
    String max_bpm_value = "";
    String activity_type = "";
    String met_value = "";

    String activityImage=ActivityEvent.RUNNING.getEmoji();

    public String getActivityImage() {
        return activityImage;
    }

    private void setActivityImage(String activityEvent) {
        if(activityEvent.equalsIgnoreCase(ActivityEvent.RUNNING.getValue())){
            this.activityImage=ActivityEvent.RUNNING.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.CYCLING.getValue())){
            this.activityImage=ActivityEvent.CYCLING.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.WALKING.getValue())){
            this.activityImage=ActivityEvent.WALKING.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.WORKOUT.getValue())){
            this.activityImage=ActivityEvent.WORKOUT.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.GOLF.getValue())){
            this.activityImage=ActivityEvent.GOLF.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.HIKING.getValue())){
            this.activityImage=ActivityEvent.HIKING.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.YOGA.getValue())){
            this.activityImage=ActivityEvent.YOGA.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.GARDENING.getValue())){
            this.activityImage=ActivityEvent.GARDENING.getEmoji();
        }
        else if(activityEvent.equalsIgnoreCase(ActivityEvent.TENNIS.getValue())){
            this.activityImage=ActivityEvent.TENNIS.getEmoji();
        }else if(activityEvent.equalsIgnoreCase(ActivityEvent.WORK_SHIFT.getValue())){
            this.activityImage=ActivityEvent.WORK_SHIFT.getEmoji();
        }else if(activityEvent.equalsIgnoreCase(ActivityEvent.OTHER.getValue())){
            this.activityImage=ActivityEvent.OTHER.getEmoji();
        }else if(activityEvent.trim().isEmpty()){
            this.activityImage=ActivityEvent.RUNNING.getEmoji();
        }else{
            Log.e("VoActivityHistoryData","setActivityImage()- Getting value which is not in the enum.");
            this.activityImage=ActivityEvent.RUNNING.getEmoji();
        }
    }

    boolean isExpanded=false;

    public boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
        setActivityImage(activity_type);
    }

    public String getMet_value() {
        return met_value;
    }

    public void setMet_value(String met_value) {
        this.met_value = met_value;
    }

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

    public String getAverage_bpm_value() {
        return average_bpm_value;
    }

    public void setAverage_bpm_value(String average_bpm_value) {
        this.average_bpm_value = average_bpm_value;
    }

    public String getMax_bpm_value() {
        return max_bpm_value;
    }

    public void setMax_bpm_value(String max_bpm_value) {
        this.max_bpm_value = max_bpm_value;
    }
}
