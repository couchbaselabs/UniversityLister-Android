package com.couchbase.universitylister.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

// Once could replace this implementation to load data from remote server
public class DataFetcher extends AsyncTask<Void,Void,JSONArray> {
    private Context mContext;
    private IDataFetchResponse mDelegate = null;

    public DataFetcher(Context context,IDataFetchResponse delegate) {
        mContext = context;
        mDelegate = delegate;
    }



    @Override
    protected JSONArray doInBackground(Void... voids) {
        String fileName = "university_sample.txt";
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonData = new JSONArray();
        try {
            // Load data from local sample data file
            InputStream is = mContext.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();

            // Convert to JSON
            jsonData = new JSONArray(stringBuilder.toString());
        } catch (IOException | JSONException e ) {
            e.printStackTrace();
            return null;
        }
        return jsonData;

    }

    @Override
    protected void onPostExecute(JSONArray result) {
        mDelegate.postResult(result);

    }
}
