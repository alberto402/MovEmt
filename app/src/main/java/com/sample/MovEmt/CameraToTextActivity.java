package com.sample.MovEmt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.concurrent.Executor;
import java.text.SimpleDateFormat;





public class CameraToTextActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private String currentPhotoPath = "";

    private class RecognizeTextExecutor implements Executor {

        @Override
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MovEMT_stop_" + timeStamp;
        File storageDir = new File(getFilesDir(), "images");;
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
    
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * RECOGNIZE PRINTED TEXT: Displays text found in image with angle and orientation of
     * the block of text.
     */
    private void recognizeTextOCRLocal(ComputerVisionClient client) {
        new RecognizeTextExecutor().execute(() -> {
            System.out.println("-----------------------------------------------");
            System.out.println("RECOGNIZE PRINTED TEXT");

            // Replace this string with the path to your own image.

            try {
                File rawImage = new File(currentPhotoPath);
                byte[] bytesArray = new byte[(int) rawImage.length()];
                FileInputStream fis = new FileInputStream(rawImage);
                fis.read(bytesArray); //read file into bytes[]
                fis.close();
                OcrResult ocrResultLocal = client.computerVision().recognizePrintedTextInStream()
                        .withDetectOrientation(true).withImage(bytesArray).withLanguage(OcrLanguages.ES).execute();

                runOnUiThread(() -> {
                    File photo = new File(currentPhotoPath);
                    if (photo.exists()) {
                        photo.delete();
                    }
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
                                            EditText stopCode = findViewById(R.id.stopCode);
                                            stopCode.setText(word.text());
                                            numberFound = true;
                                        }
                                        catch (NumberFormatException e){}
                                    }
                                }
                            }
                        }
                        if (!numberFound) {
                            Toast.makeText(getApplicationContext(), getString(R.string.recognize_stop_error), Toast.LENGTH_SHORT).show();
                            EditText stopCode = findViewById(R.id.stopCode);
                            stopCode.setText(getString(R.string.c_digo_de_parada));
                        }
                    }
                });

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parada_texto_imagen);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        ImageView stopImage = findViewById(R.id.stopImage);
        stopImage.setOnClickListener((v) -> {
            getPhoto(v);
        });

        FloatingActionButton btn_back = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        btn_back.setOnClickListener((v) -> {
            onClickBackMain(v);
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

    
    @Override
//Handle the results//
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(getString(R.string.azure_subscription_key)).withEndpoint(getString(R.string.azure_endpoint));
                    recognizeTextOCRLocal(compVisClient);
                }
                break;
            }

        }
    }



}
