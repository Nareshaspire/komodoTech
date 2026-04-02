package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oneclick-android on 29/12/17.
 */

public class VOSleepSyncDataToServer implements Serializable {

    private String message;

    private List<String> rendom_number = new ArrayList<>();

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }

    public List<String> getRendom_number() {
        return rendom_number;
    }

    public void setRendom_number(List<String> rendom_number) {
        this.rendom_number = rendom_number;
    }
}
