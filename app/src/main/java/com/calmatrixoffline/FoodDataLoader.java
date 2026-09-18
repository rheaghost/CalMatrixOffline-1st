package com.calmatrixoffline;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FoodDataLoader {
    public static void loadFoodsFromCSV(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("foods.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String name = parts[0].trim();
                    int calories = Integer.parseInt(parts[1].trim());
                    double protein = Double.parseDouble(parts[2].trim());
                    double carbs = Double.parseDouble(parts[3].trim());
                    double fat = Double.parseDouble(parts[4].trim());
                    String serving = parts[5].trim();

                    // Add to database
                    // You'll need to modify FoodDatabase to accept dynamic entries
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}