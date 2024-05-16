package com.example.app_2100;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Adith Iyer
 */

public class ArticleWebViewer extends AppCompatActivity {
    public static final String TAG = "ArticleWebViewer";

    /**
     * Called when the activity is first created.
     * Sets up the WebView, enables JavaScript, and loads the URL if valid
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied. Otherwise, it is null
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_viewer);

        // Initialize the WebView and enable JavaScript
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        // Get the URL passed via the intent
        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            // Check if the URL is valid
            if (isValidUrl(url)) {
                // Load the valid URL
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(url);
            } else {
                // Redirect to Google if the URL is invalid
                redirectToGoogle(webView);
                Log.e(TAG, "INVALID URL");
            }
        } else {
            // Redirect to Google if the URL is null or empty
            redirectToGoogle(webView);
            Log.e(TAG, "LINK IS NULL");
        }
    }

    /**
     * Validates the URL using a regular expression pattern.
     *
     * @param url The URL to be validated.
     * @return true if the URL is valid, false otherwise.
     */
    private boolean isValidUrl(@NonNull String url) {
        return android.util.Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * Redirects the WebView to the Google homepage.
     *
     * @param webView The WebView instance to be redirected.
     */
    private void redirectToGoogle(WebView webView) {
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com.au/");
    }
}

