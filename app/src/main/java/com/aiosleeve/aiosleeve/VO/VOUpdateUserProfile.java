package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oneclick-android on 29/12/17.
 */

public class VOUpdateUserProfile implements Serializable {
    private String message;

    private List<VOUpdateUserProfileItems> userData;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public List<VOUpdateUserProfileItems> getUserData ()
    {
        return userData;
    }

    public void setUserData (List<VOUpdateUserProfileItems> userData)
    {
        this.userData = userData;
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
