package com.example.app_2100;

import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {


    private String emailString;
    private String passwordString;
    private String firstNameString;
    private String lastNameString;

//     profile picture variables
    private StorageReference defaultPfpRef;
    private String defaultProfilePicture = "1.png";
    private ActivityResultLauncher<Intent> takePhotoLauncher;



//    Firebase variables to upload profile picture
    private StorageReference pfpRef;
    private FirebaseStorage storage;
    private File localPfpFile;

    private Uri localURI;
    private String userId;
    private boolean defaultPicture = true;

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


//        Set up the select image button=
        ImageButton selectImageButton = findViewById(R.id.activity_create_account_bt_choose_image);
        selectImageButton.setOnClickListener(v -> selectImageButtonPressed());

//      This is disabled because it does not work
////        Set up the take photo button
//        ImageButton takePictureButton = findViewById(R.id.activity_create_account_bt_take_photo);
//        takePictureButton.setOnClickListener(v -> takePhotoButtonPressed());


//        Set default profile picture
        localPfpFile = new File(App.getContext().getCacheDir(), "pfp.png");
        storage = FirebaseStorage.getInstance();
        defaultPfpRef = storage.getReferenceFromUrl("gs://app-f4755.appspot.com/pfp/1.png");


//        Set up the take photo button
        downloadProfilePicture();


//        FIXME: this take photo stuff doesn't work. IT is a low priority so only fix it later if we have time
//        FIXME: this requires android sdk leevel 34 or higher. our app is on 33
//        It is disabled for now
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");

//                        update the profile picture with the selected image
                        if(imageBitmap != null) {
                            updateProfileImageView(imageBitmap);
                            defaultPicture = false;
                        }
                    }
                });
    }

    /**
     * Run when select image button is pressed to select an image from the gallery
     */
    private void selectImageButtonPressed(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 100);
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }

    /**
     * Used to select an image from the gallery
     */
    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {

                    localURI = uri;

                    localPfpFile = new File(Objects.requireNonNull(uri.getPath()));
                    Bitmap immutableBitmap = null;

                    try {
//                        update the profile picture with the selected image
                        immutableBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        updateProfileImageView(immutableBitmap);
                        defaultPicture = false;
                    } catch (NullPointerException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d("Bitmap", "Selected file: " + immutableBitmap.toString());

//                    updateProfileImageView(immutableBitmap);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


    /**
     * Upload the profile picture to the storage
     * The upload picture will use the localURI
     */
    private void uploadProfilePicture(){
//        If default picture, do not upload
        if(defaultPicture){
            return;
        }

//        gs://app-f4755.appspot.com/pfp/DKWN4xhSpKQVkKdhpIcWLIivIkE2.jpg
//        gs://app-f4755.appspot.com/pfp/NuMnoRO7crd8bcqL4H4VrEZ74iY2.jpg
//        gs://app-f4755.appspot.com/pfp/6Mck1J6naYT5QccTudHaqZEjJU82.jpg
//        gs://app-f4755.appspot.com/pfp/1.png
        pfpRef = storage.getReferenceFromUrl("gs://app-f4755.appspot.com/pfp/" + userId + ".jpg");

        Log.d(TAG, "user id: " + userId);
        Log.d(TAG, "Uploading profile picture: " + pfpRef.toString());


        pfpRef.putFile(localURI).addOnSuccessListener(new OnSuccessListener(){
            @Override
            public void onSuccess(Object o) {
               Log.d(TAG,  "Successfully uploaded profile picture");
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Failed to upload profile picture"));
    }


    /**
     * Run when take photo button is pressed
      */
    private void takePhotoButtonPressed(){

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Launch the camera to take a photo.

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoLauncher.launch(takePictureIntent);
    }


    /**
     * Update the profile picture with the selected image
     */
    private void updateProfileImageView(Bitmap immutableBitmap){
        ShapeableImageView profileImg = findViewById(R.id.activity_create_account_image_view);


        Bitmap pfpImageBitmap = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(pfpImageBitmap);
        Paint paint = new Paint();

//        paint.setColor(Color.parseColor("#70000000")); // 50% opacity grey for click
        paint.setColor(Color.parseColor("#00ffffff")); // 50% opacity grey

//        account for different aspect ratio. and choose the one that fills the entire circle
        int width = pfpImageBitmap.getWidth();
        int height = pfpImageBitmap.getHeight();
        float aspectRatio = (float) width /height;

        int dpwidth = profileImg.getWidth();
        int dpheight = profileImg.getWidth();

        if(aspectRatio > 1){
//            Image is wider than it is tall
            dpheight = profileImg.getHeight();
            dpwidth = (int) (dpheight * aspectRatio);
            Log.d("Aspect ratio" , "WIDE: " + aspectRatio);
        } else {
//            Image is taller than it is wide
            dpwidth = profileImg.getWidth();
            dpheight = (int) (dpwidth / aspectRatio);
            Log.d( "Aspect ratio" , "TALL: " + aspectRatio);
        }



        canvas.drawRect(0, 0, dpwidth, dpheight, paint);


        canvas.drawBitmap(pfpImageBitmap, 0f, 0f, paint);

//        get element and set the image
        profileImg.setImageBitmap(pfpImageBitmap);
    }



    /**
     * Download the default profile picture
     */
    private void downloadProfilePicture(){
        defaultPfpRef.getFile(localPfpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                Log.d(TAG, "Successfully downloaded default profile picture");
                Bitmap immutableBitmap = BitmapFactory.decodeFile(localPfpFile.getAbsolutePath());
                updateProfileImageView(immutableBitmap);
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

        FirebaseAuthConnection.getInstance().createAccount(emailString, passwordString, firstNameString, lastNameString, defaultPicture,createAccountCallback());

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

//                    get user id and upload picture
                    userId = FirebaseAuthConnection.getCurrentUser().getUid();
                    uploadProfilePicture();



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