package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 29/12/17.
 */

public class VOBPMTableDataItems implements Serializable {
    private String id;

    private String end_time;

    private String total_sleep_time;

    private String date_data;

    private String sleep_differencetime;

    private String start_time;

    private String user_id;

    private String rendom_number;

    private String sleep_value;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getEnd_time ()
    {
        return end_time;
    }

    public void setEnd_time (String end_time)
    {
        this.end_time = end_time;
    }

    public String getTotal_sleep_time ()
    {
        return total_sleep_time;
    }

    public void setTotal_sleep_time (String total_sleep_time)
    {
        this.total_sleep_time = total_sleep_time;
    }

    public String getDate_data ()
    {
        return date_data;
    }

    public void setDate_data (String date_data)
    {
        this.date_data = date_data;
    }

    public String getSleep_differencetime ()
    {
        return sleep_differencetime;
    }

    public void setSleep_differencetime (String sleep_differencetime)
    {
        this.sleep_differencetime = sleep_differencetime;
    }

    public String getStart_time ()
    {
        return start_time;
    }

    public void setStart_time (String start_time)
    {
        this.start_time = start_time;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getRendom_number ()
    {
        return rendom_number;
    }

    public void setRendom_number (String rendom_number)
    {
        this.rendom_number = rendom_number;
    }

    public String getSleep_value ()
    {
        return sleep_value;
    }

    public void setSleep_value (String sleep_value)
    {
        this.sleep_value = sleep_value;
    }
}
