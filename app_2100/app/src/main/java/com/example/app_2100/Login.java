package com.example.app_2100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // Test Login details:
    // email: test@gmail.com
    // password: test123

    private FirebaseAuth mAuth;
    private static final String TAG = "Login_Screen";

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuthClass.getCurrentUser();
        if(currentUser != null){
            // reload();
            Log.d(TAG, "logged in already");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Initialize Firebase Auth
        mAuth = FirebaseAuthClass.getAuth();


        TextView email = (TextView) findViewById(R.id.activity_login_et_email);
        TextView password = (TextView) findViewById(R.id.activity_login_et_password);

        Button loginBt = (Button) findViewById(R.id.activity_login_bt_login);
        TextView signupTv = (TextView) findViewById(R.id.activity_login_tv_signup);

        loginBt.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)){
                signIn(emailText, passwordText);
            } else {
                Toast.makeText(Login.this, "Please enter your email/password",
                        Toast.LENGTH_LONG).show();
            }
        });

        signupTv.setOnClickListener(v -> {
            createAccount("email@email.com", "password"); // leave it as does nothing
        });
    }

    private void signIn(String email, String password) {

        FirebaseAuthClass.getInstance().signIn(email, password, new FirebaseAuthClass.AuthCallback() {
            @Override
            public void onAuthentication(boolean success) {
                if (success) {

                    // authentication success
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(Login.this, "Authentication succeeded.",
                            Toast.LENGTH_LONG).show();

                    FirebaseUser currUser = FirebaseAuthClass.getInstance().getAuth().getCurrentUser();
                    if (currUser != null){
                        User user = User.getCurrent();
                        user.setUserID(currUser.getUid());
                    }
                    startActivity(new Intent(Login.this, HomeFeed.class));

                } else {
                    // authentication failed
                    Log.w(TAG, "signInWithEmail:failure");
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAccount(String email, String password) {
       FirebaseAuthClass.getInstance().createAccount(email, password, new FirebaseAuthClass.AuthCallback() {
           @Override
           public void onAuthentication(boolean success) {
               if (success) {
                   // authentication success
                   Log.d(TAG, "createUserWithEmail:success");
                   Toast.makeText(Login.this, "Authentication succeeded.",
                           Toast.LENGTH_LONG).show();
               } else {
                   // authentication failed
                   Log.w(TAG, "createUserWithEmail:failure");
                   Toast.makeText(Login.this, "Authentication failed.",
                           Toast.LENGTH_LONG).show();
               }
           }
       });






//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "createUserWithEmail:success");
//                        Toast.makeText(Login.this, "Authentication succeeded.",
//                                Toast.LENGTH_SHORT).show();
//                        FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                        Toast.makeText(Login.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                    }
//                });
    }
}