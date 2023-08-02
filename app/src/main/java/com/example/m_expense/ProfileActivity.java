package com.example.m_expense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    EditText userName;
    EditText userEmail;
    EditText userPassword;

    long tripId;
    int userId;

    Button btnUpdateUser;
    Button btnDeleteAccount;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        getViews();
        populateUserEditor();
        setListeners();
        setToolbar();
        setNavigationBar();
    }

    private void setListeners() {
        btnUpdateUser.setOnClickListener(view -> updateUserDetails());
        btnDeleteAccount.setOnClickListener(view -> deleteAccountById());
    }

    private void setNavigationBar() {
        bottomNavigationView.setSelectedItemId(R.id.profile_link);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home_link):
                    Intent profileIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    profileIntent.putExtra("userId", userId);
                    startActivity(profileIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.search_link):
                    Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                    searchIntent.putExtra("userId", userId);
                    startActivity(searchIntent);
                    overridePendingTransition(0, 0);
                    return true;

                case (R.id.profile_link):

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

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

    private void getViews() {
        userName = findViewById(R.id.editTextTextPersonName);
        userEmail = findViewById(R.id.editTextTextEmailAddress);
        userPassword = findViewById(R.id.editTextTextPassword);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        bottomNavigationView = findViewById(R.id.bottomNav);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void deleteAccountById() {
        DBHelper db = new DBHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Deleting Account");
        builder.setMessage("Deleting this account will also delete all related expenses and trips.\n\n" +
                "Are you sure you wish to delete this account?" + "\n\n");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {


                    db.deleteUserById((long)userId);

                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }


    private void populateUserEditor() {
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getOneUserById(userId);
        cursor.moveToPosition(0);

        @SuppressLint("Range") String getUserName = cursor.getString(cursor.getColumnIndex(DBHelper.USER_NAME_COLUMN));
        @SuppressLint("Range") String getUserEmail = cursor.getString(cursor.getColumnIndex(DBHelper.USER_EMAIL_COLUMN));
        @SuppressLint("Range") String getUserPassword = cursor.getString(cursor.getColumnIndex(DBHelper.USER_PASSWORD_COLUMN));

        userName.setText(getUserName);
        userEmail.setText(getUserEmail);
        userPassword.setText(getUserPassword);
        db.close();
    }


    private void updateUserDetails() {
        DBHelper db = new DBHelper(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Change");
        builder.setMessage("Name: " + userName.getText() + "\n"
                + "Email: " + userEmail.getText() + "\n\n"
                + "Change your details to those shown above?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    String userNameUpdate = userName.getText().toString();
                    String userEmailUpdate = userEmail.getText().toString();
                    String userPasswordUpdate = userPassword.getText().toString();

                    Person p = new Person(userNameUpdate, userEmailUpdate, userPasswordUpdate);

                    db.updateUserDetails(p, userId);
                    Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(0, 0);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
