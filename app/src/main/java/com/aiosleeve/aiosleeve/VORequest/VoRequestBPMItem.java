package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestBPMItem implements Serializable {

    ArrayList<VoRequestActivityInfoItem> activity_details_info;

    public ArrayList<VoRequestActivityInfoItem> getActivity_details_info() {
        return activity_details_info;
    }

    public void setActivity_details_info(ArrayList<VoRequestActivityInfoItem> activity_details_info) {
        this.activity_details_info = activity_details_info;
    }
}