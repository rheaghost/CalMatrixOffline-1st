package com.calmatrixoffline;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
//added for correct csv reading 2609031648


public class ImportActivity extends AppCompatActivity {

    private String currentImportType = "";
    private static final int PICK_FILE_REQUEST = 100;

    private TextView resultText, statusText;
    private Button importFoodBtn, importExerciseBtn, importBarcodeBtn, backBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        dbHelper = new DatabaseHelper(this);

        resultText = findViewById(R.id.resultText);
        statusText = findViewById(R.id.statusText);
        importFoodBtn = findViewById(R.id.importFoodBtn);
        importExerciseBtn = findViewById(R.id.importExerciseBtn);
        importBarcodeBtn = findViewById(R.id.importBarcodeBtn);
        backBtn = findViewById(R.id.backBtn);

        // Remove folder button references
        // importFolderBtn is removed from layout

        importFoodBtn.setOnClickListener(v -> {
            currentImportType = "foods";
            resultText.setText("📂 Select a CSV file...");
            //pickFile("foods");
            pickFile();
        });

        importExerciseBtn.setOnClickListener(v -> {
            currentImportType = "exercises";
            resultText.setText("📂 Select a CSV file...");
            //pickFile("exercises");
            pickFile();
        });

        importBarcodeBtn.setOnClickListener(v -> {
            currentImportType = "barcodes";
            resultText.setText("📂 Select a CSV file...");
            //pickFile("barcodes");
            pickFile();
        });

        backBtn.setOnClickListener(v -> finish());

        updateStatus();
    }

    private void pickFile_old(String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Store type for later use
        intent.putExtra("import_type", type);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();


                String fileName = getFileName(uri);
                //String importType = data.getStringExtra("import_type");
                //if (importType == null) importType = "foods";

                resultText.setText("📁 File: " + fileName);
                // Use the stored import type
                if (TextUtils.isEmpty(currentImportType)) {
                    currentImportType = "foods"; // default
                }


                //processCSV(uri, importType);
                processCSV(uri, currentImportType);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "unknown";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            fileName = uri.getPath();
        }
        return fileName;
    }

    private void processCSV(Uri uri, String importType) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) {
                showError("Could not open file");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            int count = 0;
            int skipped = 0;
            String line;
            boolean firstLine = true;
            boolean isHeader = true;
            int columnCount = 0;

            // Read first line to detect column count
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // Try to detect column count from header
                    String[] headerParts = parseCSVLine(line);
                    columnCount = headerParts.length;
                    Log.d("Import", "Header column count: " + columnCount + " for type: " + importType);
                    // If header is not present, columnCount will be 0
                    if (columnCount >= 2) {
                        // This is likely a header, skip it
                        continue;
                    }
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = parseCSVLine(line);
                    int actualLength = parts.length;
                    Log.d("Import", "Row length: " + actualLength + " | Row: " + line);

                    // Determine which type this row belongs to based on column count
                    if (importType.equals("barcodes") && actualLength >= 7) {
                        // Barcode format: barcode,name,calories,protein,carbs,fat,serving
                        String barcode = parts[0].trim();
                        String name = parts[1].trim();
                        int calories = (int) Math.round(Double.parseDouble(parts[2].trim()));
                        int protein = (int) Math.round(Double.parseDouble(parts[3].trim()));
                        int carbs = (int) Math.round(Double.parseDouble(parts[4].trim()));
                        int fat = (int) Math.round(Double.parseDouble(parts[5].trim()));
                        String serving = parts[6].trim();
                        dbHelper.insertFood(name, calories, protein, carbs, fat, serving, barcode);
                        count++;
                    } else if (importType.equals("foods") && actualLength >= 6) {
                        // Food format: name,calories,protein,carbs,fat,serving
                        String name = parts[0].trim();
                        int calories = (int) Math.round(Double.parseDouble(parts[1].trim()));
                        int protein = (int) Math.round(Double.parseDouble(parts[2].trim()));
                        int carbs = (int) Math.round(Double.parseDouble(parts[3].trim()));
                        int fat = (int) Math.round(Double.parseDouble(parts[4].trim()));
                        String serving = parts[5].trim();
                        dbHelper.insertFood(name, calories, protein, carbs, fat, serving, "");
                        count++;
                    } else if (importType.equals("exercises") && actualLength >= 2) {
                        // Exercise format: name,met
                        String name = parts[0].trim();
                        double met = Double.parseDouble(parts[1].trim());
                        dbHelper.insertExercise(name, met);
                        count++;
                    } else {
                        skipped++;
                        Log.d("Import", "Skipped (wrong columns): " + line);
                    }
                } catch (NumberFormatException e) {
                    skipped++;
                    Log.d("Import", "Skipped (number format): " + line + " | Error: " + e.getMessage());
                } catch (Exception e) {
                    skipped++;
                    Log.d("Import", "Skipped (error): " + line + " | Error: " + e.getMessage());
                }
            }
            reader.close();

            Log.d("Import", "✅ Imported: " + count + ", Skipped: " + skipped);

            String typeLabel = importType.equals("foods") ? "foods" :
                    importType.equals("exercises") ? "exercises" : "barcodes";
            final int finalCount = count;
            final int finalSkipped = skipped;
            runOnUiThread(() -> {
                resultText.setText("✅ Imported " + finalCount + " " + typeLabel);
                statusText.setText("📊 Imported: " + finalCount + " | Skipped: " + finalSkipped);
                Toast.makeText(this, "✅ " + finalCount + " " + typeLabel + " imported", Toast.LENGTH_LONG).show();
                updateStatus();
            });

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> showError("Error: " + e.getMessage()));
        }
    }

    private void processCSV_foodonly(Uri uri, String importType) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) {
                showError("Could not open file");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            int count = 0;
            int skipped = 0;
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    skipped++;
                    continue;
                }

                try {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 6) {
                        String name = parts[0].trim();
                        int calories = (int) Math.round(Double.parseDouble(parts[1].trim()));
                        int protein = (int) Math.round(Double.parseDouble(parts[2].trim()));
                        int carbs = (int) Math.round(Double.parseDouble(parts[3].trim()));
                        int fat = (int) Math.round(Double.parseDouble(parts[4].trim()));
                        String serving = parts[5].trim();

                        dbHelper.insertFood(name, calories, protein, carbs, fat, serving, "");
                        count++;
                    } else {
                        skipped++;
                        Log.d("Import", "Skipped (wrong columns): " + line);
                    }
                } catch (NumberFormatException e) {
                    skipped++;
                    Log.d("Import", "Skipped (number format): " + line);
                } catch (Exception e) {
                    skipped++;
                    Log.d("Import", "Skipped (error): " + line + " - " + e.getMessage());
                }
            }
            reader.close();

            Log.d("Import", "✅ Imported: " + count + ", Skipped: " + skipped);

            int finalCount = count;
            int finalSkipped = skipped;
            runOnUiThread(() -> {
                resultText.setText("✅ Imported " + finalCount + " entries");
                statusText.setText("📊 Imported: " + finalCount + " | Skipped: " + finalSkipped);
                Toast.makeText(this, "✅ " + finalCount + " entries imported", Toast.LENGTH_LONG).show();
                updateStatus();
            });


        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> showError("Error: " + e.getMessage()));
        }
    }

    // Add this method inside ImportActivity
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }
    private void processCSV_old2(Uri uri, String importType) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) {
                showError("Could not open file");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            int count = 0;
            int skipped = 0;
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // Skip header row
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    skipped++;
                    continue;
                }

                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String name = parts[0].trim();
                        int calories = (int) Math.round(Double.parseDouble(parts[1].trim()));
                        int protein = (int) Math.round(Double.parseDouble(parts[2].trim()));
                        int carbs = (int) Math.round(Double.parseDouble(parts[3].trim()));
                        int fat = (int) Math.round(Double.parseDouble(parts[4].trim()));
                        String serving = parts[5].trim();

                        // Insert into database
                        dbHelper.insertFood(name, calories, protein, carbs, fat, serving, "");
                        count++;
                    } else {
                        skipped++;
                        Log.d("Import", "Skipped (wrong columns): " + line);
                    }
                } catch (NumberFormatException e) {
                    skipped++;
                    Log.d("Import", "Skipped (number format): " + line);
                } catch (Exception e) {
                    skipped++;
                    Log.d("Import", "Skipped (error): " + line + " - " + e.getMessage());
                }
            }
            reader.close();

            Log.d("Import", "✅ Imported: " + count + ", Skipped: " + skipped);

            // After loop:
            int finalCount = count;
            int finalSkipped = skipped;
            runOnUiThread(() -> {
                resultText.setText("✅ Imported " + finalCount + " entries");
                statusText.setText("📊 Imported: " + finalCount + " | Skipped: " + finalSkipped);
                Toast.makeText(this, "✅ " + finalCount + " entries imported", Toast.LENGTH_LONG).show();
                updateStatus();
            });

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> showError("Error: " + e.getMessage()));
        }
    }
    private void processCSV_original(Uri uri, String importType) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream is = resolver.openInputStream(uri);
            if (is == null) {
                showError("Could not open file");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            int count = 0;
            String line;
            boolean firstLine = true;
            List<String[]> rows = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    String lower = line.toLowerCase();
                    if (lower.contains("name") || lower.contains("calories") ||
                            lower.contains("barcode") || lower.contains("serving") ||
                            lower.contains("met")) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        rows.add(parts);
                        count++;
                    }
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    rows.add(parts);
                    count++;
                }
            }
            reader.close();

            if (count == 0) {
                showError("No data found in file");
                return;
            }

            // Determine format and store
            String[] sample = rows.get(0);
            int stored = 0;

            if (importType.equals("barcodes") && sample.length >= 7) {
                // Barcode format: barcode,name,calories,protein,carbs,fat,serving
                for (String[] row : rows) {
                    if (row.length >= 7) {
                        try {
                            String barcode = row[0].trim();
                            String name = row[1].trim();
                            int calories = (int) Math.round(Double.parseDouble(row[2].trim()));
                            int protein = (int) Math.round(Double.parseDouble(row[3].trim()));
                            int carbs = (int) Math.round(Double.parseDouble(row[4].trim()));
                            int fat = (int) Math.round(Double.parseDouble(row[5].trim()));
                            String serving = row[6].trim();
                            dbHelper.insertFood(name, calories, protein, carbs, fat, serving, barcode);
                            stored++;
                        } catch (NumberFormatException e) {
                            // Skip invalid rows
                        }
                    }
                }
                resultText.setText("✅ Imported " + stored + " barcodes");
            } else if (sample.length >= 6) {
                // Food format: name,calories,protein,carbs,fat,serving
                for (String[] row : rows) {
                    if (row.length >= 6) {
                        try {
                            String name = row[0].trim();
                            int calories = (int) Math.round(Double.parseDouble(row[1].trim()));
                            int protein = (int) Math.round(Double.parseDouble(row[2].trim()));
                            int carbs = (int) Math.round(Double.parseDouble(row[3].trim()));
                            int fat = (int) Math.round(Double.parseDouble(row[4].trim()));
                            String serving = row[5].trim();
                            dbHelper.insertFood(name, calories, protein, carbs, fat, serving, "");
                            stored++;
                        } catch (NumberFormatException e) {
                            // Skip invalid rows
                        }
                    }
                }
                resultText.setText("✅ Imported " + stored + " foods");
            } else if (sample.length >= 2) {
                // Exercise format: name,met
                for (String[] row : rows) {
                    if (row.length >= 2) {
                        try {
                            String name = row[0].trim();
                            double met = Double.parseDouble(row[1].trim());
                            // Store exercise in a separate table
                            dbHelper.insertExercise(name, met);
                            // For now, just count
                            stored++;
                        } catch (NumberFormatException e) {
                            // Skip invalid rows
                        }
                    }
                }
                resultText.setText("✅ Found " + stored + " exercises (storage coming soon)");
            } else {
                resultText.setText("✅ Found " + count + " entries (unknown format)");
                stored = count;
            }

            updateStatus();
            Toast.makeText(this, "✅ " + stored + " entries imported", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatus() {
        int count = dbHelper.getFoodCount();
        statusText.setText("📊 Total foods in database: " + count);
    }

    private void showError(String msg) {
        resultText.setText("❌ " + msg);
        statusText.setText("");
        Toast.makeText(this, "❌ " + msg, Toast.LENGTH_SHORT).show();
    }
}