package com.cs360.cs_360inventorytracker;

/*
 * This activity adds new account objects into the account database so that the user can gain access
 * to the inventory. It takes the user-entered information, creates a new account object from it,
 * and adds it to the account database. It then directs back to the login screen so that the user
 * can login with the newly created account.
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

public class SignupActivity extends AppCompatActivity {

    private EditText mNewUsername;
    private EditText mNewPassword;
    private EditText mNewPasswordConfirm;
    private Button mButtonCreateAccount;

    private boolean isNewUserEmpty = true;
    private boolean isNewPassEmpty = true;
    private boolean isNewPassConfirmEmpty = true;

    private InventoryDatabase mInventoryDb;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        mNewUsername = findViewById(R.id.newUsername);
        mNewPassword = findViewById(R.id.newPassword);
        mNewPasswordConfirm = findViewById(R.id.newPasswordConfirm);
        mButtonCreateAccount = findViewById(R.id.buttonCreateAccount);

        mNewUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNewUserEmpty = mNewUsername.getText().toString().isEmpty();
                mButtonCreateAccount.setEnabled(!isNewUserEmpty && !isNewPassEmpty && !isNewPassConfirmEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNewPassEmpty = mNewPassword.getText().toString().isEmpty();
                mButtonCreateAccount.setEnabled(!isNewUserEmpty && !isNewPassEmpty && !isNewPassConfirmEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mNewPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNewPassConfirmEmpty = mNewPasswordConfirm.getText().toString().isEmpty();
                mButtonCreateAccount.setEnabled(!isNewUserEmpty && !isNewPassEmpty && !isNewPassConfirmEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onCreateAccountClick(View view) {
        String username = mNewUsername.getText().toString();
        String password = mNewPassword.getText().toString();
        String passwordCfm = mNewPasswordConfirm.getText().toString();

        if (password.equals(passwordCfm)) {
            mAccount = new Account(username, password);
            mInventoryDb.accountDao().insertAccount(mAccount);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.passNoMatch, Toast.LENGTH_LONG).show();
        }
    }
}