package com.cs360.cs_360inventorytracker;

/*
 * This activity is the home screen for the app. It checks the user-entered data against the account
 * database so that you can only access the inventory if you have an account. It also has the button
 * that leads to the signup activity so the user can make a new account if necessary.
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mButtonLogin;
    private Button mButtonSignUp;

    private boolean isUserEmpty = true;
    private boolean isPassEmpty = true;

    private InventoryDatabase mInventoryDb;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        mUsername = findViewById(R.id.etUsername);
        mPassword = findViewById(R.id.etPassword);
        mButtonLogin = findViewById(R.id.buttonLogin);
        mButtonSignUp = findViewById(R.id.buttonSignUp);

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isUserEmpty = mUsername.getText().toString().isEmpty();
                mButtonLogin.setEnabled(!isUserEmpty && !isPassEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isPassEmpty = mPassword.getText().toString().isEmpty();
                mButtonLogin.setEnabled(!isUserEmpty && !isPassEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onLoginClicked(View view) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        mAccount = mInventoryDb.accountDao().getAccount(username);

        if (mAccount != null && mAccount.getPassword().equals(password)) {
            Intent intent = new Intent(this, InventoryActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.invalidUser, Toast.LENGTH_LONG).show();
        }
    }

    public void onSignUpClicked(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}