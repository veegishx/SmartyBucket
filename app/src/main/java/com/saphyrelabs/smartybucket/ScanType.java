package com.saphyrelabs.smartybucket;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class ScanType extends AppCompatActivity {
    private CardView scanListCard;
    private CardView scanIngredientCard;
    private static final int REQUEST_PERMISSION = 300;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_IMAGE = 100;

    private Uri imageUri;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_options);

        scanIngredientCard = findViewById(R.id.scan_ingredient_card);
        scanIngredientCard.setOnClickListener(view -> {
            if (checkCameraPermissions()) {
                Intent i = new Intent(ScanType.this, ClassifierActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs camera permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
                requestCameraPermission();
            }
        });

        scanListCard = findViewById(R.id.scan_list_card);
        scanListCard.setOnClickListener(view -> {
            if (checkCameraPermissions()) {
                if (checkReadPermissions()) {
                    if (checkWritePermissions()) {
                        openCameraIntent();
                    } else {
                        Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs write permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
                        requestWritePermission();
                    }
                } else {
                    Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs read permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
                    requestReadPermission();
                }
            } else {
                Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs camera permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
                requestCameraPermission();
            }
        });

        // Initialize BottomAppBar
        bottomNav = findViewById(R.id.bottom_navigation);

        // Handle onClick event
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.homeNav:
                    Intent home = new Intent(ScanType.this, MainActivity.class);
                    startActivity(home);
                    break;
                case R.id.scanNav:
                    Intent scanType = new Intent(ScanType.this, ScanType.class);
                    startActivity(scanType);
                    break;
            }
            return false;
        });
    }

    public boolean checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs camera permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkReadPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs read permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean checkWritePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ScanType.this.getApplicationContext(),"This feature needs write permissions to run. Please enable this permission first",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestReadPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openCameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        }
    }

    // dictates what to do after the user takes an image, selects and image, or crops an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // if the camera activity is finished, obtained the uri, crop it to make it square, and send it to 'parseItemImage' activity
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri source_uri = imageUri;
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                // need to crop it to square image as CNN's always required square input
                Crop.of(source_uri, dest_uri).asSquare().start(ScanType.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if cropping acitivty is finished, get the resulting cropped image uri and send it to 'parseItemImage' or 'parseListImage' activity, depending on user choice of scan
        else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            imageUri = Crop.getOutput(data);
            Intent i = new Intent(ScanType.this, parseListImage.class);
            // put image data in extras to send
            i.putExtra("resID_uri", imageUri);
            // send other required data
            startActivity(i);
        }
    }
}
