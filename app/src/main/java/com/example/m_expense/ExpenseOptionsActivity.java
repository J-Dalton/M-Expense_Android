package com.example.m_expense;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class ExpenseOptionsActivity extends AppCompatActivity {

    int userId;
    long exId;
    long tripId;

    TextView tvEditTime;
    TextView tvEditType;
    TextView tvEditAmount;
    TextView tvEditComments;

    Button btnEditExpense;
    Button btnDeleteExpense;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_options);
        Intent intent = getIntent();
        exId = intent.getLongExtra("exId", 0);
        tripId = intent.getLongExtra("tripId", 0);
        userId = intent.getIntExtra("userId", 0);
        getViews();
        populateExpenseDetails();
        setListeners();
        setToolbar();


    }

    private void getViews() {
        tvEditTime = findViewById(R.id.tveditTime);
        tvEditType = findViewById(R.id.tveditType);
        tvEditAmount = findViewById(R.id.tveditAmount);
        tvEditComments = findViewById(R.id.tveditComments);
        btnEditExpense = findViewById(R.id.btnEditExpense);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);
    }

    private void setListeners() {
        btnEditExpense.setOnClickListener(view -> goToEditExpense());
        btnDeleteExpense.setOnClickListener(view -> deleteExpenseById());
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.expenseOptionToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ExpenseOptionsActivity.this, ViewExpenseActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        intent.putExtra("exId", exId);
        startActivity(intent);
        return true;
    }


    private void populateExpenseDetails() {
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getOneExpenseById(exId);
        cursor.moveToPosition(0);

        @SuppressLint("Range") String getExpenseType = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TYPE_COLUMN));
        @SuppressLint("Range") String getExpenseTime = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TIME_COLUMN));
        @SuppressLint("Range") String getExpenseAmount = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_AMOUNT_COLUMN));
        @SuppressLint("Range") String getExpenseComments = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_COMMENTS_COLUMN));


        tvEditType.setText(getExpenseType);
        tvEditTime.setText(getExpenseTime);
        tvEditAmount.setText(String.format(getString((R.string.poundsign)), getExpenseAmount));
        tvEditComments.setText(getExpenseComments);
        db.close();
    }

    private void goToEditExpense() {
        Intent intent = new Intent(ExpenseOptionsActivity.this, EditExpenseActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        intent.putExtra("exId", exId);
        startActivity(intent);

    }

    private int budgetCheck(DBHelper db) {
        Cursor cursor = db.getOneTripById((int) tripId);
        cursor.moveToPosition(0);
        @SuppressLint("Range") int getTripBudget = cursor.getInt(cursor.getColumnIndex(DBHelper.TRIP_BUDGET_COLUMN));
        return getTripBudget;
    }

    @SuppressLint("Range")
    private void deleteExpenseById() {
        DBHelper db = new DBHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Expense Delete");
        builder.setMessage("Are you sure you want to delete this expense?\n\n");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    Cursor c = db.getOneExpenseById(exId);
                    c.moveToPosition(0);
                    int expenseToAdd = c.getInt(c.getColumnIndex(DBHelper.EXPENSE_AMOUNT_COLUMN));
                    int remainingBudget = budgetCheck(db) + expenseToAdd;
                    db.updateBudget(tripId, remainingBudget);


                    db.deleteExpenseById(exId);


                    Intent intent = new Intent(ExpenseOptionsActivity.this, ViewExpenseActivity.class);
                    intent.putExtra("exId", exId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("tripId", tripId);
                    startActivity(intent);

                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
