package com.example.m_expense;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class EditExpenseActivity extends AppCompatActivity {
    EditText exType;
    EditText exAmount;
    EditText exTime;
    EditText exComments;

    Button btnUpdateExpense;
    String selection;
    int hour;
    int minute;
    int userId;
    long tripId;
    long exId;
    int iconSelection;

    Spinner exIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Calendar currentTime = Calendar.getInstance();
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        tripId = intent.getLongExtra("tripId", 0);
        exId = intent.getLongExtra("exId", 0);
        getViews(currentTime);
        populateExpenseEditor();
        setToolbar();
        setSpinner();
        setSpinnerListener();
        setTimeListener();
        btnUpdateExpense.setOnClickListener(view -> checkBudget());
    }

    private void setTimeListener() {
        exTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditExpenseActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String time = String.format(getString((R.string.time_formatted)), checkDigit(hourOfDay), checkDigit(minute));
                        exTime.setText(time);
                        exTime.setError(null);
                    }, hour, minute, true);
            timePickerDialog.setTitle(R.string.pick_time);
            timePickerDialog.show();
        });
    }

    private void setSpinnerListener() {
        exIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selection = exIcon.getSelectedItem().toString();
                selectionToInt(selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.icon_selection, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        exIcon.setAdapter(adapter);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.addExpenseToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }

    private void getViews(Calendar currentTime) {
        exType = findViewById(R.id.edtxtExType);
        exAmount = findViewById(R.id.edtxtExAmount);
        exTime = findViewById(R.id.edtxtExTime);
        exComments = findViewById(R.id.edtxtExComments);
        exIcon = findViewById(R.id.spinnerIconSelection);
        btnUpdateExpense = findViewById(R.id.btnInsertExpense);
        btnUpdateExpense.setText(R.string.update_expense);
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(EditExpenseActivity.this, ExpenseOptionsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        intent.putExtra("exId", exId);
        startActivity(intent);
        return true;
    }

    private void checkBudget() {
        DBHelper db = new DBHelper(this);
        if (!checkEmptyFields()) {


            int amountCheck = Integer.parseInt((exAmount.getText().toString()));

            if (!belowZeroCheck(db, amountCheck)) {
                updateExpenseById(db, amountCheck);
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setCancelable(true);
                builder2.setTitle("Confirm Expense Details");
                builder2.setMessage("Editing this expense will place your budget below 0. \n\n"
                        + "Please return to the trip options and adjust your budget"
                );
                builder2.setPositiveButton("Confirm",
                        (dialog, which) -> {


                        });
                builder2.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                AlertDialog dialog2 = builder2.create();
                dialog2.show();

            }
        }
    }

    private void updateExpenseById(DBHelper db, int amountCheck) {

        Cursor cursor = db.getOneExpenseById(exId);
        cursor.moveToPosition(0);
        @SuppressLint("Range")
        int getCurrentExpense = cursor.getInt(cursor.getColumnIndex(DBHelper.EXPENSE_AMOUNT_COLUMN));
        int expenseDifference = amountCheck - getCurrentExpense;
        int newBudget = db.getBudgetByTripId((int) tripId) - expenseDifference;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Expense Updates");
        builder.setMessage("Time: " + exTime.getText().toString() + "\n"
                + "Type: " + exType.getText().toString() + "\n"
                + "Amount: " + exAmount.getText().toString() + "\n"
                + "Comments: " + exComments.getText().toString() + "\n"
                + "Icon: " + exIcon.getSelectedItem().toString() + "\n\n"
        );
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    String time = exTime.getText().toString();
                    int amount = Integer.parseInt(exAmount.getText().toString());
                    String type = exType.getText().toString();
                    String comments = exComments.getText().toString();
                    int icon = iconSelection;

                    Expense e = new Expense(type, amount, time, comments, (int) exId, icon);
                    db.updateExpenseDetails(e, exId);
                    db.updateBudget(tripId, newBudget);

                    Intent intent = new Intent(EditExpenseActivity.this, ViewExpenseActivity.class);
                    intent.putExtra("exId", exId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("tripId", tripId);
                    startActivity(intent);


                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean belowZeroCheck(DBHelper db, int amountCheck) {
        return budgetCheck(db) - amountCheck < 0;
    }

    private boolean checkEmptyFields() {
        if (exTime.getText().toString().trim().equals("")) {
            exTime.setError("Please enter a Time");

        }

        if (exType.getText().toString().trim().equals("")) {
            exType.setError("Please enter a Type");

        }

        if (exAmount.getText().toString().trim().equals("")) {
            exAmount.setError("Please enter an Amount");

        }
        return exTime.getText().toString().trim().equals("") ||
                exType.getText().toString().trim().equals("") ||
                exAmount.getText().toString().trim().equals("");
    }

    private void selectionToInt(String selection) {
        if (selection.equals("Hotel")) {
            iconSelection = 1;
        }
        if (selection.equals("Travel")) {
            iconSelection = 2;
        }
        if (selection.equals("Food")) {
            iconSelection = 3;
        }
    }

    private void populateExpenseEditor() {
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getOneExpenseById(exId);
        cursor.moveToPosition(0);

        @SuppressLint("Range") String getExpenseType = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TYPE_COLUMN));
        @SuppressLint("Range") String getExpenseTime = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TIME_COLUMN));
        @SuppressLint("Range") String getExpenseAmount = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_AMOUNT_COLUMN));
        @SuppressLint("Range") String getExpenseComments = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_COMMENTS_COLUMN));
        cursor.close();

        exType.setText(getExpenseType);
        exTime.setText(getExpenseTime);
        exAmount.setText(getExpenseAmount);
        exComments.setText(getExpenseComments);
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private int budgetCheck(DBHelper db) {
        Cursor cursor = db.getOneTripById((int) tripId);
        cursor.moveToPosition(0);
        @SuppressLint("Range") int getTripBudget = cursor.getInt(cursor.getColumnIndex(DBHelper.TRIP_BUDGET_COLUMN));
        return getTripBudget;
    }

}
