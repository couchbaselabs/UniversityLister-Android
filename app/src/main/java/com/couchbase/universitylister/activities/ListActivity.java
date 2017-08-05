package com.couchbase.universitylister.activities;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.ResultSet;
import com.couchbase.universitylister.R;
import com.couchbase.universitylister.adapter.UniversityListAdapter;
import com.couchbase.universitylister.data.DataFetcher;
import com.couchbase.universitylister.data.DatabaseManager;
import com.couchbase.universitylister.data.IDataFetchResponse;
import com.couchbase.universitylister.model.University;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity implements IDataFetchResponse {

    private static DatabaseManager dbMgr ;
    private List<University> sampleData = null; // Used to hold sample JSON data
    private final UniversityListAdapter adapter = new UniversityListAdapter(this);
    private LiveQuery query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize couchbase lite database manager
        dbMgr = new DatabaseManager(this);

        // Set content layout
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

    // IDataFetchResponse callback when the JSON sample data is loaded
    @Override public void postResult(List<University> jsonData) {
        // Store the JSON data that is loaded from assets file .
        // This data will inserted into the Couchbase Lite DB whenever
        // a request to "fetch data" is made by the user in order to simulate
        // database updates
        sampleData = jsonData;

        // Initialize Query to fetch documents
        fetchListOfUniversities();
        /*
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
        */


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:

                // User chose  "Add University"
                fetchUniversityAndAddToDatabase();
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


    private void fetchListOfUniversities() {
        try {
            // Create a liveQuery to fetch all documents from database
            query = Query.
                    select().
                    from(DataSource.database(dbMgr.database)).
                    toLive();

            // Add a live query listener to continually monitor for changes
            query.addChangeListener(new LiveQueryChangeListener() {
                                        @Override
                                        public void changed(LiveQueryChange change) {
                                            ResultSet resultRows = change.getRows();
                                            QueryRow row;
                                            List<University> universities = new ArrayList<University>();
                                            // Iterate over changed rows
                                            while ((row = resultRows.next()) != null) {
                                                // Fetch corresponding documents
                                                Document doc = row.getDocument();
                                                Log.i("LIVE","DOC ADDED"+doc.toString());

                                                // Map the document to University
                                                University university = new ObjectMapper().convertValue(doc.toMap(),University.class);
                                                universities.add(university);


                                            }



                                            // Update the adapter with the newly added University documents
                                            adapter.addUniversities(universities);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // reload adapter
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    }
            );
            query.run();
        }
        catch (IllegalArgumentException e) {

        }
    }
    // Select a random university item from json array and add to the couchbase lite database
    // In a real application, this simulates the case when either a user adds a document from
    // the app or if a document was added at the server and  replicated to the app.
    // In either case, the local database is getting updated and this will trigger  livequery listeners to fire
    private void fetchUniversityAndAddToDatabase() {
        if (sampleData == null) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder
                    .setMessage(R.string.data_not_fetched)
                    .setCancelable(true).show();
            return;

        }
        Random r = new Random();
        int index = r.nextInt(sampleData.size()-1);
        try {
            // Get university object at randomly selected index
            University university = sampleData.get(index);

            // Convert University to hashmap
            HashMap<String,Object> universityMap = new ObjectMapper().convertValue(university,HashMap.class);
            // Construct the document from university object
            Document doc = new Document(universityMap);

            Log.i("CBL","Will add doc with Id"+ doc.getId());
            // Save document to database.
            dbMgr.database.save(doc);
        }
        catch ( NullPointerException e) {
            e.printStackTrace();
        }


    }
}
