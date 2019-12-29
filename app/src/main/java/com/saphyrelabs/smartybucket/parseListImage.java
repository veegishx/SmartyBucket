package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.OcrLanguages;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.OcrLine;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.OcrRegion;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.OcrResult;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.OcrWord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;

public class parseListImage extends AppCompatActivity {
    private Button extractText;
    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;
    private String result;

    private String subscriptionKey = "d9d6ae59f7a345fe8f57b99436fbd556";
    private String endpoint = "https://smartybucketocrfeature.cognitiveservices.azure.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_list_image);

        imageView = (ImageView) findViewById(R.id.listImageView);
        extractText = (Button) findViewById(R.id.extract_items);
        textView = (TextView) findViewById(R.id.extractedText);

        textView.setText("");

        Uri uri = (Uri) getIntent().getParcelableExtra("resID_uri");
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
            // not sure why this happens, but without this the image appears on its side
            imageView.setRotation(imageView.getRotation() + 90);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create an authenticated Computer Vision client.
        ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        System.out.println("Starting Authenticated Azure ComputerVisionClient...");

        extractText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Recognize printed text with OCR for a local and remote (URL) image
                RecognizeTextOCRLocal(compVisClient);
            }
        });
    }

    public void RecognizeTextOCRLocal(ComputerVisionClient client) {
        System.out.println("-----------------------------------------------");
        System.out.println("RECOGNIZE PRINTED TEXT");

        // Replace this string with the path to your own image.
        new Thread(new Runnable() {
            public void run() {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bitmap.recycle();

                    // Recognize printed text in local image
                    OcrResult ocrResultLocal = client.computerVision().recognizePrintedTextInStream()
                            .withDetectOrientation(true).withImage(byteArray).withLanguage(OcrLanguages.EN).execute();

                    // Print results of local image
                    System.out.println();
                    System.out.println("Recognizing printed text from a local image with OCR ...");
                    System.out.println("\nLanguage: " + ocrResultLocal.language());
                    System.out.printf("Text angle: %1.3f\n", ocrResultLocal.textAngle());
                    System.out.println("Orientation: " + ocrResultLocal.orientation());

                    boolean firstWord = true;
                    // Gets entire region of text block
                    for (OcrRegion reg : ocrResultLocal.regions()) {
                        // Get one line in the text block
                        for (OcrLine line : reg.lines()) {
                            for (OcrWord word : line.words()) {
                                // get bounding box of first word recognized (just to demo)
                                if (firstWord) {
                                    System.out.println("\nFirst word in first line is \"" + word.text()
                                            + "\" with  bounding box: " + word.boundingBox());
                                    firstWord = false;
                                    System.out.println();
                                }
                                System.out.print(" " + word.text() + " ");
                                textView.append(word.text() + " ");
                            }
                            System.out.println();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
