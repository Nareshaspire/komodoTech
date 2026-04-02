package com.aiosleeve.aiosleeve.VORequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 1/20/18.
 */

public class VoRequestSleepItem implements Serializable {


    ArrayList<VoRequestSleepDetail> sleepdata = new ArrayList<>();

    public ArrayList<VoRequestSleepDetail> getSleepdata() {
        return sleepdata;
    }

    public void setSleepdata(ArrayList<VoRequestSleepDetail> sleepdata) {
        this.sleepdata = sleepdata;
    }
}