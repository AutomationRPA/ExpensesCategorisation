package com.example.scantext_gms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PieChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("AppTest_PieChart: ", "Executing onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piechart);
        PieChart pieChart = findViewById(R.id.piechart);

        ArrayList Categories = new ArrayList();
        ArrayList<Entry> pieChartArray = new ArrayList();
        Map<String, Float> spendAnalyticsMap = new HashMap<String, Float>();
        List<Product> displayProducts = JsonReadWrite.getObjectsFromUserJSONFile(getApplicationContext());

        PieDataSet dataSet = new PieDataSet(pieChartArray, "Expense categorisation");

        if (displayProducts!=null) {
            spendAnalyticsMap = SpendAnalytics.analyseSpends(displayProducts);

            Set<String> keySet = spendAnalyticsMap.keySet();
            Integer i = 0;
            for (String key : keySet) {
                pieChartArray.add(new Entry(spendAnalyticsMap.get(key), i));
                Categories.add(key);
            }
        } else {

            pieChartArray.add(new Entry(100.0f, 0));
            Categories.add("No Receipts Added");
            Toast.makeText(PieChartActivity.this,
                    "No receipts generated, Please add a receipt",
                    Toast.LENGTH_LONG)
                    .show();
        }

        PieData data = new PieData(Categories, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);

    }
}