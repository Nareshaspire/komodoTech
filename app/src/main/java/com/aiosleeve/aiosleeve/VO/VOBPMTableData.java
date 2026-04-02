package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oneclick-android on 29/12/17.
 */

public class VOBPMTableData implements Serializable {
    private String message;

    private List<VOBPMTableDataItems> data;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public List<VOBPMTableDataItems> getData ()
    {
        return data;
    }

    public void setData (List<VOBPMTableDataItems> data)
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
