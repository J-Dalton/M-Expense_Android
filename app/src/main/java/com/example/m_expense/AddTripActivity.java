package com.example.m_expense;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FusedLocationProviderClient locationClient;
    private final int REQUEST_PERMISSION_FINE_LOCATION = 1;

    EditText enteredDestination;
    EditText enteredDate;
    EditText enteredTripName;
    EditText enteredTripDescription;
    EditText enteredBudget;

    RadioButton rbRiskYes;
    RadioButton rbRiskNo;
    RadioButton rbLocationYes;
    RadioButton rbLocationNo;
    RadioGroup rgRisk;
    RadioGroup rgLocation;

    Button btnInsertTrip;

    int flagId;
    int userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        setToolbar();
        getViews();
        setListeners();
        permissionsCheck();
        setRadioButtonListener();
    }

    private void setRadioButtonListener() {
        rgLocation.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case (R.id.rbLocationYes):
                    enteredDestination.setError(null);
                    showLocation(true);

                    break;
                case (R.id.rbLocationNo):

                    showLocation(false);
                    break;

            }
        });
    }

    private void permissionsCheck() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_FINE_LOCATION
            );
        }
    }

    private void setListeners() {
        enteredDate.setOnClickListener(this::showDatePickerDialog);
        btnInsertTrip.setOnClickListener(view -> emptyFieldCheck());
    }

    private void getViews() {
        rgRisk = findViewById(R.id.rgRisk);
        rgLocation = findViewById(R.id.rgLocation);
        rbRiskYes = findViewById(R.id.rbRiskYes);
        rbRiskNo = findViewById(R.id.rbRiskNo);
        rbLocationYes = findViewById(R.id.rbLocationYes);
        rbLocationNo = findViewById(R.id.rbLocationNo);
        btnInsertTrip = findViewById(R.id.btnInsertTrip);
        enteredDate = findViewById(R.id.edtxtDate);
        enteredDestination = findViewById(R.id.edtxtDestination);
        enteredTripName = findViewById(R.id.edtxtTripName);
        enteredTripDescription = findViewById(R.id.edtxtDescription);
        enteredBudget = findViewById(R.id.edtxtBudget);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.addTripToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean checkDestinationField() {
        return enteredDestination.getText().toString().equals("");
    }

    private boolean checkTripNameField() {
        return enteredTripName.getText().toString().equals("");
    }

    private boolean checkDateField() {
        return enteredDate.getText().toString().equals("");
    }
    private boolean checkBudgetField() {
        return enteredBudget.getText().toString().equals("");
    }

    private void emptyFieldCheck() {

        if (checkTripNameField()) {
            enteredTripName.setError("Please enter a Trip Name");
        }
        if (checkDestinationField()) {
            enteredDestination.setError("Please enter a Destination");
        }
        if (checkDateField()) {
            enteredDate.setError("Please enter a Date");
        }
        if(checkBudgetField()){
            enteredBudget.setError("Please enter a budget");
        }

        if (!checkTripNameField() && !checkDestinationField() && !checkDateField() && !checkBudgetField()) {
            prepareInsertTrip();

        }
    }

    private void prepareInsertTrip() {
        String riskString;
        if (rbRiskYes.isChecked()) {
            riskString = "Yes";
        } else {
            riskString = "No";
        }
        boolean risk;
        String name = enteredTripName.getText().toString();
        String destination = enteredDestination.getText().toString();
        String date = enteredDate.getText().toString();
        risk = rbRiskYes.isChecked();
        String description = enteredTripDescription.getText().toString();
        String budgetString = enteredBudget.getText().toString();
        int budget = Integer.parseInt(budgetString);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Trip Details");
        builder.setMessage("Trip Name: " + enteredTripName.getText() + "\n"
                + "Budget: " + budget + "\n"
                + "Date: " + enteredDate.getText() + "\n"
                + "Destination: " + enteredDestination.getText() + "\n"
                + "Description: " + enteredTripDescription.getText() + "\n"
                + "Risk assessment required?: " + riskString + "\n"
        );
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    DBHelper db = new DBHelper(this);


                    Trip t = new Trip(name, destination, date, risk, description, userId, getFlagId(), budget);

                    db.insertTrip(t);
                    clearFields();
                    Intent intent = new Intent(AddTripActivity.this, ViewTripActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    setCustomToast("Trip added successfully");
                    db.close();
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private int getFlagId() {
        flagId = 0;
        if (enteredDestination.getText().toString().toLowerCase().contains("england")) {
            flagId = 1;
        }
        if (enteredDestination.getText().toString().toLowerCase().contains("france")) {
            flagId = 2;
        }
        if (enteredDestination.getText().toString().toLowerCase().contains("germany")) {
            flagId = 3;
        }
        if (enteredDestination.getText().toString().toLowerCase().contains("spain")) {
            flagId = 4;
        }
        return flagId;
    }

    private void clearFields() {
        enteredTripName.setText("");
        enteredDestination.setText("");
        enteredDate.setText("");
        enteredTripDescription.setText("");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCustomToast("Permission granted!");
                showLocation(false);
            } else {
                setCustomToast("Permission denied!");

            }
        }
    }

    //To work on a new emulator, you must open google maps and give permissions
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
                        enteredDestination.setText(fullAddress);
                    } else {
                        enteredDestination.setText("");
                    }
                }
            });
        }
    }


    private boolean checkPermission() {
        return true;
    }

    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }

    private void showDatePickerDialog(View v) {
        enteredDate.setError(null);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
