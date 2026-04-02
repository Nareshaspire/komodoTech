package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestActivityInfoItem implements Serializable {

    String type = "";
    String user_id = "";
    String access_token = "";
    String rendom_number = "";
    ArrayList<VoRequestActivityItem> activity_details;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRendom_number() {
        return rendom_number;
    }

    public void setRendom_number(String rendom_number) {
        this.rendom_number = rendom_number;
    }

    public ArrayList<VoRequestActivityItem> getActivity_details() {
        return activity_details;
    }

    public void setActivity_details(ArrayList<VoRequestActivityItem> activity_details) {
        this.activity_details = activity_details;
    }
}
