package com.example.m_expense;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements RVInterface {

    Spinner searchFilter;
    EditText searchBar;
    BottomNavigationView bottomNavigationView;
    long tripId;
    int userId;
    RecyclerView recyclerView;
    RVAdapter rvAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        getViews();
        setToolbar();
        setSpinner();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RVInterface RVInterface = this;
        Context context = this;
        setSearchBarListener(RVInterface, context);
        setSpinnerListener(RVInterface, context);
        setNavigationBar();

    }

    private void setNavigationBar() {
        bottomNavigationView.setSelectedItemId(R.id.search_link);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home_link):
                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    homeIntent.putExtra("userId", userId);
                    startActivity(homeIntent);
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.search_link):

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

    private void setSpinnerListener(RVInterface RVInterface, Context context) {
        searchFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                applyFilterToQuery(context, RVInterface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setSearchBarListener(RVInterface RVInterface, Context context) {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                applyFilterToQuery(context, RVInterface);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                applyFilterToQuery(context, RVInterface);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                applyFilterToQuery(context, RVInterface);
            }
        });
    }

    private void getViews() {
        recyclerView = findViewById(R.id.rvSearchResults);
        searchFilter = findViewById(R.id.spSearchType);
        searchBar = findViewById(R.id.edittextSearch);
        bottomNavigationView = findViewById(R.id.bottomNav);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.search_filter, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        searchFilter.setAdapter(adapter);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

    private void applyFilterToQuery(Context context, RVInterface RVInterface) {
        DBHelper db = new DBHelper(this);
        String search = searchBar.getText().toString();
        String filter = searchFilter.getSelectedItem().toString();
        rvAdapter = new RVAdapter(context, RVInterface, db.getTripNameSearchQuery(search, filter, userId), "search");
        recyclerView.setAdapter(rvAdapter);

    }

    @Override
    public void onItemLongClick(long pos) {
        Intent intent = new Intent(SearchActivity.this, TripOptionsActivity.class);
        intent.putExtra("tripId", pos);
        intent.putExtra("userId", userId);
        startActivity(intent);

    }

}
