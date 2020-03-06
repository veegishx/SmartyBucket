package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class RecipeDetails extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private ImageView imageView;
    private TextView appbar_title, appbar_subtitle, title;
    private boolean hiddenToolbarView = false;
    private LinearLayout titleAppBar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl, mImg, mTitle, mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout = findViewById(R.id.appbar);
        titleAppBar = findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appbar_title = findViewById(R.id.title_on_appbar);
        appbar_subtitle = findViewById(R.id.subtitle_on_appbar);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mImg = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("label");
        mSource = intent.getStringExtra("source");

        RequestOptions requestOptions = new RequestOptions();

        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appbar_title.setText(mSource);
        appbar_subtitle.setText(mUrl);
        title.setText(mTitle);

        initWebView(mUrl);

    }

    private void initWebView(String url) {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) maxScroll;

        if (percentage == 1f && hiddenToolbarView) {
            titleAppBar.setVisibility(View.GONE);
            hiddenToolbarView = !hiddenToolbarView;
        } else if (percentage < 1f && hiddenToolbarView) {
            titleAppBar.setVisibility(View.VISIBLE);
            hiddenToolbarView = !hiddenToolbarView;
        }
    }
}
