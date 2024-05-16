package com.example.app_2100;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private static Context mContext;
    private final static String TAG = "App";
    private static final int BATCH_NUMBER = 15; // number of posts to fetch in each batch for Feeds

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    /**
     * Read the lines from the text file
     * @param filename Filename for the textfile to read
     * @return List of each line
     */
    public static List<String> readTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getContext().getAssets().open(filename)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading filename: " + filename, e);
        }
        return lines;
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
