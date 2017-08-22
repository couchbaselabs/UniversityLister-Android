package com.couchbase.universitylister.model;

import java.util.List;

public class University {
    private String name;
    private  String country;

    public University() {
        //
    }
    public String getName() {
        return name;

    }
    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;

    }
    public void setCountry(String country) {
        this.country = country;
    }
}
