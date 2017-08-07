package com.couchbase.universitylister.data;

import android.content.Context;
import android.os.AsyncTask;

import com.couchbase.universitylister.model.University;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
            // 1. Load data from local sample data file
            InputStream inputStream = mContext.getAssets().open(fileName);
            // 2. use Jackson library to map the JSON to List of University POJO
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
        // 3. Notify the IDataFetchResponse delegate (which in this case is ListActivity) of the availability of data
        mDelegate.postResult(result);

    }
}
