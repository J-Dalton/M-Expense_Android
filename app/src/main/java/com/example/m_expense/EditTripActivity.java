package com.example.m_expense;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FusedLocationProviderClient locationClient;


    long tripId;
    boolean riskBool;
    int userId;
    int flagId;


    String riskString;

    EditText tripName;
    EditText tripDate;
    EditText tripDescription;
    EditText tripDestination;
    EditText tripBudget;

    RadioGroup rgRisk;
    RadioButton rbRiskYes;
    RadioButton rbRiskNo;

    RadioGroup rgLocation;
    RadioButton rbLocationYes;
    RadioButton rbLocationNo;

    Button btnEditTrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent = getIntent();
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        getViews();
        populateTripEditor();
        setToolbar();
        setListeners();
    }

    private void setListeners() {
        tripDate.setOnClickListener(this::showDatePickerDialog);
        btnEditTrip.setOnClickListener(view -> prepareTripDetails());
        rgLocation.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case (R.id.rbLocationYes):
                    tripDestination.setError(null);
                    showLocation(true);

                    break;
                case (R.id.rbLocationNo):

                    showLocation(false);
                    break;

            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.addTripToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }

    private void getViews() {
        tripName = findViewById(R.id.edtxtTripName);
        tripDate = findViewById(R.id.edtxtDate);
        tripDescription = findViewById(R.id.edtxtDescription);
        tripDestination = findViewById(R.id.edtxtDestination);
        tripBudget = findViewById(R.id.edtxtBudget);
        rgLocation = findViewById(R.id.rgLocation);
        rbRiskYes = findViewById(R.id.rbRiskYes);
        rbRiskNo = findViewById(R.id.rbRiskNo);
        rgRisk = findViewById(R.id.rgRisk);
        rbLocationYes = findViewById(R.id.rbLocationYes);
        rbLocationNo = findViewById(R.id.rbLocationNo);
        btnEditTrip = findViewById(R.id.btnInsertTrip);
        btnEditTrip.setText(R.string.update_trip);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void prepareTripDetails() {

        if (rbRiskYes.isChecked()) {
            riskString = "Yes";
        } else {
            riskString = "No";
        }

            DBHelper db = new DBHelper(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Changes");
            builder.setMessage("Trip Name: " + tripName.getText() + "\n"
                    + "Budget: " + tripBudget.getText() + "\n"
                    + "Date: " + tripDate.getText() + "\n"
                    + "Destination: " + tripDestination.getText() + "\n"
                    + "Description: " + tripDescription.getText() + "\n"
                    + "Risk assessment required?: " + riskString + "\n"
            );
            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        riskBool = rbRiskYes.isChecked();
                        int budget = Integer.parseInt(tripBudget.getText().toString());

                        Trip t = new Trip(tripName.getText().toString(), tripDestination.getText().toString(),
                                tripDate.getText().toString(), riskBool,
                                tripDescription.getText().toString(), userId, getFlagId(), budget);
                        db.updateTripDetails(t, (int) tripId);

                        Intent intent = new Intent(EditTripActivity.this, ViewTripActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        setCustomToast("Trip updated");

                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    private int getFlagId() {

        flagId = 0;
        if (tripDestination.getText().toString().toLowerCase().contains("england")) {
            flagId = 1;
        }
        if (tripDestination.getText().toString().toLowerCase().contains("france")) {
            flagId = 2;
        }
        if (tripDestination.getText().toString().toLowerCase().contains("germany")) {
            flagId = 3;
        }
        if (tripDestination.getText().toString().toLowerCase().contains("spain")) {
            flagId = 4;
        }
        return flagId;
    }

    private void showDatePickerDialog(View v) {
        tripDate.setError(null);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

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
        tripBudget.setText(String.valueOf(getTripBudget));

        if (getTripRisk == 1) {
            rbRiskYes.setChecked(true);
        } else {
            rbRiskNo.setChecked(true);
        }
        rbLocationNo.setChecked(true);
    }

    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(c.getTime());

        EditText inputDate = findViewById(R.id.edtxtDate);
        inputDate.setText(today);

    }
    private boolean checkPermission() {
        return true;
    }
    @SuppressLint("Range")
    private void showLocation(boolean b) {
        if (checkPermission()) {

            locationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 5);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String postcode = addresses.get(0).getPostalCode();
                    String city = addresses.get(0).getAdminArea();
                    String streetName = addresses.get(0).getFeatureName();
                    String countryName = addresses.get(0).getCountryName();

                    String fullAddress = String.format(getString((R.string.location_formatted)), city, streetName, postcode, countryName);
                    if (b) {
                        tripDestination.setText(fullAddress);
                    } else {
                        tripDestination.setText("");
                    }
                }
            });
        }
    }
}
