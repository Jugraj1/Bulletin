package com.example.app_2100;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.util.Log;

public class HomeFeed extends AppCompatActivity {
    private static final String TAG = "HomeFeed_Screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);


        ImageButton profilePicIb = findViewById(R.id.activity_home_feed_ib_profile);

        profilePicIb.setOnClickListener(v -> {
            Log.d(TAG, "profile pib ib clicked");
        });
        createProfilePic();
    }

    private void createProfilePic(){
        Bitmap squareImageBitmap = createDummyBitmap(200, 200);

        // Combine square image with circular corner drawable
        Bitmap roundedImageBitmap = Bitmap.createBitmap(squareImageBitmap.getWidth(), squareImageBitmap.getHeight(), squareImageBitmap.getConfig());
        Canvas canvas = new Canvas(roundedImageBitmap);
        Paint paint = new Paint();
        Drawable drawable = getResources().getDrawable(R.drawable.circular_corner);
        drawable.setBounds(0, 0, squareImageBitmap.getWidth(), squareImageBitmap.getHeight());
        drawable.draw(canvas);
        canvas.drawBitmap(squareImageBitmap, 0f, 0f, paint);

        // Set rounded image as background of the ImageButton
        ImageButton imageButton = findViewById(R.id.activity_home_feed_ib_profile);
        BitmapDrawable roundedImageDrawable = new BitmapDrawable(getResources(), roundedImageBitmap);
        imageButton.setBackground(roundedImageDrawable);
    }

    private Bitmap createDummyBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLUE);
        return bitmap;
    }
}