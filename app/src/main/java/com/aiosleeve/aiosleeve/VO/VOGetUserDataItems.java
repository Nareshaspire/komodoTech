package com.aiosleeve.aiosleeve.VO;

import java.io.Serializable;

/**
 * Created by oneclick-android on 28/12/17.
 */

public class VOGetUserDataItems implements Serializable {
    private String created_date;

    private String lon;

    private String weight;

    private String height;

    private String is_active;

    private String contact_no;

    private String email;

    private String fk_role_id;

    private String dob;

    private String name;

    private String gender;

    private String user_id;

    private String town;

    private String photo;

    private String lat;

    private String medication = "";
    private String heart_condition = "";

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getLon ()
    {
        return lon;
    }

    public void setLon (String lon)
    {
        this.lon = lon;
    }

    public String getWeight ()
    {
        return weight;
    }

    public void setWeight (String weight)
    {
        this.weight = weight;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getIs_active ()
    {
        return is_active;
    }

    public void setIs_active (String is_active)
    {
        this.is_active = is_active;
    }

    public String getContact_no ()
    {
        return contact_no;
    }

    public void setContact_no (String contact_no)
    {
        this.contact_no = contact_no;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getFk_role_id ()
    {
        return fk_role_id;
    }

    public void setFk_role_id (String fk_role_id)
    {
        this.fk_role_id = fk_role_id;
    }

    public String getDob ()
    {
        return dob;
    }

    public void setDob (String dob)
    {
        this.dob = dob;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getGender ()
    {
        return gender;
    }

    public void setGender (String gender)
    {
        this.gender = gender;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getTown () {
        return town;
    }

    public void setTown (String town) {
        this.town = town;
    }

    public String getPhoto () {
        return photo;
    }

    public void setPhoto (String photo) {
        this.photo = photo;
    }

    public String getLat () {
        return lat;
    }

    public void setLat (String lat) {
        this.lat = lat;
    }

    public String getMedactions() {
        return medication;
    }

    public void setMedactions(String medication) {
        this.medication = medication;
    }

    public String getHeart_condition() {
        return heart_condition;
    }

    public void setHeart_condition(String heart_condition) {
        this.heart_condition = heart_condition;
    }
}
