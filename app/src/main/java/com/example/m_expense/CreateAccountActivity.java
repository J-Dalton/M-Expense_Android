package com.example.m_expense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    EditText entered_name;
    EditText entered_email;
    EditText entered_password;
    EditText confirm_password;

    Button btnCreateUser;

    CheckBox terms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getViews();
        setToolbar();
        btnCreateUser.setOnClickListener(view -> createUser());

    }

    private void getViews() {
        entered_name = findViewById(R.id.edtxtNameCA);
        entered_email = findViewById(R.id.edtxtEmailCA);
        entered_password = findViewById(R.id.edtxtPasswordCA);
        confirm_password = findViewById(R.id.edtxtRetypePassword);
        btnCreateUser = findViewById(R.id.btnCreateAccountCA);
        terms = findViewById(R.id.cbAgree);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.createAccountToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);

        startActivity(intent);
        return true;
    }
    private void createUser() {
        DBHelper db = new DBHelper(this);
        String name = entered_name.getText().toString();
        String email = entered_email.getText().toString();
        String password = entered_password.getText().toString();
        String match_password = confirm_password.getText().toString();

        if (!password.equals(match_password)) {

            entered_password.setText("");
            confirm_password.setText("");
            entered_password.setError("Passwords do not match!");
            confirm_password.setError("Passwords do not match!");

        }

        if (nameFieldEmpty()) {
            entered_name.setError("Please enter a name");

        }

        if (emailFieldEmpty()) {
            entered_email.setError("Please enter an email");

        }

        if (passwordFieldEmpty()) {
            entered_password.setError("Please enter a password");

        }


        if (db.checkIfEmailExists(email)) {
            entered_email.setError("This email address is already in use, please login instead");

        }

        if (!termsAgreedChecked()) {
            terms.setError("Please agree to the terms and conditions");

        }

        if (!nameFieldEmpty() && !emailFieldEmpty() && !passwordFieldEmpty() &&
                password.equals(match_password) && termsAgreedChecked() && !db.checkIfEmailExists(email)) {
            Person p = new Person(name, email, password);
            db.insertUser(p);
            int userId = db.getIdByEmail(email);
            Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            db.close();

        }
    }

    private boolean termsAgreedChecked() {
        return terms.isChecked();
    }


    private boolean nameFieldEmpty() {
        return entered_name.getText().toString().equals("");
    }

    private boolean emailFieldEmpty() {
        return entered_email.getText().toString().equals("");
    }

    private boolean passwordFieldEmpty() {
        return entered_password.getText().toString().equals("");
    }


}
