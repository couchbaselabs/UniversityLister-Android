package com.couchbase.universitylister.data;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

import android.content.Context;

import com.couchbase.lite.ConflictResolver;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ReplicatorChangeListener;
import java.io.File;
import java.util.Map;




public class DatabaseManager  {

    private final static String DATABASE_NAME = "university";

    public Database database;

    private static DatabaseManager instance = null;

    public DatabaseManager(Context context) {

        // Set database configuration
        try {

            // Set Database configuration
            DatabaseConfiguration config = new DatabaseConfiguration(context);
            File dir = context.getDir("CBL",Context.MODE_PRIVATE);
            config.setDirectory(dir);

            // Create / Open a database with specified name and configuration
            database = new Database(DATABASE_NAME, config);


        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    public void addItemToDatabase(Map<String,Object> university) {
        Document document = new Document(university);
        database.save(document);

    }

    public static DatabaseManager getSharedInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }


}

