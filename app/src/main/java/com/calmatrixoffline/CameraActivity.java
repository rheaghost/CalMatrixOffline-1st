package com.calmatrixoffline;
//2609051313  web img search manual entry ok
//now starting meal macros
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

public class CameraActivity extends AppCompatActivity {
    private static final boolean ENABLE_BING_YANDEX = false; // set to true to enable
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;

    private static final int CAMERA_PERMISSION_REQUEST = 102;

    private static final long ONLINE_SEARCH_COOLDOWN_MS = 60000;
    private long lastOnlineSearchTime = 0;

    // Get your free API key from https://api.imgbb.com/
    //private static final String IMGBB_API_KEY = "5e99d00c8d4a36e4d71f9c068ad9d013";
    //private static final String IMGBB_API_KEY ="";

    private ImageView photoPreview;
    private TextView resultText;
    private Button captureBtn, galleryBtn, useFoodBtn, backBtn;
    private Button searchAiBtn;
    private Button searchGoogleBtn;//
    // searchBingBtn, searchYandexBtn;
    private Button searchBingBtn;
    private Button searchYandexBtn;
    private Button fixBtn;
    private WebView webView;
    private EditText foodNameInput;
    private Button confirmFoodBtn;

    private DatabaseHelper dbHelper;
    private FoodClassifier classifier;

    private Bitmap capturedImage;
    private String recognizedFood = "";
    private String currentModelOutput = "";

    //private Button manualEntryBtn;
    private LinearLayout manualEntryRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        dbHelper = new DatabaseHelper(this);
        classifier = new FoodClassifier(this);//ok

        // Find views
        photoPreview = findViewById(R.id.photoPreview);
        resultText = findViewById(R.id.resultText);
        captureBtn = findViewById(R.id.captureBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        useFoodBtn = findViewById(R.id.useFoodBtn);//ok
        fixBtn = findViewById(R.id.fixBtn);
        backBtn = findViewById(R.id.backBtn);//ok
        searchAiBtn = findViewById(R.id.searchAiBtn);
        searchGoogleBtn = findViewById(R.id.searchGoogleBtn);//ok
        searchBingBtn = findViewById(R.id.searchBingBtn);
        searchYandexBtn = findViewById(R.id.searchYandexBtn);
        webView = findViewById(R.id.webView);//ok
        foodNameInput = findViewById(R.id.foodNameInput);
        confirmFoodBtn = findViewById(R.id.confirmFoodBtn);//ok 1829

        if (!ENABLE_BING_YANDEX) {
            searchBingBtn.setVisibility(View.GONE);
            searchYandexBtn.setVisibility(View.GONE);
        }

        // Hide WebView and input initially
        webView.setVisibility(View.GONE);//ok
        //foodNameInput.setVisibility(View.GONE);//ok
        //confirmFoodBtn.setVisibility(View.GONE);//ok
        useFoodBtn.setVisibility(View.GONE);//ok
        fixBtn.setVisibility(View.GONE);//ok
        searchAiBtn.setVisibility(View.GONE);//ok
        searchGoogleBtn.setVisibility(View.GONE);//ok
        searchBingBtn.setVisibility(View.GONE);
        searchYandexBtn.setVisibility(View.GONE);

        //manualEntryBtn = findViewById(R.id.manualEntryBtn);
        //manualEntryRow = findViewById(R.id.manualEntryRow); // now we can find it
        //manualEntryBtn.setVisibility(View.VISIBLE);   // ✅ show it


        //manualEntryRow.setVisibility(View.GONE);


        // Setup WebView for online search
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);//ok
        //webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        //ok

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                runOnUiThread(() -> {
                    //manualEntryRow.setVisibility(View.VISIBLE);
                    resultText.setText("🔍 Search loaded. Type the food name below.");
                    foodNameInput.setVisibility(View.VISIBLE);
                    confirmFoodBtn.setVisibility(View.VISIBLE);
                });
            }
        });//ok 1834


        // Click listeners
        captureBtn.setOnClickListener(v -> openCamera());
        galleryBtn.setOnClickListener(v -> openGallery());
        backBtn.setOnClickListener(v -> finish());//ok

        // Manual Entry button click
        /*

        manualEntryBtn.setOnClickListener(v -> {
            foodNameInput.setVisibility(View.VISIBLE);   // <-- ensure the EditText is visible
            confirmFoodBtn.setVisibility(View.VISIBLE);  // <-- ensure the button is visible
            manualEntryRow.setVisibility(View.VISIBLE);
            foodNameInput.setText("");
            foodNameInput.setHint("Type the food name...");
            foodNameInput.requestFocus();                // <-- show keyboard
            resultText.setText("✏️ Type the food name and tap 'Use'.");
        });

         */

        /*

        manualEntryBtn.setOnClickListener(v -> {
            manualEntryRow.setVisibility(View.VISIBLE);
            resultText.setText("✏️ Type the food name above, then tap '✅ Use'.");
            foodNameInput.requestFocus();
        });
        */




       /*
        useFoodBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(recognizedFood)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FOOD_NAME", recognizedFood);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No food recognized", Toast.LENGTH_SHORT).show();
            }
        });
        */

        fixBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(currentModelOutput)) {
                showFixDialog("food", recognizedFood.isEmpty() ? "food" : recognizedFood);
            } else {
                showFixDialog(currentModelOutput, recognizedFood.isEmpty() ? currentModelOutput : recognizedFood);
            }
        });

        // AI Search
        searchAiBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            recognizeFoodWithAI(capturedImage);
        });//ok searchAiBtn2609091841

        // Online Search buttons
        //searchGoogleBtn.setOnClickListener(v -> performSearch("google"));

        // Google Search (online)
        /*
        searchGoogleBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            performGoogleSearch();
        });//searchGoogleBtn ok 2609041842

         */

        //search google on separate browser as it considers you as bot 2609050637
        searchGoogleBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }

            resultText.setText("🔍 Uploading for Google search...");
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://www.google.com/searchbyimage?image_url=" + Uri.encode(imageUrl);
                    // Open in external browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Google search opened in browser");
                }

                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                    Toast.makeText(CameraActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        //end google sep browser

        //searchBingBtn.setOnClickListener(v -> performSearch("bing"));
        // Bing Search (opens in browser)
        /*
        searchBingBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }

            resultText.setText("🔍 Uploading for Bing search...");
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://www.bing.com/images/search?view=detailv2&form=IDLVIR&iss=inf&q=imgurl:" + Uri.encode(imageUrl);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Bing search opened in browser");
                }

                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                    Toast.makeText(CameraActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        */
        //searchYandexBtn.setOnClickListener(v -> performSearch("yandex"));

        // Bing Search (corrected URL)
        searchBingBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            resultText.setText("🔍 Uploading for Bing search...");
            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://www.bing.com/images/search?q=imgurl:" + Uri.encode(imageUrl) + "&view=detailv2&iss=sbi";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Bing search opened in browser");
                }
                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                }
            });
        });

// Yandex Search (corrected URL)
        searchYandexBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            resultText.setText("🔍 Uploading for Yandex search...");
            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://yandex.com/images/search?url=" + Uri.encode(imageUrl) + "&rpt=imageview";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Yandex search opened in browser");
                }
                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                }
            });
        });

// Yandex Search (opens in browser)
        /*
        searchYandexBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }

            resultText.setText("🔍 Uploading for Yandex search...");
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://yandex.com/images/search?rpt=imageview&img_url=" + Uri.encode(imageUrl);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Yandex search opened in browser");
                }

                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                    Toast.makeText(CameraActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        */

        // Yandex Search (corrected URL)
        searchYandexBtn.setOnClickListener(v -> {
            if (capturedImage == null) {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            resultText.setText("🔍 Uploading for Yandex search...");
            uploadToImgbb(capturedImage, new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    String searchUrl = "https://yandex.com/images/search?url=" + Uri.encode(imageUrl) + "&rpt=imageview";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                    startActivity(browserIntent);
                    resultText.setText("✅ Yandex search opened in browser");
                }
                @Override
                public void onError(String error) {
                    resultText.setText("❌ Upload failed: " + error);
                }
            });
        });





        // Confirm button for user-typed food name (from online search)

        confirmFoodBtn.setOnClickListener(v -> {
            String userFood = foodNameInput.getText().toString().trim();
            if (TextUtils.isEmpty(userFood)) {
                Toast.makeText(this, "Please type the food name", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.FoodItem food = dbHelper.searchFood(userFood);
            if (food != null) {
                recognizedFood = food.name;
                resultText.setText("✅ Found: " + food.name + "\n" +
                        "Calories: " + food.calories + " cal\n" +
                        "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g");
                useFoodBtn.setVisibility(View.VISIBLE);
                useFoodBtn.setText("📝 Use \"" + food.name + "\"");
                // Hide WebView and input
                webView.setVisibility(View.GONE);
                //foodNameInput.setVisibility(View.GONE);
                //confirmFoodBtn.setVisibility(View.GONE);
                manualEntryRow.setVisibility(View.GONE);
            } else {
                //Not in database-offer to add it 2609041843

                new AlertDialog.Builder(this)
                        .setTitle("Food Not Found")
                        .setMessage("This food is not in your database. Would you like to add it?")
                        .setPositiveButton("Add", (dialog, which) -> showAddFoodDialog(userFood))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });//end confirmFoodBtn.setInClick ok 2609041844



        confirmFoodBtn.setOnClickListener(v -> {
            String userFood = foodNameInput.getText().toString().trim();
            if (TextUtils.isEmpty(userFood)) {
                Toast.makeText(this, "Please type the food name", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.FoodItem food = dbHelper.searchFood(userFood);
            if (food != null) {
                // Found in DB → send straight back to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FOOD_NAME", food.name);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                // Not in DB → offer to add
                new AlertDialog.Builder(this)
                        .setTitle("Food Not Found")
                        .setMessage("This food is not in your database. Would you like to add it?")
                        .setPositiveButton("Add", (dialog, which) -> showAddFoodDialog(userFood))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Use Food button (sends to MainActivity)
        useFoodBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(recognizedFood)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FOOD_NAME", recognizedFood);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No food recognized", Toast.LENGTH_SHORT).show();
            }
        });//usefoodbtn manual search ok 2609041855

        // Fix button (correction)
        fixBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(currentModelOutput)) {
                showFixDialog("food", recognizedFood.isEmpty() ? "food" : recognizedFood);
            } else {
                showFixDialog(currentModelOutput, recognizedFood.isEmpty() ? currentModelOutput : recognizedFood);
            }
        });//fix button ok 2609041855
        // Also allow manual entry even without a photo: the user can just type in the input field and tap confirm.
        // But the input field is hidden initially. We can show it if user taps a "Manual Entry" button.
        // For simplicity, we already have the input field; we can make it visible all the time or add a "Manual" button.
        // I'll add a small "Manual Entry" button next to the search buttons.
        // For now, we'll keep it hidden until Google search or AI search is used.
        // But we can also allow the user to type without taking a photo by adding a "Manual" button.
        // Let's add a "Manual" button in the layout and connect it.
        // But to keep it simple, we can show the input field all the time, but disabled until a photo is taken?
        // Instead, I'll add a "Manual Entry" button that shows the input field.
        // The user already has the ability to type after AI or Google. That is sufficient.



    }//onCreate


    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
            return;
        }

        // Permission already granted
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }
    private void openCamera_old1() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }//ok 260941908



    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                capturedImage = photo;
                photoPreview.setImageBitmap(photo);
                // Show all search buttons
                searchAiBtn.setVisibility(View.VISIBLE);
                searchGoogleBtn.setVisibility(View.VISIBLE);
                searchBingBtn.setVisibility(View.VISIBLE);
                searchYandexBtn.setVisibility(View.VISIBLE);
                resultText.setText("📸 Photo taken. Choose AI or Online search.");
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), data.getData());
                    if (photo != null) {
                        capturedImage = photo;
                        photoPreview.setImageBitmap(photo);
                        searchAiBtn.setVisibility(View.VISIBLE);
                        searchGoogleBtn.setVisibility(View.VISIBLE);
                        searchBingBtn.setVisibility(View.VISIBLE);
                        searchYandexBtn.setVisibility(View.VISIBLE);
                        resultText.setText("📸 Photo selected. Choose AI or Online search.");
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }//ok onactivityresult 2609041915

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // ============================================================
    // AI RECOGNITION (TFLite)
    // ============================================================
    private void recognizeFoodWithAI(Bitmap image) {
        resultText.setText("🧠 Analyzing with AI...");
        useFoodBtn.setVisibility(View.GONE);
        fixBtn.setVisibility(View.GONE);
        recognizedFood = "";
        currentModelOutput = "";

        FoodClassifier.ClassificationResult result = classifier.classifyFood(image, dbHelper);
        String modelOutput = result.foodName;
        float confidence = result.confidence;
        currentModelOutput = modelOutput;

        if (modelOutput.equals("food") || confidence < 0.5f) {
            resultText.setText("❌ AI uncertain (confidence: " + String.format("%.0f%%", confidence * 100) +
                    ").\nTry Online search or manual entry.");
            fixBtn.setVisibility(View.VISIBLE);
            fixBtn.setText("✏️ Enter food name...");
            return;
        }

        String confidenceText = "Confidence: " + String.format("%.0f%%", confidence * 100);
        DatabaseHelper.FoodItem food = dbHelper.searchFood(modelOutput);

        if (food != null) {
            recognizedFood = food.name;
            resultText.setText("✅ " + food.name + "\n" +
                    "Calories: " + food.calories + " cal\n" +
                    "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g\n" +
                    confidenceText);
            useFoodBtn.setVisibility(View.VISIBLE);
            useFoodBtn.setText("📝 Use \"" + food.name + "\"");
        } else {
            recognizedFood = modelOutput;
            resultText.setText("🔍 AI Recognized: " + modelOutput + "\n" +
                    confidenceText + "\n" +
                    "⚠️ Not in your database yet.");
            useFoodBtn.setVisibility(View.GONE);
        }
        fixBtn.setVisibility(View.VISIBLE);
        fixBtn.setText("✏️ Fix: This is actually...");
    }//ok aisearch 2609041917




    // ============================================================
    // GOOGLE SEARCH (Online)
    // ============================================================
    private void performGoogleSearch() {
        // Rate limiting
        long now = System.currentTimeMillis();
        if (now - lastOnlineSearchTime < ONLINE_SEARCH_COOLDOWN_MS) {
            long remaining = (ONLINE_SEARCH_COOLDOWN_MS - (now - lastOnlineSearchTime)) / 1000;
            Toast.makeText(this, "Please wait " + remaining + " seconds", Toast.LENGTH_SHORT).show();
            return;
        }
        lastOnlineSearchTime = now;

        resultText.setText("🔍 Uploading photo for Google search...");
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        uploadToImgbb(capturedImage, new UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                String searchUrl = "https://www.google.com/searchbyimage?image_url=" + Uri.encode(imageUrl);
                resultText.setText("🔍 Loading Google search...");
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(searchUrl);
            }

            @Override
            public void onError(String error) {
                resultText.setText("❌ Upload failed: " + error + "\nTry typing the food name manually.");
                Toast.makeText(CameraActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                // Show the input field for manual entry
                foodNameInput.setVisibility(View.VISIBLE);
                confirmFoodBtn.setVisibility(View.VISIBLE);
                foodNameInput.setHint("Type the food name...");
            }
        });
    }//ok performgooglesearch 2609041918

    /*
    private interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }//ok uploadcallback2609041025
    */






    // ============================================================
    // ONLINE SEARCH (WebView + imgbb)
    // ============================================================
    private void performSearch(String engine) {
        if (capturedImage == null) {
            Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastOnlineSearchTime < ONLINE_SEARCH_COOLDOWN_MS) {
            long remaining = (ONLINE_SEARCH_COOLDOWN_MS - (now - lastOnlineSearchTime)) / 1000;
            Toast.makeText(this, "Please wait " + remaining + " seconds", Toast.LENGTH_SHORT).show();
            return;
        }
        lastOnlineSearchTime = now;

        resultText.setText("🔍 Uploading photo for " + engine + " search...");
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        uploadToImgbb(capturedImage, new UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                String searchUrl = buildSearchUrl(engine, imageUrl);
                if (searchUrl != null) {
                    resultText.setText("🔍 Loading " + engine + " search...");
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(searchUrl);
                } else {
                    resultText.setText("❌ Unknown search engine");
                    Toast.makeText(CameraActivity.this, "Unknown search engine", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                resultText.setText("❌ Upload failed: " + error);
                Toast.makeText(CameraActivity.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String buildSearchUrl(String engine, String imageUrl) {
        switch (engine) {
            case "google":
                return "https://www.google.com/searchbyimage?image_url=" + Uri.encode(imageUrl);
            case "bing":
                return "https://www.bing.com/images/search?view=detailv2&form=IDLVIR&iss=inf&q=imgurl:" + Uri.encode(imageUrl);
            case "yandex":
                return "https://yandex.com/images/search?rpt=imageview&img_url=" + Uri.encode(imageUrl);
            default:
                return null;
        }
    }

    private interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    private void uploadToImgbb(Bitmap bitmap, UploadCallback callback) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                String imageBase64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);

                String apiKey = getApiKey();  // <-- gets from SharedPreferences

                if (apiKey.isEmpty()) {
                    runOnUiThread(() -> callback.onError("No API key. Go to Settings."));
                    return;
                }

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("key", getApiKey())//no more IMGBB_API_KEY
                        .addFormDataPart("image", imageBase64)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.imgbb.com/1/upload")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getBoolean("success")) {
                        String imageUrl = json.getJSONObject("data").getString("url");
                        runOnUiThread(() -> callback.onSuccess(imageUrl));
                    } else {
                        String error = json.getJSONObject("error").getString("message");
                        runOnUiThread(() -> callback.onError(error));
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }//ok uploadimgtobb 26090419229

    private String getApiKey() {
        android.content.SharedPreferences prefs = getSharedPreferences("CalSnapPrefs", MODE_PRIVATE);
        return prefs.getString("imgbb_api_key", "");
    }



    // ============================================================
    // FIX DIALOG (for corrections)
    // ============================================================
    private void showFixDialog(final String modelOutput, final String currentGuess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correct Food Name");
        builder.setMessage("What food is this actually?");

        final EditText input = new EditText(this);
        input.setHint("e.g., Pizza");
        input.setText(currentGuess);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String userCorrection = input.getText().toString().trim();
            if (!userCorrection.isEmpty()) {
                dbHelper.saveCorrection(modelOutput, userCorrection);
                recognizedFood = userCorrection;
                resultText.setText("✅ Saved: " + userCorrection);
                Toast.makeText(this, "Correction saved!", Toast.LENGTH_SHORT).show();
                useFoodBtn.setVisibility(View.VISIBLE);
                useFoodBtn.setText("📝 Use \"" + userCorrection + "\"");
                useFoodBtn.setOnClickListener(v -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("FOOD_NAME", userCorrection);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            } else {
                Toast.makeText(this, "No correction entered", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ============================================================
    // ADD FOOD DIALOG (when user types a name not in DB)
    // ============================================================

    private void showAddFoodDialog(String foodName) {
        // Create a LinearLayout to hold all input fields
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        // Create EditText fields
        final EditText caloriesInput = new EditText(this);
        caloriesInput.setHint("Calories");
        layout.addView(caloriesInput);

        final EditText proteinInput = new EditText(this);
        proteinInput.setHint("Protein (g)");
        layout.addView(proteinInput);

        final EditText carbsInput = new EditText(this);
        carbsInput.setHint("Carbs (g)");
        layout.addView(carbsInput);

        final EditText fatInput = new EditText(this);
        fatInput.setHint("Fat (g)");
        layout.addView(fatInput);

        final EditText servingInput = new EditText(this);
        servingInput.setHint("Serving size");
        layout.addView(servingInput);

        // Build the dialog
        new AlertDialog.Builder(this)
                .setTitle("Add Food")
                .setMessage("Enter nutrition info for " + foodName)
                .setView(layout) // <-- Single view
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        int calories = Integer.parseInt(caloriesInput.getText().toString().trim());
                        int protein = Integer.parseInt(proteinInput.getText().toString().trim());
                        int carbs = Integer.parseInt(carbsInput.getText().toString().trim());
                        int fat = Integer.parseInt(fatInput.getText().toString().trim());
                        String serving = servingInput.getText().toString().trim();
                        if (serving.isEmpty()) serving = "1 serving";
                        //dbHelper.insertFood(foodName, calories, protein, carbs, fat, serving, "");
                        /*
                        dbHelper.insertUserFood(foodName, calories, protein, carbs, fat, serving, "");

                        Toast.makeText(this, "✅ Added: " + foodName, Toast.LENGTH_SHORT).show();
                        recognizedFood = foodName;
                        useFoodBtn.setVisibility(View.VISIBLE);
                        useFoodBtn.setText("📝 Use \"" + foodName + "\"");

                         */

                        dbHelper.insertUserFood(foodName, calories, protein, carbs, fat, serving, "");

                        Toast.makeText(this, "✅ Added: " + foodName, Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("FOOD_NAME", foodName);
                        setResult(RESULT_OK, resultIntent);
                        finish();


                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    /*
    private void showAddFoodDialog_old(String foodName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Food");
        builder.setMessage("Enter nutrition info for " + foodName);

        final EditText caloriesInput = new EditText(this);
        caloriesInput.setHint("Calories");
        final EditText proteinInput = new EditText(this);
        proteinInput.setHint("Protein (g)");
        final EditText carbsInput = new EditText(this);
        carbsInput.setHint("Carbs (g)");
        final EditText fatInput = new EditText(this);
        fatInput.setHint("Fat (g)");
        final EditText servingInput = new EditText(this);
        servingInput.setHint("Serving size");

        builder.setView(caloriesInput, proteinInput, carbsInput, fatInput, servingInput);
        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                int calories = Integer.parseInt(caloriesInput.getText().toString().trim());
                int protein = Integer.parseInt(proteinInput.getText().toString().trim());
                int carbs = Integer.parseInt(carbsInput.getText().toString().trim());
                int fat = Integer.parseInt(fatInput.getText().toString().trim());
                String serving = servingInput.getText().toString().trim();
                if (serving.isEmpty()) serving = "1 serving";
                dbHelper.insertFood(foodName, calories, protein, carbs, fat, serving, "");
                Toast.makeText(this, "✅ Added: " + foodName, Toast.LENGTH_SHORT).show();
                recognizedFood = foodName;
                useFoodBtn.setVisibility(View.VISIBLE);
                useFoodBtn.setText("📝 Use \"" + foodName + "\"");
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

     */
}