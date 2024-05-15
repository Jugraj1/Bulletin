package com.example.app_2100;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleWebViewer extends AppCompatActivity {
    public static final String TAG = "ArticleWebViewer";
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_viewer);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient());
        }
        else{
            webView.loadUrl("https://www.google.com.au/");
            webView.setWebViewClient(new WebViewClient());
            Log.e(TAG, "LINK IS NULL");
        }
    }
}