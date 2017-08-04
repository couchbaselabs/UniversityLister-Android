package com.couchbase.universitylister.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.university_toolbar);
        setSupportActionBar(toolbar);

        // Get recycler view
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvUniversities);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Asynchronously Load the data from local sample file
        DataFetcher fetcher = new DataFetcher(this,this);
        fetcher.execute();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:

                // User chose  "Add University"
                // Now select a random university item from json array and add to the couchbase lite database
                // In a real application, this would correspond to the case when a user addded a document from
                // the app or if a document was added at the server (and  replicated to the app )
                // In either case, the key point is that the local database is getting updated
                // and this will trigger the livequery callback to fire
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }
}
