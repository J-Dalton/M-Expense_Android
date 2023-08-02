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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class ViewExpenseActivity extends AppCompatActivity implements RVInterface {

    RVAdapter adapter;
    long tripId;
    long exId;
    int userId;

    Button btnAddExpense;

    TextView tvDisplayTripInfo;
    TextView tvExpenseCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);
        DBHelper db = new DBHelper(this);
        Intent intent = getIntent();
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        exId = intent.getLongExtra("exId", 0);
        getViews();
        setRecyclerAdapter(db);
        setToolbar();
        btnAddExpense.setOnClickListener(view -> goToViewTripExpenses());
        displayTripName();
        displayTripExpenseCount();
    }

    private void setRecyclerAdapter(DBHelper db) {
        RecyclerView recyclerView = findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter(this, this, db.getAllExpensesById((int) tripId), "expense");
        recyclerView.setAdapter(adapter);
    }

    private void getViews() {
        btnAddExpense = findViewById(R.id.btnAddAnotherExpense);
        tvDisplayTripInfo = findViewById(R.id.tvtripDetails);
        tvExpenseCount = findViewById(R.id.tvExpenseCount);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.viewExpenseToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ViewExpenseActivity.this, TripOptionsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        intent.putExtra("exId", exId);
        startActivity(intent);
        return true;
    }

    public void displayTripName() {
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getOneTripById((int) tripId);
        cursor.moveToPosition(0);

        @SuppressLint("Range") String getTripName = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_NAME_COLUMN));

        String display = String.format(getString(R.string.trip_details), getTripName);
        tvDisplayTripInfo.setText(display);
        db.close();

    }

    public void displayTripExpenseCount() {
        DBHelper db = new DBHelper(this);
        String expenseCount = String.valueOf(db.countExpensesByTripId(tripId));
        String display = String.format(getString(R.string.expense_count), expenseCount);
        tvExpenseCount.setText(display);
        db.close();

    }

    @Override
    public void onItemLongClick(long pos) {
        Intent intent = new Intent(ViewExpenseActivity.this, ExpenseOptionsActivity.class);
        intent.putExtra("exId", pos);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
    }

    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }

    private void goToViewTripExpenses() {
        Intent intent = new Intent(ViewExpenseActivity.this, AddExpenseActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
    }
}
