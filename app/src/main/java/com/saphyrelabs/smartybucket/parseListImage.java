package com.saphyrelabs.smartybucket;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class parseListImage extends AppCompatActivity {
    private Button extractText;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_list_image);

        imageView = (ImageView) findViewById(R.id.listImageView);

        Uri uri = (Uri)getIntent().getParcelableExtra("resID_uri");
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
            // not sure why this happens, but without this the image appears on its side
            imageView.setRotation(imageView.getRotation() + 90);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
