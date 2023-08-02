package com.example.m_expense;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class AddExpenseActivity extends AppCompatActivity {

    EditText exType;
    EditText exAmount;
    EditText exTime;
    EditText exComments;

    Spinner exIcon;
    String selection;
    Button btnInsertExpense;

    int hour;
    int minute;
    int userId;
    long tripId;
    long exId;
    int iconSelection;

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
        setToolbar();
        setSpinner();
        btnInsertExpense.setOnClickListener(view -> checkBudget());
        setTimeListener();

    }

    private void setTimeListener() {
        exTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddExpenseActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String time = String.format(getString((R.string.time_formatted)), checkDigit(hourOfDay), checkDigit(minute));
                        exTime.setText(time);
                        exTime.setError(null);
                    }, hour, minute, true);
            timePickerDialog.setTitle(R.string.pick_time);
            timePickerDialog.show();

        });
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.icon_selection, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        exIcon.setAdapter(adapter);

        exIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = exIcon.getSelectedItem().toString();
                selectionToInt(selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        btnInsertExpense = findViewById(R.id.btnInsertExpense);
        exIcon = findViewById(R.id.spinnerIconSelection);

        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
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

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(AddExpenseActivity.this, ViewExpenseActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
        return true;
    }

    private void setCustomToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, s, duration).show();
    }
    private boolean belowZeroCheck(DBHelper db, int amountCheck) {
        return budgetCheck(db) - amountCheck < 0;
    }

    private void checkBudget() {
        DBHelper db = new DBHelper(this);
        if(!checkEmptyFields()){
            int amountCheck = Integer.parseInt((exAmount.getText().toString()));

            if (!belowZeroCheck(db, amountCheck)) {
                prepareInsertExpense(db);
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setCancelable(true);
                builder2.setTitle("Confirm Expense Details");
                builder2.setMessage("Adding this expense will place your budget below 0. \n\n"
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
    private void prepareInsertExpense(DBHelper db) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Expense Details");
        builder.setMessage("Type: " + exType.getText() + "\n"
                + "Amount: " + exAmount.getText() + "\n"
                + "Time: " + exTime.getText() + "\n"
                + "Comments: " + exComments.getText() + "\n"
                + "Icon: " + exIcon.getSelectedItem().toString() + "\n\n"
        );
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    String type = exType.getText().toString();
                    int amount = Integer.parseInt(exAmount.getText().toString());
                    String time = exTime.getText().toString();
                    String comments = exComments.getText().toString();
                    int icon = iconSelection;
                    int remainingBudget = budgetCheck(db) - amount;

                    Expense e = new Expense(type, amount, time, comments, (int) tripId, icon);
                    db.insertExpense(e, userId);
                    db.updateBudget(tripId, remainingBudget);

                    Intent intent = new Intent(AddExpenseActivity.this, TripOptionsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("tripId", tripId);
                    startActivity(intent);
                    setCustomToast("Expense added successfully");


                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

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

    private void budgetBelowZero() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Expense Details");
        builder.setMessage("Adding this expense will place your budget below 0. \n\n"
                + "Are you sure you wish to continue?"
        );
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {


                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int budgetCheck(DBHelper db) {
        Cursor cursor = db.getOneTripById((int) tripId);
        cursor.moveToPosition(0);
        @SuppressLint("Range") int getTripBudget = cursor.getInt(cursor.getColumnIndex(DBHelper.TRIP_BUDGET_COLUMN));
        return getTripBudget;
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
