package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oneclick-android on 28/12/17.
 */

public class VOGetUserData implements Serializable {
    private String message;

    private List<VOGetUserDataItems> userdata;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public List<VOGetUserDataItems> getUserdata ()
    {
        return userdata;
    }

    public void setUserdata (List<VOGetUserDataItems> userdata)
    {
        this.userdata = userdata;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }
}
