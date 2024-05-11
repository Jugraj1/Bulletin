package com.example.app_2100;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getContext() {
        return mContext;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        // Check if the drawable is a vector drawable
        if (drawable instanceof VectorDrawableCompat) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888); // some standard height
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight()); // put drawable on canvas
        drawable.draw(canvas);

        return bitmap;
    }
}
