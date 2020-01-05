package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
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
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class parseListImage extends AppCompatActivity {
    private String edamamAppId = BuildConfig.EDAMAM_APP_ID;
    private String adamamKey = BuildConfig.EDAMAM_API_KEY;
    private Button extractText;
    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;
    private String result;
    private static final String TAG = parseListImage.class.getSimpleName();
    private ArrayList<String> listOfIngredients = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_list_image);

        imageView = (ImageView) findViewById(R.id.listImageView);
        extractText = (Button) findViewById(R.id.extract_items);
        textView = (TextView) findViewById(R.id.extractedText);

        textView.setText("");

        Uri uri = (Uri) getIntent().getParcelableExtra("resID_uri");
        System.out.println("URL: " + uri);
        try {
            bitmap = handleSamplingAndRotationBitmap(this, uri);
            imageView.setImageBitmap(bitmap);
            // Image needs to be taken in landscape mode in order for OCR to work
            // Rotation has no effect on OCR feature for some reason
           // May be related to this: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
            imageView.setRotation(imageView.getRotation() + 90);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap2 = bitmapDrawable.getBitmap();



        // Create an authenticated Computer Vision client.
        // ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        // System.out.println("Starting Authenticated Azure ComputerVisionClient...");

        extractText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Recognize printed text with OCR for a local and remote (URL) image
                //RecognizeTextOCRLocal(compVisClient);
                try {
                    performOCR(bitmap2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Perform Image Analysis on separate thread
    public void performOCR(Bitmap bitmap2) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                    if (!recognizer.isOperational()) {
                        Toast.makeText(parseListImage.this, "Error", Toast.LENGTH_LONG).show();
                        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                        boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                        if (hasLowStorage) {
                            Toast.makeText(parseListImage.this, R.string.error_low_storage, Toast.LENGTH_LONG).show();
                            Log.w(TAG, getString(R.string.error_low_storage));
                        }
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap2).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();

                        System.out.println("TEXT DETECTION");
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock myItem = items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                            System.out.println("TEXT DETECTED: " + sb.toString());
                            listOfIngredients.add(sb.toString());
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t1.join();

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < listOfIngredients.size(); i++) {
            line.append(listOfIngredients.get(i) + ", ");
        }

        AlertDialog alertDialog = new AlertDialog.Builder(parseListImage.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(line);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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

    /**
     * Source: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
