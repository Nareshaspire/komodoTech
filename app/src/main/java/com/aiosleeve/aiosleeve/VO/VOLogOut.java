package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 21/12/17.
 */

public class VOLogOut implements Serializable {

    private String message;
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

}
