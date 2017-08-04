package com.couchbase.universitylister.data;

import org.json.JSONArray;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

public interface IDataFetchResponse {
    void postResult(JSONArray jsonData);
}
