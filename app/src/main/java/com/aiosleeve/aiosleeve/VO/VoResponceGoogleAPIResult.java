package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oneclickpc001 on 29/1/18.
 */

public class VoResponceGoogleAPIResult implements Serializable {

    Geometry geometry;

    String id = "";
    String name = "";
    String vicinity = "";

    List<String> types = new ArrayList<>();

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public class Geometry implements Serializable {
        Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public class Location implements Serializable {

        Double lat;
        Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }
}
