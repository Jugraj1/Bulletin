package com.example.app_2100;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;


/**
 * Jinzeng
 * Jugraj Singh
 */

public class SearchActivity extends AppCompatActivity {
    // Date picker dialogs
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog datePickerDialogTo;

    // Buttons for selecting dates
    private Button dateButton;
    private Button dateButtonTo;

    // Text field for search input
    private EditText searchField;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Apply window insets listener to adjust padding when system bars change
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize date picker dialogs
        initDatePicker();

        // Set today's date to date buttons
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        dateButtonTo = findViewById(R.id.datePickerButtonTo);
        dateButtonTo.setText(getTodaysDate());

        // Set up button for going back to home feed
        Button goBackBt = findViewById(R.id.activity_search_bt_go_back);
        goBackBt.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, HomeFeed.class));
        });

        // Set up search functionality
        searchField = findViewById(R.id.activity_search_et_search_field);
        Button goSearchBt = findViewById(R.id.activity_search_bt_go);
        goSearchBt.setOnClickListener(v -> {
            // Prepare intent to pass search parameters to search results activity
            Intent searchToSearchResultsActivity = new Intent(SearchActivity.this, SearchResultsActivity.class);
            searchToSearchResultsActivity.putExtra("searchField", searchField.getText().toString());
            searchToSearchResultsActivity.putExtra("dateButton", dateButton.getText().toString());
            searchToSearchResultsActivity.putExtra("dateButtonTo", dateButtonTo.getText().toString());
            startActivity(searchToSearchResultsActivity);
        });
    }

    // Initialize date picker dialogs with current date
    private void initDatePicker() {
        // Listener for setting selected date to the "From" date button
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        // Listener for setting selected date to the "To" date button
        DatePickerDialog.OnDateSetListener dateSetListenerTo = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButtonTo.setText(date);
        };

        // Get current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialogs
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialogTo = new DatePickerDialog(this, style, dateSetListenerTo, year, month, day);
        datePickerDialogTo.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    // Get today's date in the required format
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    // Format the date string
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    // Get abbreviated month name
    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
            default:
                return "JAN"; // Default to January
        }
    }

    // Show date picker dialog for selecting "From" date
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    // Show date picker dialog for selecting "To" date
    public void openDatePickerTo(View view) {
        datePickerDialogTo.show();
    }
}