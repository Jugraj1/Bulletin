package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_2100.callbacks.AuthCallback;
import com.example.app_2100.firebase.FirebaseAuthConnection;
import com.google.firebase.auth.FirebaseUser;

/**
 * Noah Vendrig
 */

public class Login extends AppCompatActivity {

    // Test Login details:
    // email: test@gmail.com
    // password: test123

    private static final String TAG = "Login_Screen";

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase authentication
        TextView email = (TextView) findViewById(R.id.activity_create_account_et_email);
        TextView password = (TextView) findViewById(R.id.activity_create_account_et_password);

        Button loginBt = (Button) findViewById(R.id.activity_create_account_bt_create_account);
        TextView signupTv = (TextView) findViewById(R.id.activity_login_tv_signup);

        // Login button onClick handling
        loginBt.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)) {
                signIn(emailText, passwordText);
            } else {
                Toast.makeText(Login.this, "Please enter your email/password", Toast.LENGTH_LONG).show();
            }
        });

        // Sign up "text" (which acts like a button) onClick handling
        signupTv.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, CreateAccount.class));
            finish();
        });
    }

    /**
     * Signs in the user with provided email and password
     *
     * @param email    The email address of the user
     * @param password The password of the user
     */
    private void signIn(String email, String password) {
        FirebaseAuthConnection.getInstance().signIn(email, password, new AuthCallback() {
            @Override
            public void onAuthentication(boolean success) {
                if (success) {
                    // Authentication succeeded
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(Login.this, "Authentication succeeded.", Toast.LENGTH_LONG).show();

                    // We are logged in, so we can access currently logged in user using Firebase
                    FirebaseUser currUser = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
                    if (currUser != null) {
                        startActivity(new Intent(Login.this, HomeFeed.class)); // Go to home page since login is validated
                        finish();
                    } else {
                        Log.w(TAG, "signInWithEmail: logged-in account is null");
                    }
                } else {
                    // Authentication failed
                    Log.w(TAG, "signInWithEmail:failure");
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}