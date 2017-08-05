package com.couchbase.universitylister.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;


import com.couchbase.universitylister.model.University;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

// Once could replace this implementation to load data from remote server
public class DataFetcher extends AsyncTask<Void,Void,List<University>> {
    private Context mContext;
    private IDataFetchResponse mDelegate = null;

    public DataFetcher(Context context,IDataFetchResponse delegate) {
        mContext = context;
        mDelegate = delegate;
    }



    @Override
    protected List<University> doInBackground(Void... voids) {
        String fileName = "university_sample.txt";
        StringBuilder stringBuilder = new StringBuilder();
        List<University> universities = null;
        try {
            // Load data from local sample data file
            InputStream inputStream = mContext.getAssets().open(fileName);
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            universities = Arrays.asList(mapper.readValue(inputStream, University[].class));

            return universities;
        } catch (IOException  e ) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    protected void onPostExecute(List<University> result) {
        mDelegate.postResult(result);

    }
}
