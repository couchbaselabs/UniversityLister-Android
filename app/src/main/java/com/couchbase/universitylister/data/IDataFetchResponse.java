package com.couchbase.universitylister.data;

import com.couchbase.universitylister.model.University;

import java.util.List;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

public interface IDataFetchResponse {
    void postResult(List<University> jsonData);
}
