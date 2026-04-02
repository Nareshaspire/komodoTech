package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by android on 1/20/18.
 */

public class VoResponseBPMSync implements Serializable {

    String success = "";
    String message = "";

    ArrayList<String> rendom_number;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getRendom_number() {
        return rendom_number;
    }

    public void setRendom_number(ArrayList<String> rendom_number) {
        this.rendom_number = rendom_number;
    }
}
