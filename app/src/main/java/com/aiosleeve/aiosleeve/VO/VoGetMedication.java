package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oneclickpc001 on 18/5/18.
 */

public class VoGetMedication implements Serializable {

    String success = "";
    String message = "";

    List<VoGetData> data = new ArrayList<>();

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

    public List<VoGetData> getData() {
        return data;
    }

    public void setData(List<VoGetData> data) {
        this.data = data;
    }

    public class VoGetData implements Serializable {
        String medactions = "";

        public String getMedactions() {
            return medactions;
        }

        public void setMedactions(String medactions) {
            this.medactions = medactions;
        }
    }
}
