package com.couchbase.universitylister.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.couchbase.universitylister.R;
import com.couchbase.universitylister.adapter.UniversityListAdapter;
import com.couchbase.universitylister.data.DataFetcher;
import com.couchbase.universitylister.data.IDataFetchResponse;
import com.couchbase.universitylister.model.University;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements IDataFetchResponse {

    private JSONArray sampleData;
    private final UniversityListAdapter adapter = new UniversityListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvUniversities);


        // Load the data from local sample file
        DataFetcher fetcher = new DataFetcher(this,this);
        fetcher.execute();


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Implementing
    @Override public void postResult(JSONArray jsonData) {
        // Store the JSON data that is loaded from assets file .
        // This data will inserted into the Couchbase Lite DB whenever
        // a request to "fetch data" is made by the user in order to simulate
        // database updates
        sampleData = jsonData;
        Log.i("DATA", String.valueOf(jsonData));
        // You could use any JSON to POJO mapper . Doing it manually here.
        List<University> universities = new ArrayList<University>();

        try {
            for (int i = 0; i < sampleData.length(); i++) {
                JSONObject object = sampleData.getJSONObject(i);
                String name = object.getString("name");
                String country = object.getString("country");
                University university = new University(name, country);
                universities.add(university);

            }
            adapter.setUniversities(universities);
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
