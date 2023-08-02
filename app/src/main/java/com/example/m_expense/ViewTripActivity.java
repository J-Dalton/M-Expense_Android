package com.example.m_expense;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class ViewTripActivity extends AppCompatActivity implements RVInterface {

    RVAdapter adapter;
    int userId;
    long tripId;
    RecyclerView recyclerView;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        DBHelper db = new DBHelper(this);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        tripId = intent.getLongExtra("tripId", 0);
        setRecyclerAdapter(db);
        setToolbar();
        setNavigationBar();
    }

    private void setRecyclerAdapter(DBHelper db) {
        recyclerView = findViewById(R.id.rvTrips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter(this, this, db.getAllTripsById(userId), "trip");
        recyclerView.setAdapter(adapter);
    }

    private void setNavigationBar() {
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.trip_link);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home_link):
                    Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                    intent1.putExtra("userId", userId);
                    startActivity(intent1);
                    overridePendingTransition(0, 0);
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
                    return true;
            }
            return false;
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewTripToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onItemLongClick(long pos) {
        Intent intent = new Intent(ViewTripActivity.this, TripOptionsActivity.class);
        intent.putExtra("tripId", pos);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}

