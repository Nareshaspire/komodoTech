package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oneclickpc001 on 29/1/18.
 */

public class VoResponceGoogleAPI implements Serializable {

    List<VoResponceGoogleAPIResult> results;

    public List<VoResponceGoogleAPIResult> getResults() {
        return results;
    }

    public void setResults(List<VoResponceGoogleAPIResult> results) {
        this.results = results;
    }
}
