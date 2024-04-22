package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Search extends AppCompatActivity {
    private static String TAG = "Search_Screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText searchQueryEt = (EditText) findViewById(R.id.activity_search_et_query);

        // on click for search button: do this when search is pressed
        // id: @+id/activity_search_bt_search
        Button searchBt = findViewById(R.id.activity_search_bt_search);
        String queryStr = searchQueryEt.getText().toString();


        searchBt.setOnClickListener(v -> {
            Log.d(TAG, "search clicked");

            // perform the search
        });

    }
}