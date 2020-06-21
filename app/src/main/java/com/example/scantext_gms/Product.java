package com.example.scantext_gms;

import android.util.Log;

public class Product {
    String name;
    int price;
    String catogory;

    //List<String> messages;

    public Product() {
        Log.v("AppTest_Product ", "Executing Product");
        this.name = name;
        this.price = price;
        this.catogory = catogory;
    }

    public String getName() {
        return name;
    }

    public void setName(String catogory) {
        this.name = name;
    }

    public String getCatogory() {
        return catogory;
    }

    public void setCatogory(String catogory) {
        this.catogory = catogory;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        Log.v("AppTest_Product Method", "Executing Method : toString");
        return "{" + "\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"price\": " + price + ",\n" +
                "\"catogory\": \"" + catogory + "\"\n" +
                '}';
    }

    public String toString2() {
        Log.v("AppTest_Product Method", "Executing Method : toString2");
        return String.format("%-32s%-10d%-16s", name, price, catogory) + "\n";
    }
}


