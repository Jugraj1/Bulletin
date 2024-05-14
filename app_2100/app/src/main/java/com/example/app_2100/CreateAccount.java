package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.context.AttributeContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {


    private String emailString;
    private String passwordString;
    private String firstNameString;
    private String lastNameString;

//    Default profile picture variables
    private StorageReference storageReference;
    private String defaultProfilePicture = "1.png";
    private FirebaseStorage storage;
    private File localPfpFile;


//    ------------------

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


//        Set default profile picture
        localPfpFile = new File(App.getContext().getCacheDir(), "pfp.png");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://app-f4755.appspot.com/pfp/1.png"
        );
        downloadProfilePicture();

    }

    private void updateProfileImageView(){
        Bitmap immutableBitmap = BitmapFactory.decodeFile(localPfpFile.getAbsolutePath());
        ShapeableImageView profileImg = findViewById(R.id.activity_create_account_image_view);


//       FIXME: null pointer exception below
        Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(pfpImageBitmap);
        Paint paint = new Paint();

//        paint.setColor(Color.parseColor("#70000000")); // 50% opacity grey for click
        paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey
//        canvas.drawRect(0, 0, pfpImageBitmap.getWidth(), pfpImageBitmap.getHeight(), paint);
        canvas.drawRect(0, 0, profileImg.getWidth(), profileImg.getHeight(), paint);
        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);

//        get element and set the image
        profileImg.setImageBitmap(pfpImageBitmap);
    }



    /**
     * Download the default profile picture
     */
    private void downloadProfilePicture(){
        storageReference.getFile(localPfpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                Log.d(TAG, "Successfully downloaded default profile picture");
                updateProfileImageView();
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(Exception e){
                Log.d(TAG, "Failed to download default profile picture");
            }
        });
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