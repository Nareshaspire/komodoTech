package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 21/12/17.
 */

public class VOLogin implements Serializable {
    private String message;

    private VOLoginData data;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public VOLoginData getData ()
    {
        return data;
    }

    public void setData (VOLoginData data)
    {
        this.data = data;
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
