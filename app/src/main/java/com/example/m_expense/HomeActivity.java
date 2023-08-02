package com.example.m_expense;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    int userId;

    String expenseCount;
    String welcome;
    String tripCount;
    String expenseSum;

    TextView user_info_display;
    TextView expenseSumDisplay;
    TextView tripCountDisplay;
    TextView expenseCountDisplay;
    TextView averageEx;

    ImageButton btnAddTrip;

    Cursor cursor;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DBHelper db = new DBHelper(this);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        getUserInfo(db);
        getViews();
        setToolBar();
        setUserInfo(expenseCount, welcome, tripCount, expenseSum);
        setNavBar();

    }

    private void setNavBar() {
        btnAddTrip.setOnClickListener(view -> goToAddTripActivity(userId));
        bottomNavigationView.setSelectedItemId(R.id.home_link);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home_link):
                    return true;
                case (R.id.search_link):
                    Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                    searchIntent.putExtra("userId", userId);
                    startActivity(searchIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.profile_link):
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    profileIntent.putExtra("userId", userId);
                    startActivity(profileIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.trip_link):
                    Intent tripIntent = new Intent(getApplicationContext(), ViewTripActivity.class);
                    tripIntent.putExtra("userId", userId);
                    startActivity(tripIntent);
                    overridePendingTransition(0, 0);
                    return true;
            }

            return false;
        });
    }

    private void getUserInfo(DBHelper db) {
        expenseCount = String.valueOf(db.countExpensesByUserId(userId));
        welcome = getUserNameById(db);
        tripCount = String.valueOf(db.countTripsByUserId(userId));
        expenseSum = String.valueOf(db.sumOfExpensesByUserId(userId));
    }

    private void getViews() {
        expenseSumDisplay = findViewById(R.id.txtExpenseSum);
        tripCountDisplay = findViewById(R.id.txtTripCount);
        user_info_display = findViewById(R.id.tvUserInfo);
        expenseCountDisplay = findViewById(R.id.txtExpenseCount);
        averageEx = findViewById(R.id.tvAverageEx);
        btnAddTrip = findViewById(R.id.btnAddTrip);
        bottomNavigationView = findViewById(R.id.bottomNav);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }

    private void setUserInfo(String expenseCount, String welcome, String tripCount, String expenseSum) {
        expenseSumDisplay.setText(String.format(getString((R.string.expense_count)), expenseCount));
        tripCountDisplay.setText(String.format(getString((R.string.trip_count)), tripCount));
        user_info_display.setText(String.format(getString((R.string.welcome_message)), welcome));
        expenseCountDisplay.setText(String.format(getString((R.string.expense_sum)), expenseSum));


            float average = (float) Integer.parseInt(expenseSum) / (float) Integer.parseInt(tripCount);
            @SuppressLint("DefaultLocale") String averageString = String.format("%.2f", average);
            if(Double.isNaN(average)){
                averageString = "0";
            }
            averageEx.setText(String.format(getString((R.string.average_expense)), averageString));


    }

    @Override
    public boolean onSupportNavigateUp() {
        DBHelper db = new DBHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want log out?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
        db.close();
        return true;
    }

    private String getUserNameById(DBHelper db) {
        cursor = db.getOneUserById(userId);
        cursor.moveToFirst();
        @SuppressLint("Range") String getUsername = cursor.getString(cursor.getColumnIndex(DBHelper.USER_NAME_COLUMN));
        cursor.close();
        return getUsername;
    }


    private void goToAddTripActivity(int id) {
        Intent intent = new Intent(HomeActivity.this, AddTripActivity.class);
        intent.putExtra("userId", id);
        startActivity(intent);
    }


}
