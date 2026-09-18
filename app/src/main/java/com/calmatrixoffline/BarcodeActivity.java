package com.calmatrixoffline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

public class BarcodeActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private static final int CAMERA_REQUEST = 102;

    private TextView resultText;
    private Button scanBtn, galleryBtn, backBtn, useBtn;
    private BarcodeScanner scanner;
    private String lastScannedName = "";
    private String lastScannedBarcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        resultText = findViewById(R.id.resultText);
        scanBtn = findViewById(R.id.scanBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        backBtn = findViewById(R.id.backBtn);
        useBtn = findViewById(R.id.useBtn);

        scanner = BarcodeScanning.getClient();

        // ===== USE BUTTON: Send result back to MainActivity =====
        /*
        useBtn.setOnClickListener(v -> {
            if (!lastScannedName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FOOD_NAME", lastScannedName);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Scan a barcode first", Toast.LENGTH_SHORT).show();
            }
        });
        */

        useBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(lastScannedName)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FOOD_NAME", lastScannedName);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No food to use", Toast.LENGTH_SHORT).show();
            }
        });

        scanBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            } else {
                openCamera();
            }
        });

        galleryBtn.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 200);
        });

        backBtn.setOnClickListener(v -> finish());

        // Hide use button initially
        useBtn.setVisibility(android.view.View.GONE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                scanBitmap(photo);
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), data.getData());
                if (photo != null) {
                    scanBitmap(photo);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanBitmap(Bitmap bitmap) {
        resultText.setText("🔍 Scanning barcode...");
        useBtn.setVisibility(android.view.View.GONE);

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty()) {
                        resultText.setText("❌ No barcode found in image");
                        Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Barcode barcode = barcodes.get(0);
                    String code = barcode.getRawValue();

                    if (code == null) {
                        resultText.setText("❌ Could not read barcode");
                        return;
                    }

                    resultText.setText("📱 Barcode: " + code + "\nSearching...");

                    BarcodeDatabase.BarcodeItem item = BarcodeDatabase.search(code);
                    if (item != null) {
                        lastScannedName = item.name;
                        lastScannedBarcode = code;

                        resultText.setText("✅ " + item.name + "\n" +
                                "Calories: " + item.calories + " cal\n" +
                                "Protein: " + item.protein + "g | Carbs: " + item.carbs + "g | Fat: " + item.fat + "g\n" +
                                "Serving: " + item.serving);

                        // Show the USE button
                        useBtn.setVisibility(android.view.View.VISIBLE);
                        useBtn.setText("📝 Use \"" + item.name + "\"");

                        Toast.makeText(this, "✅ Found: " + item.name, Toast.LENGTH_SHORT).show();
                    } else {
                        lastScannedName = "";
                        lastScannedBarcode = "";
                        useBtn.setVisibility(android.view.View.GONE);
                        resultText.setText("❌ Product not in local database.\nBarcode: " + code);
                        Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    resultText.setText("❌ Scan failed: " + e.getMessage());
                    Toast.makeText(this, "❌ Scan failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}