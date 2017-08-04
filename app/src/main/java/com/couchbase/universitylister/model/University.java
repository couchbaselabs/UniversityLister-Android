package com.couchbase.universitylister.model;

/**
 * Created by priya.rajagopal on 8/3/17.
 */

public class University {
    private String mName;
    private String mCountry;

    public University(String name, String country) {
        mName = name;
        mCountry = country;
    }
    public String getName() {
        return mName;

    }
    public String getCountry() {
        return mCountry;

    }
}
