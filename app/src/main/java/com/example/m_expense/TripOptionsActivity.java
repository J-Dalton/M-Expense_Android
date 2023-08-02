package com.example.m_expense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class TripOptionsActivity extends AppCompatActivity {

    TextView tripName;
    TextView tripDate;
    TextView tripDescription;
    TextView tripDestination;
    TextView tripRisk;
    TextView tripExpenseCount;
    TextView tripBudget;


    Button btnEditTrip;
    Button btnDeleteTrip;
    Button btnViewExpenses;

    long tripId;
    int userId;
    long exId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_options);
        Intent intent = getIntent();
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        exId = intent.getLongExtra("exId", 0);
        getViews();
        populateTripEditor();
        setListeners();
        setToolbar();
    }

    private void setListeners() {
        btnEditTrip.setOnClickListener(view -> goToEditTripDetails());
        btnViewExpenses.setOnClickListener(view -> goToViewTripExpenses());
        btnDeleteTrip.setOnClickListener(view -> deleteTripById());
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tripOptionsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }

    private void getViews() {
        tripName = findViewById(R.id.tvTripNameDisplay);
        tripDate = findViewById(R.id.tvDateDisplay);
        tripDescription = findViewById(R.id.tvDescriptionDisplay);
        tripDestination = findViewById(R.id.tvDestinationDisplay);
        tripRisk = findViewById(R.id.tvRiskDisplay);
        tripExpenseCount = findViewById(R.id.tvExpenseNum);
        tripBudget = findViewById(R.id.tvBudget);
        btnEditTrip = findViewById(R.id.btnEditOption);
        btnDeleteTrip = findViewById(R.id.btnDeleteOption);
        btnViewExpenses = findViewById(R.id.btnViewExpensesOption);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(TripOptionsActivity.this, ViewTripActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        intent.putExtra("exId", exId);
        startActivity(intent);
        return true;
    }

    private void goToEditTripDetails() {
        Intent intent = new Intent(TripOptionsActivity.this, EditTripActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
    }

    private void goToViewTripExpenses() {
        Intent intent = new Intent(TripOptionsActivity.this, ViewExpenseActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
    }

    private void populateTripEditor() {
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getOneTripById((int) tripId);
        cursor.moveToPosition(0);

        @SuppressLint("Range") String getTripName = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_NAME_COLUMN));
        @SuppressLint("Range") String getTripDate = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_DATE_COLUMN));
        @SuppressLint("Range") String getTripDescription = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_DESCRIPTION_COLUMN));
        @SuppressLint("Range") String getTripDestination = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_DESTINATION_COLUMN));
        @SuppressLint("Range") int getTripRisk = cursor.getInt(cursor.getColumnIndex(DBHelper.TRIP_RISK_COLUMN));
        @SuppressLint("Range") int getTripBudget = cursor.getInt(cursor.getColumnIndex(DBHelper.TRIP_BUDGET_COLUMN));

        tripName.setText(getTripName);
        tripDate.setText(getTripDate);
        tripDescription.setText(getTripDescription);
        tripDestination.setText(getTripDestination);
        tripExpenseCount.setText(String.valueOf(db.countExpensesByTripId(tripId)));
        tripBudget.setText(String.format(getString((R.string.poundsign)), String.valueOf(getTripBudget)));



        if (getTripRisk == 1) {
            tripRisk.setText(R.string.radio_yes);
        } else {
            tripRisk.setText(R.string.radio_no);
        }
        db.close();
    }

    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }

    private void deleteTripById() {
        DBHelper db = new DBHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Deleting this trip will also delete all related expenses? Continue anyway?\n\n"
        );
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    db.deleteTripById(tripId);

                    Intent intent = new Intent(TripOptionsActivity.this, ViewTripActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("tripId", tripId);
                    startActivity(intent);
                    setCustomToast("Trip deleted");
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
