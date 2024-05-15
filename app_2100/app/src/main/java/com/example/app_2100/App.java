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
    private static final int BATCH_NUMBER = 15; // number of posts to fetch in each batch for Feeds

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    /***
     * Used for classes that arent acitivities, in which they need application context
     * @return Context of the application
     */
    public static Context getContext() {
        return mContext;
    }

    /***
     *
     * @return Batch number for how many posts to query from database (Homefeed / Following feed
     */
    public static int getBATCH_NUMBER() {
        return BATCH_NUMBER;
    }

    /**
     * Converts a drawable asset into a bitmap, to be used for profile picture displaying (default pfp)
     * @param drawable Drawable to be converted to bitmap
     * @return The drawable as a bitmap
     */
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
