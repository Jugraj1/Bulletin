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

public class SearchActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog datePickerDialogTo;
    private Button dateButton;
    private Button dateButtonTo;
    private EditText searchField;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        dateButtonTo = findViewById(R.id.datePickerButtonTo);
        dateButtonTo.setText(getTodaysDate());


        Button goBackBt = findViewById(R.id.activity_search_bt_go_back);
        goBackBt.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, HomeFeed.class));
        });

        searchField = findViewById(R.id.activity_search_et_search_field);
        Button goSearchBt = findViewById(R.id.activity_search_bt_go);
        goSearchBt.setOnClickListener(v -> {
            // Log.d("searchField:", searchField.getText().toString());
            Intent searchToSearchResultsActivity = new Intent(SearchActivity.this, SearchResultsActivity.class);
            searchToSearchResultsActivity.putExtra("searchField", searchField.getText().toString());
            searchToSearchResultsActivity.putExtra("dateButton", dateButton.getText().toString());
            searchToSearchResultsActivity.putExtra("dateButtonTo", dateButtonTo.getText().toString());
            startActivity(searchToSearchResultsActivity);
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
                // Log.d("dateButton:", dateButton.getText().toString());
            }
        };

        DatePickerDialog.OnDateSetListener dateSetListenerTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButtonTo.setText(date);
                // Log.d("dateButtonTo:", dateButtonTo.getText().toString());
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialogTo = new DatePickerDialog(this, style, dateSetListenerTo, year, month, day);
        datePickerDialogTo.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
    public void openDatePickerTo(View view) {
        datePickerDialogTo.show();
    }
}