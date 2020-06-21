package com.example.scantext_gms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;


public class ScanTextOverCamera extends AppCompatActivity {



    Bitmap imageBitmap;
    String scannedText;
    Intent resultIntent;

    private Button captureButton;
    private SurfaceView cameraView;
    private TextView textBlockContent;
    private CameraSource cameraSource;

    final int RequestCameraPermission = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermission: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("AppTest_STOC : ", "Executing onCreate");
        setContentView(R.layout.camerasurfacelayout);
        //onRequestPermissionsResult(RequestCameraPermission,);
        captureButton = findViewById(R.id.capture_image_btn);
        startCamera();
    }

    public void startCamera() {
        Log.v("AppTest_STOC :  Method ", "Executing Method : startCamera");

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textBlockContent = (TextView) findViewById(R.id.text_value);

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("Test:ScanTextOverCamera", "Detector dependencies are not yet available.");
        }
        Log.w("Test:ScanTextOverCamera", "initiating CameraSource");
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(1.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.v("AppTest_STOC :  Method ", "Executing Method : surfaceCreated");
                try {
                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ScanTextOverCamera.this, new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermission);
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.v("AppTest_STOC :  Method ", "Executing Method : surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
                Log.v("AppTest_STOC :  Method ", "Executing Method : surfaceDestroyed");
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
                Log.v("AppTest_STOC :  Method ", "Executing Method : release");
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                Log.v("AppTest_STOC :  Method ", "Executing Method : receiveDetections");
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textBlockContent.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder value = new StringBuilder();
                            for (int i = 0; i < items.size(); ++i) {
                                TextBlock item = items.valueAt(i);
                                value.append(item.getValue());
                                value.append("\n");
                            }
                            //update text block content to TextView
                            textBlockContent.setText(value.toString());
                            scannedText = value.toString();
                        }
                    });
                }

            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "CaptureButton Pressed");
                Toast.makeText(getApplicationContext(), "CaptureButton Pressed", Toast.LENGTH_SHORT);
                //resultIntent = getParentActivityIntent();
                // intent = new Intent(getApplicationContext() ,MainActivity.class);
                resultIntent = new Intent();
                resultIntent.putExtra("DetectedTextFromCamera", scannedText);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                //startActivity(intent);
                //startActivityForResult();
                //setContentView(R.layout.activity_main);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }
}