//https://www.youtube.com/watch?v=7qw-zl9XGd4
//https://github.com/eddydn/TextRecognitionImage

package com.example.scantext_gms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ScanTextFromImage extends AppCompatActivity {

    ImageView imageView;
    Button btnProcess;
    TextView txtResult;
    Bitmap bitmap;
    private static int IMG_RESULT = 1;
    Intent intent, resultIntent;
    String ImageDecode, scannedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("AppTest_STFI : ", "Executing onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanimagefromgallary);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnProcess = (Button) findViewById(R.id.button);
        txtResult = (TextView) findViewById(R.id.textView);

        ImageButton LoadImage = (ImageButton) findViewById(R.id.button_SelectImage);
        //Initial Preview - Select Image From Gallary
        // bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.text_recognition);
        //imageView.setImageBitmap(bitmap);

        LoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("AppTest_STFI : ", "Clicked LoadImage");

                //      intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //      startActivityForResult(intent, IMG_RESULT);
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMG_RESULT);
                //      startActivityForResult(intent, IMG_RESULT);

            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("AppTest_STFI : ", "Clicked btnProcess");

                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational())
                    Log.e("ERROR", "Detector dependencies are not yet available");
                else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    txtResult.setText(stringBuilder.toString());
                    scannedText = stringBuilder.toString();

                    resultIntent = new Intent();
                    resultIntent.putExtra("DetectedTextFromImage", scannedText);
                    setResult(Activity.RESULT_OK, resultIntent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("AppTest_STFI :  Method ", "Executing Method : onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Test", "onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Log.i("Test", "onActivityResult-try");
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
