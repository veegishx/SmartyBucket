package com.saphyrelabs.smartybucket;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.saphyrelabs.smartybucket.tflite.Classifier;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class ScanType extends AppCompatActivity {
    private CardView scanListCard;
    private CardView scanIngredientCard;
    private int scanType = 0;
    public static final int REQUEST_PERMISSION = 300;
    public static final int REQUEST_IMAGE = 100;

    private Uri imageUri;

    private String choice;

    private boolean quant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_options);

        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        scanIngredientCard = (CardView) findViewById(R.id.scan_ingredient_card);
        scanIngredientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences myPreferences = getSharedPreferences("userConfigurations", MODE_PRIVATE);
                String modelType = myPreferences.getString("modelType",null);
                scanType = 1;
                Intent i = new Intent(ScanType.this, ClassifierActivity.class);
                startActivity(i);
//                if (modelType.equals("float")) {
//                    choice = "new_mobile_model.tflite";
//                    quant = false;
//                    openCameraIntent();
//                } else  {
//                    choice = "inception_quant.tflite";
//                    quant = true;
//                    openCameraIntent();
//                }


            }
        });

        scanListCard = (CardView) findViewById(R.id.scan_list_card);
        scanListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
                scanType = 1;
            }
        });
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
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
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
            // put filename in extras
            i.putExtra("choice", choice);
            // put model type in extras
            i.putExtra("quant", quant);
            // send other required data
            startActivity(i);
        }
    }
}
