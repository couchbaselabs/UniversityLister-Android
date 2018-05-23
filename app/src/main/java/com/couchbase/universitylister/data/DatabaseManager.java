package com.couchbase.universitylister.data;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import java.io.File;

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
            config.setDirectory(dir.toString());

            // Create / Open a database with specified name and configuration
            database = new Database(DATABASE_NAME, config);



        }
        catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }



    public static DatabaseManager getSharedInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }


}

