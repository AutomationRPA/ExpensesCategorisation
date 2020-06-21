package com.example.scantext_gms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    int scanTextOverCamera_requestCode = 1, scanTextFromImage_requestCode = 2;
    private Button captureBillBtn, pickBillFromGallaryBtn, analyseSpends;
    private EditText editTextView;

    private Button getItemsFromText_btn;

    Context context;
    String scannedText;
    List<Product> newProducts;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("AppTest_Main : ", "Executing onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureBillBtn = findViewById(R.id.capture_bill_btn);
        pickBillFromGallaryBtn = findViewById(R.id.pickbillfromgallary_btn);
        //imageView = findViewById(R.id.image_view);
        editTextView = findViewById(R.id.text_display);
        analyseSpends = findViewById(R.id.spendAnalytics);
        getItemsFromText_btn = findViewById(R.id.getItemsFromText_btn);

        context = getApplicationContext();
        scannedText = editTextView.getText().toString();

        //First Run Method
        prefs = getSharedPreferences("com.example.scantext_gms", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false+
            Log.v("AppTest_Main : ", "Firstrun ");
            //requestPermissions();
            JsonReadWrite.initilizeBaseProductsJSON(context);
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        //getItemsFromText_btn.callOnClick();
        captureBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("AppTest_Main : ", "Clicked captureBillBtn");
              /*
               //Print Base Products
               try {
                    Log.v("AppTest_Main : ", "\n\nBaseProducts.json Products\n\n" +display(JsonReadWrite.getObjectsFrombaseJSONFile(context)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                checkPermission(Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_CODE);
                Log.d("Test : MainActivity", "Invoking startCamera()");
                Toast.makeText(MainActivity.this, "Opening Camera", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(MainActivity.this, ScanTextOverCamera.class), scanTextOverCamera_requestCode);
            }
        });

        pickBillFromGallaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("AppTest_Main : ", "Clicked pickBillFromGallaryBtn");
                startActivityForResult(new Intent(MainActivity.this, ScanTextFromImage.class), scanTextFromImage_requestCode);
            }
        });

        getItemsFromText_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("AppTest_Main : ", "Clicked getItemsFromText_btn");

                jsonParseProducts();

                editTextView.setText(scannedText);

                if (!newProducts.isEmpty()) {
                    newProducts = new ArrayList<Product>();
                    //getItemsFromText_btn.setVisibility(View.INVISIBLE);
                    analyseSpends.setVisibility(View.VISIBLE);
                } else {
                    if (scannedText.isEmpty()) {
                        Toast.makeText(MainActivity.this,
                                "Please Scan or Import Receipt",
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Please Check your Receipt",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

        analyseSpends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("AppTest_Main : ", "Clicked analyseSpends");
                //analyseSpends.setVisibility(View.INVISIBLE);
                Intent I = new Intent(MainActivity.this, PieChartActivity.class);
                startActivity(I);
                //getItemsFromText_btn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("AppTest_Main :  Method ", "Executing Method : onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Test : MainActivity", "onActivityResult " + requestCode + " " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    scannedText = data.getStringExtra("DetectedTextFromCamera");
                    break;
                case 2:
                    scannedText = data.getStringExtra("DetectedTextFromImage");
                    break;
                default:
                    Log.e("Test : MainActivity", "requestCode Not Found");
            }
        }
        Log.d("Test : MainActivity", "Result Text:" + scannedText);
        // TODO Update your TextView.
        editTextView.setText(scannedText);
    }

    public void jsonParseProducts() {
        Log.v("AppTest_Main :  Method ", "Executing Method : jsonParseProducts");
        Map<String, Product> baseProductsMapDictionary = new HashMap<String, Product>();
        List<Product> baseProducts = new ArrayList<Product>();

        scannedText = editTextView.getText().toString();
        newProducts = BillFilter.filterBill(scannedText);
        if (newProducts.isEmpty()) {
            Log.v("AppTest_Main :  Method ", "new Products list is empty");
            Toast.makeText(MainActivity.this,
                    "Please Check your Receipt",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            //getObjectsFrombaseJSONFile - Converts base JSON String to Objects
            //Reader reader = Files.newBufferedReader(Paths.get(getApplicationContext().getFilesDir() + "/BaseProducts.json"));
            baseProducts = JsonReadWrite.getObjectsFrombaseJSONFile(context);

            if (baseProducts != null) {
                //convertObjectsoDict - Converts baseObjectsToDictionary
                baseProductsMapDictionary = JsonReadWrite.convertObjectsoDict(baseProducts);
            } else {
                Log.e("AppTest_Main : Json ", "Error : baseProducts is empty  ");
            }

            //getCatogoryFromBaseProductsJSONFile - Assigns known products catogories to its products objects.
            newProducts = JsonReadWrite.assignCategoryFromBaseProductsJSONFile(newProducts, baseProductsMapDictionary);

            //Todo implement Update UserJson File with new products
            //impelemet Read updated user Json File

            //Append the Catogorized new products into UserProducts.Json
            JsonReadWrite.writeObjectsToUserJSONFile(newProducts, context, 1);

            //Design Would you like to save - Dialog in future
        } catch (Exception e) {
            Log.e("AppTest_Main : Json ", "Error : " + e.getMessage());
        }

        Log.v("AppTest_Main : Json ", "Data :\n\n Assigned catogories to new Products\n\n");
        scannedText = display(newProducts);
        Log.v("AppTest_Main : Json ", "Data : \n\n setText-Output \n\n" + scannedText);
    }

    public String display(List<Product> displayProducts) {
        Log.v("AppTest_Main :  Method ", "Executing Method : display");
        //Print Products Scanned along with their Categories
        //Print Products to String
        String newProductsJSONString = "";
        String tempText = "Product                         Price     Category \n"; //Append this in Activity
        for (int i = 0; i < displayProducts.size(); i++) {
            tempText = tempText + displayProducts.get(i).toString2();
            newProductsJSONString = newProductsJSONString + displayProducts.get(i).toString() + ",\n";
            Log.v("AppTest_Main : display ", "Data :\n\n Assigned catogories to new Products\n\n" + newProductsJSONString);
        }
        return tempText;
    }

    public void requestPermissions() {
        Log.v("AppTest_Main :  Method ", "Executing Method : requestPermissions");

        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE);
    }

    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        Log.v("AppTest_Main :  Method ", "Executing Method : checkPermission");
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[]{permission},
                            requestCode);
        } else {
            Toast
                    .makeText(MainActivity.this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
        Log.v("AppTest_Main :  Method ", "Executing Method : onRequestPermissionsResult");
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
