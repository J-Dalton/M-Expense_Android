package com.example.m_expense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnCreateAccount;

    EditText entered_password;
    EditText entered_email;

    String passwordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        setListeners();

        entered_email.setText("j@j.co.uk");
        entered_password.setText("1");
    }

    private void getViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        entered_email = findViewById(R.id.edittxtEmailLog);
        entered_password = findViewById(R.id.edittxtPasswordLog);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(view -> checkPassword());
        btnCreateAccount.setOnClickListener(view -> openCreateAccountPage());
    }

    private void checkPassword() {
        DBHelper db = new DBHelper(this);
        try {
            passwordCheck = db.getPasswordByEmail(entered_email.getText().toString());
        } catch (CursorIndexOutOfBoundsException e) {
            entered_email.setText("");
        }

        String pass1 = entered_password.getText().toString();

        if (pass1.equals(passwordCheck)) {
            int userId = db.getIdByEmail(entered_email.getText().toString());

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            entered_email.setError("Invalid email/password combination");
            entered_password.setError("Invalid email/password combination");
        }
    db.close();
    }

    private void  openCreateAccountPage(){
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }
}