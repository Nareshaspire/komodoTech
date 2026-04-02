package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 25/12/17.
 */

public class VOSignUp implements Serializable{
    private String message;

    private VOSignUpData data;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public VOSignUpData getData ()
    {
        return data;
    }

    public void setData (VOSignUpData data)
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
