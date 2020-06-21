package com.example.scantext_gms;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpendAnalytics {

    public static Map<String, Float> analyseSpends(List<Product> products) {
        Log.v("AppTest_Main :  Method ", "Executing Method : analyseSpends");

        Map<String, Float> catogorySpendsMap = new HashMap<String, Float>();

        if(products.isEmpty())
            return catogorySpendsMap;

        for (int i = 0; i < products.size(); i++) {
            if (!catogorySpendsMap.containsKey((products.get(i).catogory.toLowerCase()))) {
                catogorySpendsMap.put(products.get(i).catogory.toLowerCase(), (float) products.get(i).price);
            } else {
                Float catogorySum = catogorySpendsMap.get(products.get(i).catogory.toLowerCase());
                catogorySum = catogorySum + products.get(i).price;
                catogorySpendsMap.put(products.get(i).catogory.toLowerCase(), catogorySum);
            }
        }

        String spendsStr = "";
        Set<String> keySet = catogorySpendsMap.keySet();
        for (String key : keySet) {
            spendsStr = spendsStr + key.toString() + "   :   " + catogorySpendsMap.get(key).toString() + "\n";
        }
        Log.v("AppTest_Main : spendsStr : /n", spendsStr.toString());
        //editTextView.setText(spendsStr);
        return catogorySpendsMap;
    }
}

