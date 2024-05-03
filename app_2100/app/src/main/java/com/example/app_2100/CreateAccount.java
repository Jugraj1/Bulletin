package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.context.AttributeContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {


    private String emailString;
    private String passwordString;
    private String firstNameString;
    private String lastNameString;

    private static final String TAG = "CreateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


//        Set up the create account button
        Button createAccountButton = findViewById(R.id.activity_create_account_bt_create_account);
        createAccountButton.setOnClickListener(v -> createAccountButtonPressed());


//        Set up the cancel button
        TextView cancelTextView = findViewById(R.id.activity_create_account_bt_cancel);
        cancelTextView.setOnClickListener(v -> cancelButtonPressed());
    }


    /**
     * Run when cancel button is pressed
     * Redirect to MainActivity
     */
    private void cancelButtonPressed(){
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Run when create account button is pressed
     * Validate all fields
     * Create account if all fields are valid
     * Redirect to HomeFeed if successful
     */
    private void createAccountButtonPressed(){
       if(validateAllFields()){
              // Create account
              // Add account to database
              // Redirect to login page

              // Create account
           createAccount();
       }
    }

    /**
     * Create account with email and password
     * Redirect to HomeFeed if successful
     * Display error message if unsuccessful
     */
    private void createAccount(){
//        Can only create account with email and password.
        Log.d(TAG, "createAccount() started");
        FirebaseAuthConnection.getInstance().createAccount(emailString, passwordString, firstNameString, lastNameString, createAccountCallback());
    }

    /**
     * Callback for createAccount specify what happens after account creation
     * Redirect to HomeFeed if successful
     * Display error message if unsuccessful
     * @return
     */
    private AuthCallback createAccountCallback (){
        return new AuthCallback() {
            @Override
            public void onAuthentication(boolean success) {
                if (success) {

                    Log.w(TAG, "createUserWithEmail:success");
                    Toast.makeText(CreateAccount.this, "Account Creation succeeded.", Toast.LENGTH_LONG).show();

//                    Redirect to HomeFeed
                    startActivity(new Intent(CreateAccount.this, HomeFeed.class));
                } else {
                    Log.w(TAG, "createUserWithEmail:failure");
                    Toast.makeText(CreateAccount.this, "Account Creation failed.",
                            Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    /**
     * Validate all fields
     * @return Account object if all fields are valid, null otherwise
     */
    private boolean validateAllFields(){
//        Get all the fields
        EditText emailEditText = findViewById(R.id.activity_create_account_et_email);
        EditText passwordEditText = findViewById(R.id.activity_create_account_et_password);
        EditText firstNameEditText = findViewById(R.id.activity_create_account_et_first_name);
        EditText lastNameEditText = findViewById(R.id.activity_create_account_et_last_name);

        emailString = emailEditText.getText().toString();
        passwordString = passwordEditText.getText().toString();
        firstNameString = firstNameEditText.getText().toString();
        lastNameString = lastNameEditText.getText().toString();


//        Validate for empty fields
        if (emailString.isEmpty() || passwordString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty()){
//            Tell user to fill in all fields
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

//        validate emailString format
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(emailString);

        if(!mat.matches()){
//            Tell user email is invalid
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }

//        validate passwordString
        if (passwordString.length() < 6){
//          Tell user password is too short
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}