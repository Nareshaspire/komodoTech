package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclickpc001 on 19/5/18.
 */

public class VoMedicationTaken implements Serializable {

    String success = "";
    String message = "";

    VoMedicationTakenData data;

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

    public VoMedicationTakenData getData() {
        return data;
    }

    public void setData(VoMedicationTakenData data) {
        this.data = data;
    }

    public class VoMedicationTakenData implements Serializable {

        String user_id = "";
        String medication_name = "";
        String medication_time = "";
        String access_token = "";

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getMedication_name() {
            return medication_name;
        }

        public void setMedication_name(String medication_name) {
            this.medication_name = medication_name;
        }

        public String getMedication_time() {
            return medication_time;
        }

        public void setMedication_time(String medication_time) {
            this.medication_time = medication_time;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }
}
