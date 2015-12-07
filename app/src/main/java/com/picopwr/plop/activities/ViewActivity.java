package com.picopwr.plop.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.picopwr.plop.R;
import com.picopwr.plop.db.LogDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    private ListView listView;


    LogDBHelper logDBHelper = new LogDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Get list of log entries
        List<ArrayList<String>> entries = populateListView();

        // Convert inner array into a string
        List<String> adaptedEntries = new ArrayList<>();
        for (ArrayList<String> entry : entries) {
            adaptedEntries.add(entry.toString());
        }

        // List view
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adaptedEntries);
        listView.setAdapter(arrayAdapter);

        Log.d("ENTRIES?", entries.toString());
    }

    public List<ArrayList<String>> populateListView() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        return logDBHelper.getLogEntries(email);
    }

}
