package com.example.scantext_gms;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReadWrite {

    public static void initilizeBaseProductsJSON(Context context) {
        Log.v("AppTest_JRW method : ", "Executing Method : initilizeBaseProductsJSON");
        //Todo Optimize the files
        //String jsonFileInApp_String = Utils.getJsonFromAssets(getApplicationContext(), "BaseProducts.json");
        // Log.v("AppTest_JRW: Productdata", jsonFileInApp_String);

        try {
            //Creates a public directory for app.
            Environment.getExternalStoragePublicDirectory("DIRECTORY_DOCUMENTS");//Creates a public directory for app.


            InputStream is2 = context.getAssets().open("BaseProducts.json");
            Log.v("AppTest_JRW : Paths", is2.toString() + "Executing getApplicationContext().getExternalFilesDir(DIRECTORY_DOCUMENTS) ");
            File destination = new File(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/BaseProducts.json");
            Log.v("AppTest_JRW : Paths", is2.toString() + "  " + destination.toString());
            JsonReadWrite.copyFileUsingStream(is2, destination);
            is2.close();


            //Initilise UserProducts.json
            InputStream is4 = context.getAssets().open("UserProducts.json");
            File directory_documents = Environment.getExternalStoragePublicDirectory("DIRECTORY_DOCUMENTS");//Creates a public directory for app.
            Log.v("AppTest_JRW : Paths", is4.toString() + "Executing getApplicationContext().getExternalFilesDir(null) ");
            destination = new File(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/UserProducts.json");
            Log.v("AppTest_JRW : Paths", is4.toString() + "  " + destination.toString());
            JsonReadWrite.copyFileUsingStream(is4, destination);
            is4.close();

        } catch (Exception e) {
            Log.v("AppTest_JRW : Productdata", "ErrorCopying file : " + e.getMessage());
        }
    }

    public static void copyFileUsingStream(InputStream source, File dest) throws IOException {
        Log.v("AppTest_JRW method : ", "Executing Method : copyFileUsingStream");

        InputStream is = source;
        OutputStream os = null;
        try {
            // is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static List<Product> assignCategoryFromBaseProductsJSONFile(List<Product> products, Map<String, Product> mapDictionary) {
        //getCatogoryFromBaseProductsJSONFile - Assigns catogory of known products to products Object.
        Log.v("AppTest_JRW method : ", "Executing Method :  assignCategoryFromBaseProductsJSONFile");
        for (int i = 0; i < products.size(); i++) {
            if (mapDictionary.containsKey(products.get(i).name.toLowerCase())) {
                products.get(i).catogory = (mapDictionary.get(products.get(i).name.toLowerCase())).catogory;
            }
        }
        return products;
    }

    public static Map<String, Product> convertObjectsoDict(List<Product> products) {
        //convertObjectsoDict - Converts baseObjectsToDictionary
        Log.v("AppTest_JRW method : ", "Executing Method : convertObjectsoDict");

        //List<Product> newProducts = BillFilter.FilterBill(scannedText);
        Map<String, Product> newMapDictionary = new HashMap<String, Product>();

        for (int i = 0; i < products.size(); i++) {
            Product temp = products.get(i);
            newMapDictionary.put(products.get(i).name.toLowerCase(), temp);
            Log.v("AppTest_JRW : DictionaryValues", "Key : " + newMapDictionary.get(i) + " Values : " + newMapDictionary.get(products.get(i).name.toLowerCase()).catogory + "  " + newMapDictionary.get(products.get(i).name.toLowerCase()).name + "  " + newMapDictionary.get(products.get(i).name.toLowerCase()).price);
        }
        return newMapDictionary;
    }

    //Read - Write Methods
    public static List<Product> getObjectsFrombaseJSONFile(Context context) throws IOException {
        Log.v("AppTest_JRW method : ", "Executing Method : getObjectsFrombaseJSONFile");
        //getObjectsFrombaseJSONFile - Converts base JSON String to Objects
        Reader reader = Files.newBufferedReader(Paths.get(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/BaseProducts.json"));
        List<Product> baseProducts = new ArrayList<Product>();
        try {
            Gson gson = new Gson();
            Type listProductType = new TypeToken<List<Product>>() {
            }.getType();
            baseProducts = gson.fromJson(reader, listProductType);

        } catch (Exception e) {
            Log.e("AppTest_JRW : Json ", "Error : getObjectsFrombaseJSONFile" + e.getMessage());
        }
        return baseProducts;
    }

    public static List<Product> getObjectsFromUserJSONFile(Context context) {
        Log.v("AppTest_JRW method : ", "Executing Method : getObjectsFromUserJSONFile");

        //Read UserProducts.Json File and convert to Object
        List<Product> userProducts = new ArrayList<Product>();
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/UserProducts.json")));
            Type listProductType = new TypeToken<List<Product>>() {
            }.getType();
            userProducts = gson.fromJson(reader, listProductType);

        } catch (Exception e) {
        }
        return userProducts;
    }

    public static void writeObjectsToBaseJSONFile() {
        Log.v("AppTest_JRW method : ", "Executing Method : writeObjectsToBaseJSONFile");

        try {

        } catch (Exception e) {
        }
    }

    public static void writeObjectsToUserJSONFile(List<Product> newProducts, Context context, Integer Append) {
        Log.v("AppTest_JRW method : ", "Executing Method : writeObjectsToUserJSONFile");
        Log.v("AppTest_JRW Append", "\nNew Products to be added to Json\n" + newProducts.toString());
        if (newProducts.isEmpty()) {
            Log.v("AppTest_JRW Append", "\nNew Products is Empty\n" + newProducts.toString());
            return;
        }
        try {

            Writer writer;
            if (Append == 1) {
                List<Product> allUserProducts = new ArrayList<Product>();
                //Read UserProducts.Json File and convert to Object
                allUserProducts = getObjectsFromUserJSONFile(context);
                //Append UserProducts & new products to the list
                if (allUserProducts!=null) {
                    Log.v("AppTest_JRW Append", "Appending Data");
                    allUserProducts.addAll(newProducts);
                }else
                    allUserProducts = newProducts;

                Log.v("AppTest_JRW Appen", "Context : " + context.toString());
                writer = new FileWriter(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/UserProducts.json");
                new Gson().toJson(allUserProducts, writer);
            } else {
                writer = new FileWriter(context.getApplicationContext().getExternalFilesDir("DIRECTORY_DOCUMENTS") + "/UserProducts.json");
                new Gson().toJson(newProducts, writer);
            }
            writer.close();
        } catch (Exception e) {
            Log.e("AppTest_JRW : Json ", "Error : writeObjectsToUserJSONFile" + e.getMessage());
        }
    }
}

