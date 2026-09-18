package com.calmatrixoffline;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

    public static List<FoodItem> loadFoods(Context context, String filename) {
        List<FoodItem> foods = new ArrayList<>();
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    FoodItem item = new FoodItem();
                    item.name = parts[0].trim();
                    item.calories = (int) Math.round(Double.parseDouble(parts[1].trim()));
                    item.protein = (int) Math.round(Double.parseDouble(parts[2].trim()));
                    item.carbs = (int) Math.round(Double.parseDouble(parts[3].trim()));
                    item.fat = (int) Math.round(Double.parseDouble(parts[4].trim()));
                    item.serving = parts[5].trim();
                    foods.add(item);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foods;
    }

    public static List<ExerciseItem> loadExercises(Context context, String filename) {
        List<ExerciseItem> exercises = new ArrayList<>();
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    ExerciseItem item = new ExerciseItem();
                    item.name = parts[0].trim();
                    item.met = Double.parseDouble(parts[1].trim());
                    exercises.add(item);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exercises;
    }

    public static List<BarcodeItem> loadBarcodes(Context context, String filename) {
        List<BarcodeItem> barcodes = new ArrayList<>();
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    BarcodeItem item = new BarcodeItem();
                    item.barcode = parts[0].trim();
                    item.name = parts[1].trim();
                    item.calories = (int) Math.round(Double.parseDouble(parts[2].trim()));
                    item.protein = (int) Math.round(Double.parseDouble(parts[3].trim()));
                    item.carbs = (int) Math.round(Double.parseDouble(parts[4].trim()));
                    item.fat = (int) Math.round(Double.parseDouble(parts[5].trim()));
                    item.serving = parts[6].trim();
                    barcodes.add(item);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return barcodes;
    }

    // Model classes
    public static class FoodItem {
        public String name;
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public String serving;
    }

    public static class ExerciseItem {
        public String name;
        public double met;
    }

    public static class BarcodeItem {
        public String barcode;
        public String name;
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public String serving;
    }
}