package com.sample.MovEmt.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;
import com.sample.MovEmt.R;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.fragment.LoadingDialogFragment;
import org.json.JSONObject;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;


public class EnterStopActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private String currentPhotoPath = "";
    private Button stopSearch;
    private EditText stopCode;
    private boolean info = false;


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MovEMT_stop_" + timeStamp;
        File storageDir = new File(getFilesDir(), "images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpeg",         /* suffix */
            storageDir      /* directory */
        );
    
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            ExifInterface exif = new ExifInterface(
                    imagePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /**
     * RECOGNIZE PRINTED TEXT: Displays text found in image with angle and orientation of
     * the block of text.
     */
    private void recognizeTextOCRLocal(ComputerVisionClient client, LoadingDialogFragment dialog, File photo) {
        Executors.newSingleThreadExecutor().execute(() -> {
            System.out.println("-----------------------------------------------");
            System.out.println("RECOGNIZE PRINTED TEXT");

            // Replace this string with the path to your own image.

            try {
                Bitmap bmp = BitmapFactory.decodeFile(photo.getAbsolutePath());
                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(photo.getAbsolutePath()));
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        bmp.getHeight(), matrix, true);
                FileOutputStream out = new FileOutputStream(photo);
                if (bmp.getHeight() > 1024 || bmp.getWidth() > 1024)
                    bmp = Bitmap.createScaledBitmap(bmp, 1024, 1024, true);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                byte[] bytesArray = new byte[(int) photo.length()];
                FileInputStream fis = new FileInputStream(photo);
                fis.read(bytesArray); //read file into bytes[]
                fis.close();
                OcrResult ocrResultLocal = client.computerVision().recognizePrintedTextInStream()
                        .withDetectOrientation(true).withImage(bytesArray).withLanguage(OcrLanguages.ES).execute();


                if (ocrResultLocal != null) {
                    System.out.println();
                    System.out.println("Recognizing printed text from a local image with OCR ...");
                    System.out.println("\nLanguage: " + ocrResultLocal.language());
                    System.out.printf("Text angle: %1.3f\n", ocrResultLocal.textAngle());
                    System.out.println("Orientation: " + ocrResultLocal.orientation());

                    // Gets entire region of text block
                    boolean numberFound = false;
                    for (OcrRegion reg : ocrResultLocal.regions()) {
                        // Get one line in the text block
                        for (OcrLine line : reg.lines()) {
                            for (OcrWord word : line.words()) {
                                // get bounding box of first word recognized (just to demo)
                                if ((word.text().length() >= 3 && word.text().length() <= 4)) {
                                    try {
                                        Integer.parseInt(word.text());
                                        URL url = new URL(String.format(EndPoint.INFO_STOP, word.text()));
                                        HttpURLConnection con = null;

                                        con = (HttpURLConnection) url.openConnection();
                                        con.setRequestMethod("GET");

                                        //headers
                                        con.setRequestProperty("accessToken", Authentication.accessToken);
                                        con.setRequestProperty("Content-Type", "application/json; utf-8");
                                        con.setRequestProperty("Accept", "application/json");

                                        int status = con.getResponseCode();

                                        if (status == HttpURLConnection.HTTP_OK){
                                            String response = new ResponseReader().getResponse(con);
                                            JSONObject res = new JSONObject(response);
                                            if ("00".equals(res.getString("code"))) {
                                                runOnUiThread(() -> {
                                                    stopCode.setText(word.text());
                                                });
                                                numberFound = true;
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                    }
                                }
                            }
                        }
                    }
                    dialog.dismiss();
                    if (!numberFound) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), getString(R.string.recognize_stop_error), Toast.LENGTH_SHORT).show();
                            EditText stopCode = findViewById(R.id.stopCodeText);
                            stopCode.setText("");
                        });
                    }
                }
                photo.delete();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                dialog.dismiss();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), getString(R.string.recognize_stop_error), Toast.LENGTH_SHORT).show();
                    EditText stopCode = findViewById(R.id.stopCodeText);
                    stopCode.setText("");
                });
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        info = extras.getBoolean("info",false);
        setContentView(R.layout.activity_enter_stop_view);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
        }

        Button stopButton= findViewById(R.id.capturePhotoButton);
        stopButton.setOnClickListener((v) -> {
            getPhoto(v);
        });

        Button btnBack = findViewById(R.id.Back);
        btnBack.setOnClickListener((v) -> {
            onClickBackMain(v);
        });

        stopCode = findViewById(R.id.stopCodeText);
        stopSearch = findViewById(R.id.searchStopButton);
        stopSearch.setOnClickListener(this::onClickSearch);
    }


    private void onClickSearch(View v){
        runOnUiThread(() -> {
            String text = stopCode.getText().toString();
            if(text.equals(""))
                return;

            try {
                int stopCode = Integer.parseInt(text);
                Intent intent;
                if(info)
                {
                     intent = new Intent(v.getContext(), StopInfoActivity.class);
                }
                else {
                     intent = new Intent(v.getContext(), StopBusesActivity.class);
                }
                intent.putExtra("stopNumber", stopCode);
                startActivityForResult(intent, 0);

            } catch (Exception e){
                Log.e("parada_texto_imagen", text + " is NAN.");
            }
        });
    }


    void onClickBackMain(View v){
        Intent intent = new Intent (v.getContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }

    public void getPhoto(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                                                    "com.sample.MovEmt.fileprovider",
                                                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE);
            }
        }

    }

    // Handle the result of capturing the photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQUEST_CODE: {
                File currentPhoto = new File(currentPhotoPath);
                if (currentPhoto.length() > 0) {
                    LoadingDialogFragment loadingDialog = new LoadingDialogFragment();
                    loadingDialog.show(getSupportFragmentManager(), "LoadingDialogFragment");

                    ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(getString(R.string.azure_subscription_key)).withEndpoint(getString(R.string.azure_endpoint));
                    recognizeTextOCRLocal(compVisClient, loadingDialog, currentPhoto);
                }
                else if (currentPhoto.exists()) {
                    currentPhoto.delete();
                }
                break;
            }
        }
    }
}
