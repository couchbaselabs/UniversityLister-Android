package com.couchbase.universitylister.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.universitylister.R;
import com.couchbase.universitylister.adapter.UniversityListAdapter;
import com.couchbase.universitylister.data.DataFetcher;
import com.couchbase.universitylister.data.DatabaseManager;
import com.couchbase.universitylister.data.IDataFetchResponse;
import com.couchbase.universitylister.model.University;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class ListActivity extends AppCompatActivity implements IDataFetchResponse {

    private static DatabaseManager dbMgr ;
    private List<University> sampleData = null; // Used to hold sample JSON data
    private final UniversityListAdapter adapter = new UniversityListAdapter(this);
    private Query query;

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
        QueryForListOfUniversities();

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


    private void QueryForListOfUniversities() {
        try {


            // 1. Create a liveQuery to fetch all documents from database
            query = QueryBuilder.
                    select(SelectResult.all()).
                    from(DataSource.database(dbMgr.database));

            // 2. Add a live query listener to continually monitor for changes
            query.addChangeListener(new QueryChangeListener() {
                                        @Override
                                        public void changed(QueryChange change) {
                                            ResultSet resultRows = change.getResults();
                                            Result row;
                                            List<University> universities = new ArrayList<University>();
                                            // 3. Iterate over changed rows, corresponding documents and map to University POJO
                                            while ((row = resultRows.next()) != null) {
                                                ObjectMapper objectMapper = new ObjectMapper();
                                                // Ignore undeclared properties
                                                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


                                                // Get dictionary corresponding to the database name
                                                Dictionary valueMap = row.getDictionary(dbMgr.database.getName());

                                                // Convert from dictionary to corresponding University object
                                                University university = objectMapper.convertValue(valueMap.toMap(),University.class);
                                                universities.add(university);
                                            }

                                            // 4. Update the adapter with the newly added University documents
                                            adapter.setUniversities(universities);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // 5. Notify adapter of changed data
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    }
            );
            // 6. Run Query
            query.execute();
        }
        catch (IllegalArgumentException e) {

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    // Select a random university item from json array and add to the couchbase lite database
    // In a real application, this simulates the case when either a user adds a document from
    // the app or if a document was added at the server and  replicated to the app.
    // In either case, the local database is getting updated and this will trigger  livequery listeners to fire
    private void fetchUniversityAndAddToDatabase() {
        if (sampleData == null) {
            // The sample data has not yet loaded. So notify user
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder
                    .setMessage(R.string.data_not_fetched)
                    .setCancelable(true)
                    .setNegativeButton ("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                    } }).show();
            return;

        }
        Random r = new Random();
        int index = r.nextInt(sampleData.size()-1);
        try {
            // 1. Get university object at randomly selected index
            University university = sampleData.get(index);

            // 2. Construct the document from university object
            ObjectMapper objectMapper = new ObjectMapper();

            // Ignore undeclared properties
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HashMap<String,Object> universityMap = objectMapper.convertValue(university,HashMap.class);
            MutableDocument doc = new MutableDocument(universityMap);

            // 3. Save document to database.
            dbMgr.database.save(doc);
        }
        catch ( CouchbaseLiteException | NullPointerException e) {
            e.printStackTrace();
        }


    }
}
