package com.calmatrixoffline;
// 2609059808 web image search using separate browser-google works
// 2609141844 Name changed from CalSnap1 to CalVault To CalMatrix Offline due to already taken

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.provider.MediaStore;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView foodInput;
    private AutoCompleteTextView exerciseInput;
    private EditText weightInput, durationInput;
    private TextView resultFood, resultExercise, dailyTotal, todaySummary, streakText, budgetDisplay;
    private RecyclerView historyRecyclerView;

    private ImageView foodImage;


    private int currentFoodCalories = 0;
    private int currentExerciseCalories = 0;
    private String currentFoodName = "";
    private String currentExerciseName = "";
    //2609031430

    //meal elated 2609052109
    private Button saveMealBtn, myMealsBtn;
    private Button createMealBtn;  //2609060959
    //backup recovery 26090922
    private Button backupBtn, recoveryBtn;

    private Button addImageBtn, deleteImageBtn;
    private LinearLayout imageActionRow;
    private String currentFoodNameForImage = "";

    //2610111525 for correct user food add
    private Button editFoodBtn;
    private DatabaseHelper.FoodItem currentUserFood;
    //end user food add

    private static final int CAMERA_REQUEST_CODE = 400;

    //2609040202
    private static final int BARCODE_REQUEST_CODE = 300;

    //2609110846
    private static final int PICK_IMAGE_REQUEST = 500;

    //knips
    //private static final int PICK_IMAGE_REQUEST = 500;
    private static final int TAKE_PHOTO_REQUEST = 501;
    private static final int CAMERA_PERMISSION_REQUEST = 502;

    private static final int VOICE_REQUEST_CODE = 350;


    private DatabaseHelper dbHelper;
    private LogAdapter adapter;
    private UserProfile userProfile;
    private int dailyBudget = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        userProfile = new UserProfile(this);


        // Initialize views
        foodInput = findViewById(R.id.foodInput);
        exerciseInput = findViewById(R.id.exerciseInput);
        weightInput = findViewById(R.id.weightInput);
        durationInput = findViewById(R.id.durationInput);
        resultFood = findViewById(R.id.resultFood);
        resultExercise = findViewById(R.id.resultExercise);
        dailyTotal = findViewById(R.id.dailyTotal);
        todaySummary = findViewById(R.id.todaySummary);
        streakText = findViewById(R.id.streakText);
        budgetDisplay = findViewById(R.id.budgetDisplay);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        foodImage = findViewById(R.id.foodImage); //food image 2609110624

        //2610111529 5b user food add related
        editFoodBtn = findViewById(R.id.editFoodBtn);
        editFoodBtn.setVisibility(View.GONE);
        editFoodBtn.setOnClickListener(v -> {
            if (currentUserFood != null && currentUserFood.isEditable) {
                showEditUserFoodDialog(currentUserFood);
            }
        });
        //end 2610111529


        // ... other view initialization ...

        preloadSampleImagesOnce();

        // ============================================================
        // FOOD AUTOCOMPLETE
        // ============================================================
        List<String> foodNames = dbHelper.getAllFoodNames();
        if (foodNames.isEmpty()) {
            // If no foods in database, add default foods
            addDefaultFoods();
            foodNames = dbHelper.getAllFoodNames();
        }
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, foodNames);
        foodInput.setAdapter(foodAdapter);
        foodInput.setThreshold(1);

        foodInput.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            searchFoodByName(selected);
        });

        // ============================================================
        // EXERCISE AUTOCOMPLETE
        // ============================================================
        List<String> exerciseNames = dbHelper.getAllExerciseNames();
        if (exerciseNames.isEmpty()) {
            addDefaultExercises();
            exerciseNames = dbHelper.getAllExerciseNames();
        }
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, exerciseNames);
        exerciseInput.setAdapter(exerciseAdapter);
        exerciseInput.setThreshold(1);

        exerciseInput.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            exerciseInput.setText(selected);
            // Don't auto-search; user still needs weight + duration
        });

        // ============================================================
        // SETUP RECYCLERVIEW
        // ============================================================
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogAdapter(dbHelper.getTodayEntries(), this::onDeleteEntry);
        historyRecyclerView.setAdapter(adapter);

        dailyBudget = userProfile.getDailyBudget();

        // ============================================================
        // BUTTONS
        // ============================================================
        Button searchFoodBtn = findViewById(R.id.searchFoodBtn);
        Button searchExerciseBtn = findViewById(R.id.searchExerciseBtn);
        Button logBtn = findViewById(R.id.logBtn);
        Button clearBtn = findViewById(R.id.clearBtn);
        Button viewAllBtn = findViewById(R.id.viewAllBtn);
        Button navTrendsBtn = findViewById(R.id.navTrendsBtn);
        Button navProfileBtn = findViewById(R.id.navProfileBtn);
        Button navWeightBtn = findViewById(R.id.navWeightBtn);
        //missing
        Button navImportBtn=findViewById(R.id.navImportBtn);
        Button navExportBtn = findViewById(R.id.navExportBtn);
        Button navInsightsBtn = findViewById(R.id.navInsightsBtn);
        Button navWaterBtn = findViewById(R.id.navWaterBtn);
        Button navBarcodeBtn = findViewById(R.id.navBarcodeBtn);
        Button navCameraBtn = findViewById(R.id.navCameraBtn);
        Button navSettingsBtn = findViewById(R.id.navSettingsBtn);
        Button navHelpBtn = findViewById(R.id.navHelpBtn);
        navHelpBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HelpActivity.class)));



        saveMealBtn = findViewById(R.id.saveMealBtn);
        myMealsBtn = findViewById(R.id.myMealsBtn);
        createMealBtn = findViewById(R.id.createMealBtn);
        //backup reovery
        backupBtn = findViewById(R.id.backupBtn);
        recoveryBtn = findViewById(R.id.recoveryBtn);
        //

        //sns share btn 2609142002
        //findViewById(R.id.shareBtn).setOnClickListener(v -> shareToday());
        findViewById(R.id.shareBtn).setOnClickListener(v -> shareDailySummary());//new 2609151910

        //findViewById(R.id.shareFoodBtn).setOnClickListener(v -> shareCurrentFood());
        findViewById(R.id.shareFoodBtn).setOnClickListener(v -> shareUserFoods());
        addImageBtn = findViewById(R.id.addImageBtn);
        deleteImageBtn = findViewById(R.id.deleteImageBtn);
        imageActionRow = findViewById(R.id.imageActionRow);

        updateUI();

        // ---- FOOD SEARCH ----
        searchFoodBtn.setOnClickListener(v -> {
            String query = foodInput.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(this, "Enter a food name", Toast.LENGTH_SHORT).show();
                return;
            }
            searchFoodByName(query);
        });

        // ---- EXERCISE SEARCH ----
        //begin 2searchexercise setonclicklistener
        /*
        searchExerciseBtn.setOnClickListener(v -> {
            String query = exerciseInput.getText().toString().trim();
            String weightStr = weightInput.getText().toString().trim();
            String durationStr = durationInput.getText().toString().trim();

            if (TextUtils.isEmpty(query)) {
                Toast.makeText(this, "Enter an exercise", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(weightStr) && userProfile.hasProfile()) {
                weightStr = String.valueOf(userProfile.getWeight());
                weightInput.setText(weightStr);
            }

            if (TextUtils.isEmpty(weightStr)) {
                Toast.makeText(this, "Enter your weight (kg)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(durationStr)) {
                Toast.makeText(this, "Enter duration (minutes)", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                int duration = Integer.parseInt(durationStr);
                ExerciseDatabase.ExerciseItem exercise = ExerciseDatabase.search(query);
                if (exercise != null) {
                    currentExerciseCalories = exercise.calculateCalories(weight, duration);
                    currentExerciseName = exercise.name;
                    resultExercise.setText("🏃 " + exercise.name + "\n" +
                            "Calories burned: " + currentExerciseCalories + " cal\n" +
                            "Duration: " + duration + " min\n" +
                            "Weight: " + weight + " kg");
                    Toast.makeText(this, "✅ Found: " + exercise.name, Toast.LENGTH_SHORT).show();
                    updateUI();
                } else {
                    resultExercise.setText("❌ No exercise found for: " + query);
                    currentExerciseCalories = 0;
                    currentExerciseName = "";
                    updateUI();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        */
        searchExerciseBtn.setOnClickListener(v -> {
            String query = exerciseInput.getText().toString().trim();
            String weightStr = weightInput.getText().toString().trim();
            String durationStr = durationInput.getText().toString().trim();

            if (TextUtils.isEmpty(query)) {
                Toast.makeText(this, "Enter an exercise", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(weightStr) && userProfile.hasProfile()) {
                weightStr = String.valueOf(userProfile.getWeight());
                weightInput.setText(weightStr);
            }

            if (TextUtils.isEmpty(weightStr)) {
                Toast.makeText(this, "Enter your weight (kg)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(durationStr)) {
                Toast.makeText(this, "Enter duration (minutes)", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                int duration = Integer.parseInt(durationStr);

                // SEARCH IN SQLITE DATABASE (imported exercises)
                DatabaseHelper.ExerciseItem exercise = dbHelper.searchExercise(query);

                if (exercise != null) {
                    currentExerciseCalories = exercise.calculateCalories(weight, duration);
                    currentExerciseName = exercise.name;
                    resultExercise.setText("🏃 " + exercise.name + "\n" +
                            "Calories burned: " + currentExerciseCalories + " cal\n" +
                            "Duration: " + duration + " min\n" +
                            "Weight: " + weight + " kg");
                    Toast.makeText(this, "✅ Found: " + exercise.name, Toast.LENGTH_SHORT).show();
                    updateUI();
                } else {
                    resultExercise.setText("❌ No exercise found for: " + query);
                    currentExerciseCalories = 0;
                    currentExerciseName = "";
                    updateUI();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        //end searchexercise setonclicklistener

        // ---- LOG ----
        logBtn.setOnClickListener(v -> {
            if (currentFoodCalories == 0 && currentExerciseCalories == 0) {
                Toast.makeText(this, "Search a food or exercise first", Toast.LENGTH_SHORT).show();
                return;
            }
            int net = currentFoodCalories - currentExerciseCalories;
            String foodName = TextUtils.isEmpty(currentFoodName) ? "None" : currentFoodName;
            String exerciseName = TextUtils.isEmpty(currentExerciseName) ? "None" : currentExerciseName;
            long id = dbHelper.insertLog(foodName, currentFoodCalories, exerciseName, currentExerciseCalories, net);
            if (id != -1) {
                Toast.makeText(this, "✅ Logged: Net " + net + " cal", Toast.LENGTH_SHORT).show();
                // Reset
                currentFoodCalories = 0;
                currentExerciseCalories = 0;
                currentFoodName = "";
                currentExerciseName = "";
                resultFood.setText("Enter a food name above");
                resultExercise.setText("Enter an exercise above");
                foodInput.setText("");
                exerciseInput.setText("");
                durationInput.setText("");
                if (userProfile.hasProfile()) {
                    weightInput.setText(String.valueOf(userProfile.getWeight()));
                }
                updateUI();
            } else {
                Toast.makeText(this, "❌ Failed to log", Toast.LENGTH_SHORT).show();
            }
            foodImage.setVisibility(View.GONE);//2609110639
            imageActionRow.setVisibility(View.GONE);//2609110926
        });

        // ---- CLEAR ----
        clearBtn.setOnClickListener(v -> {
            currentFoodCalories = 0;
            currentExerciseCalories = 0;
            currentFoodName = "";
            currentExerciseName = "";
            resultFood.setText("Enter a food name above");
            resultExercise.setText("Enter an exercise above");
            foodInput.setText("");
            exerciseInput.setText("");
            durationInput.setText("");
            if (userProfile.hasProfile()) {
                weightInput.setText(String.valueOf(userProfile.getWeight()));
            }
            updateUI();

            foodImage.setVisibility(View.GONE);//2609110636
            imageActionRow.setVisibility(View.GONE);//2609110925

            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
        });

        // ---- HISTORY ----
        viewAllBtn.setOnClickListener(v -> showHistoryDialog());

        // ---- NAVIGATION ----
        navTrendsBtn.setOnClickListener(v -> startActivity(new Intent(this, TrendsActivity.class)));
        /*
        findViewById(R.id.navVoiceBtn).setOnClickListener(v ->
                startActivity(new Intent(this, VoiceActivity.class)));*/

        findViewById(R.id.navVoiceBtn).setOnClickListener(v -> {
            Intent i = new Intent(this, VoiceActivity.class);
            startActivityForResult(i, VOICE_REQUEST_CODE);
        });

        navProfileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        navWeightBtn.setOnClickListener(v -> startActivity(new Intent(this, WeightActivity.class)));
        navImportBtn.setOnClickListener(v->startActivity(new Intent(this,ImportActivity.class)));
        navExportBtn.setOnClickListener(v -> exportData());
        navInsightsBtn.setOnClickListener(v -> showInsights());
        navWaterBtn.setOnClickListener(v -> startActivity(new Intent(this, WaterActivity.class)));
        //navBarcodeBtn.setOnClickListener(v -> startActivity(new Intent(this, BarcodeActivity.class)));
        //navCameraBtn.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));

        navBarcodeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivityForResult(intent, BARCODE_REQUEST_CODE);
        });
        navCameraBtn = findViewById(R.id.navCameraBtn);
        navCameraBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);  // Use the constant
        });

        navSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        saveMealBtn.setOnClickListener(v -> saveCurrentDayAsMeal());
        myMealsBtn.setOnClickListener(v -> showMyMeals());

        createMealBtn.setOnClickListener(v -> showCreateMealDialog());

        //backup recovey
        backupBtn.setOnClickListener(v -> performBackup());
        recoveryBtn.setOnClickListener(v -> showRecoveryDialog());

        //image add del btns
        //addImageBtn.setOnClickListener(v -> pickImageForFood());
        addImageBtn.setOnClickListener(v -> showImageSourceDialog());
        deleteImageBtn.setOnClickListener(v -> deleteImageForFood());

        Button exportUserFoodsBtn = findViewById(R.id.exportUserFoodsBtn);
        exportUserFoodsBtn.setOnClickListener(v -> exportUserFoods());

        //data button 2609161257
        Button dataBtn = findViewById(R.id.dataBtn);
        dataBtn.setOnClickListener(v -> showDataDialog());

        //dbHelper.fixExistingUserFoodsEditable();//fix the old ones to make editable 2610111615

        //whosper test 2610181330
        /*
        try {
            boolean loaded = WhisperBridge.isModelLoaded();
            Toast.makeText(this,
                    "Whisper native OK (model loaded: " + loaded + ")",
                    Toast.LENGTH_LONG).show();
        } catch (UnsatisfiedLinkError e) {
            Toast.makeText(this,
                    "Whisper native FAILED: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            android.util.Log.e("WhisperTest", "UnsatisfiedLinkError", e);
        }

         */
        //end test



        //new test 2610181456
        new Thread(() -> {
            java.io.File model = ensureModelInstalled();
            if (model == null) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Model install failed (check assets/models/ggml-base.bin)",
                        Toast.LENGTH_LONG).show());
                return;
            }

            long t0 = System.currentTimeMillis();
            boolean loaded = WhisperBridge.loadWhisperModel(model.getAbsolutePath());
            long elapsed = System.currentTimeMillis() - t0;

            final boolean ok = loaded;
            final long ms = elapsed;
            runOnUiThread(() -> Toast.makeText(this,
                    "Model load: " + ok + " (" + ms + " ms)",
                    Toast.LENGTH_LONG).show());
        }).start();

    }//onCreate

    // ============================================================
    // FOOD SEARCH
    // ============================================================


    private void searchFoodByName_old1(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        DatabaseHelper.FoodItem food = dbHelper.searchFood(query);
        if (food != null) {
            currentFoodCalories = food.calories;
            currentFoodName = food.name;
            resultFood.setText("🍔 " + food.name + "\n" +
                    "Calories: " + food.calories + " cal\n" +
                    "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g\n" +
                    "Serving: " + food.serving);
            updateUI();
            Toast.makeText(this, "✅ Found: " + food.name, Toast.LENGTH_SHORT).show();
        } else {
            resultFood.setText("❌ No food found for: " + query);
            currentFoodCalories = 0;
            currentFoodName = "";
            updateUI();
        }
    }

    //new searchfoodbyname and helpers

    private void searchFoodByName(String query) {
        if (query == null || query.isEmpty()) return;

        DatabaseHelper.FoodItem food = dbHelper.searchFood(query);
        if (food != null) {
            currentFoodCalories = food.calories;
            currentFoodName = food.name;
            resultFood.setText("🍔 " + food.name + "\n" +
                    "Calories: " + food.calories + " cal\n" +
                    "Protein: " + food.protein + "g | Carbs: " + food.carbs + "g | Fat: " + food.fat + "g\n" +
                    "Serving: " + food.serving);

            // Load image if available
            loadFoodImage(food.name);

            //success bytn

            currentFoodNameForImage = food.name;
            imageActionRow.setVisibility(View.VISIBLE);

            // Show Edit button only for editable user foods 2610111533  5c
            currentUserFood = food.isEditable ? food : null;
            editFoodBtn.setVisibility(food.isEditable ? View.VISIBLE : View.GONE);
            //end 26101111533

            updateUI();
            Toast.makeText(this, "✅ Found: " + food.name, Toast.LENGTH_SHORT).show();
        } else {
            //
            resultFood.setText("❌ No food found for: " + query);
            currentFoodCalories = 0;
            currentFoodName = "";
            foodImage.setVisibility(View.GONE);
            //failure button
            imageActionRow.setVisibility(View.GONE);

            //2610111535
            currentUserFood = null;
            editFoodBtn.setVisibility(View.GONE);



            //end 26101111535
            updateUI();
            //
            final String q = query;
            new AlertDialog.Builder(this)
                    .setTitle("Food not found")
                    .setMessage("Add \"" + q + "\" as a new food?")
                    .setPositiveButton("Add", (d, w) -> showAddFoodDialog(q))
                    .setNegativeButton("Cancel", null)
                    .show();




        }


    }

    private void loadFoodImage(String foodName) {
        // Get image_path from DB
        String imageFilename = dbHelper.getFoodImagePath(foodName);

        if (imageFilename == null || imageFilename.isEmpty()) {
            foodImage.setVisibility(View.GONE);
            return;
        }

        // Try to load from Downloads/CalSnap/food_images/
        String fullPath = BackupHelper.getImageFullPath(imageFilename);
        if (fullPath != null) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(fullPath);
                if (bmp != null) {
                    foodImage.setImageBitmap(bmp);
                    foodImage.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (Exception e) {
                android.util.Log.e("ImageLoad", "Failed: " + e.getMessage());
            }
        }

        // If file not found in Downloads, try assets as fallback
        try {
            java.io.InputStream is = getAssets().open("sample_images/" + imageFilename);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                foodImage.setImageBitmap(bmp);
                foodImage.setVisibility(View.VISIBLE);
                return;
            }
        } catch (Exception e) {
            // Silent — just hide
        }

        foodImage.setVisibility(View.GONE);
    }



    //



    // ============================================================
    // UPDATE UI
    // ============================================================
    private void updateUI() {
        int totalFood = dbHelper.getTodayTotalFoodCalories();
        int totalExercise = dbHelper.getTodayTotalExerciseCalories();
        int totalNet = dbHelper.getTodayTotalNetCalories();
        int streak = dbHelper.getStreak();

        dailyBudget = userProfile.getDailyBudget();

        if (userProfile.hasProfile()) {
            budgetDisplay.setText("🎯 Budget: " + dailyBudget + " cal/day");
        } else {
            budgetDisplay.setText("🎯 Budget: " + dailyBudget + " cal/day (default)");
        }

        dailyTotal.setText("📊 Today: " + totalNet + " / " + dailyBudget + " cal");
        todaySummary.setText("🍔 " + totalFood + " cal  |  🏃 -" + totalExercise + " cal");
        streakText.setText("🔥 " + streak + " day streak");

        adapter.updateData(dbHelper.getTodayEntries());
    }

    // ============================================================
    // DELETE ENTRY
    // ============================================================
    private void onDeleteEntry(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Delete this entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteEntry(id);
                    updateUI();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ============================================================
    // HISTORY DIALOG
    // ============================================================
    private void showHistoryDialog() {
        List<LogEntry> entries = dbHelper.getAllEntries();
        if (entries.isEmpty()) {
            Toast.makeText(this, "No entries yet", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Last 30 Days\n\n");
        for (LogEntry entry : entries) {
            String food = entry.getFoodName().equals("None") ? "" : "🍔 " + entry.getFoodName() + " (" + entry.getFoodCalories() + " cal)";
            String exercise = entry.getExerciseName().equals("None") ? "" : "🏃 " + entry.getExerciseName() + " (-" + entry.getExerciseCalories() + " cal)";
            String line = entry.getDate() + " ";
            if (!food.isEmpty()) line += food;
            if (!food.isEmpty() && !exercise.isEmpty()) line += " | ";
            if (!exercise.isEmpty()) line += exercise;
            line += " = " + entry.getNetCalories() + " cal\n";
            sb.append(line);
        }
        new AlertDialog.Builder(this)
                .setTitle("📊 Full History")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }


    private List<MealItem> tempMealItems = new ArrayList<>();

    private void showCreateMealDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Meal");

        // Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        // Food name input with autocomplete
        AutoCompleteTextView foodInput = new AutoCompleteTextView(this);
        foodInput.setHint("Search food...");
        foodInput.setThreshold(1);
        List<String> foodNames = dbHelper.getAllFoodNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNames);
        foodInput.setAdapter(adapter);
        layout.addView(foodInput);

        // Add button
        Button addBtn = new Button(this);
        addBtn.setText("Add to Meal");
        addBtn.setOnClickListener(v -> {
            String selected = foodInput.getText().toString().trim();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select a food", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.FoodItem food = dbHelper.searchFood(selected);
            if (food != null) {
                tempMealItems.add(new MealItem(food.name, food.calories, 1, food.serving));
                Toast.makeText(this, "Added: " + food.name, Toast.LENGTH_SHORT).show();
                foodInput.setText("");
            } else {
                Toast.makeText(this, "Food not found", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(addBtn);

        // List of added items (TextView for simplicity)
        TextView itemsText = new TextView(this);
        itemsText.setText("Added items: (none)");
        itemsText.setTextColor(getColor(R.color.white));
        itemsText.setPadding(0, 16, 0, 16);
        layout.addView(itemsText);

        //add the servings for recipes'
        final EditText servingsInput = new EditText(this);
        servingsInput.setHint("Servings (how many portions)");
        servingsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        servingsInput.setText("1");
        layout.addView(servingsInput);
        //end servings 2609151712


        // Update the list when items change
        // We'll update in the add button listener and later in save

        // Save button
        Button saveBtn = new Button(this);
        saveBtn.setText("Save Meal");

        saveBtn.setOnClickListener(v -> {
            if (tempMealItems.isEmpty()) {
                Toast.makeText(this, "Add at least one food", Toast.LENGTH_SHORT).show();
                return;
            }

            int servings;
            try {
                servings = Integer.parseInt(servingsInput.getText().toString().trim());
                if (servings < 1) servings = 1;
            } catch (Exception e) {
                servings = 1;
            }
            final int finalServings = servings;

            AlertDialog.Builder nameBuilder = new AlertDialog.Builder(this);
            nameBuilder.setTitle("Meal Name");
            final EditText nameInput = new EditText(this);
            nameInput.setHint("e.g., My Breakfast");
            nameBuilder.setView(nameInput);
            nameBuilder.setPositiveButton("Save", (d, which) -> {
                String mealName = nameInput.getText().toString().trim();
                if (mealName.isEmpty()) return;

                MealMacro macro = new MealMacro(mealName, new ArrayList<>(tempMealItems));
                macro.servings = finalServings;
                dbHelper.saveMealMacro(macro);
                tempMealItems.clear();
                Toast.makeText(this, "✅ Saved: " + mealName +
                        " (" + finalServings + " servings)", Toast.LENGTH_SHORT).show();
                d.dismiss();
            });
            nameBuilder.setNegativeButton("Cancel", null);
            nameBuilder.show();
        });

        //
        /*
        saveBtn.setOnClickListener(v -> {
            if (tempMealItems.isEmpty()) {
                Toast.makeText(this, "Add at least one food", Toast.LENGTH_SHORT).show();
                return;
            }
            // Prompt for meal name
            AlertDialog.Builder nameBuilder = new AlertDialog.Builder(this);
            nameBuilder.setTitle("Meal Name");
            final EditText nameInput = new EditText(this);
            nameInput.setHint("e.g., My Breakfast");
            nameBuilder.setView(nameInput);
            nameBuilder.setPositiveButton("Save", (d, which) -> {
                String mealName = nameInput.getText().toString().trim();
                if (mealName.isEmpty()) {
                    Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                MealMacro macro = new MealMacro(mealName, new ArrayList<>(tempMealItems));
                dbHelper.saveMealMacro(macro);
                tempMealItems.clear();
                Toast.makeText(this, "✅ Meal saved: " + mealName, Toast.LENGTH_SHORT).show();
                d.dismiss();
            });
            nameBuilder.setNegativeButton("Cancel", null);
            nameBuilder.show();
        });
        */





        layout.addView(saveBtn);

        builder.setView(layout);
        builder.setNegativeButton("Cancel", (d, which) -> tempMealItems.clear());
        builder.show();
    }

    // ============================================================
    // EXPORT
    // ============================================================
    private void exportData() {
        String csv = dbHelper.exportToCSV();
        new AlertDialog.Builder(this)
                .setTitle("📤 Export Data")
                .setMessage("Copy CSV data:\n\n" + csv)
                .setPositiveButton("Copy", (dialog, which) -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("CalSnap Data", csv);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "✅ Copied to clipboard", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    // ============================================================
    // INSIGHTS
    // ============================================================
    private void showInsights() {
        int totalNet = dbHelper.getTodayTotalNetCalories();
        int totalFood = dbHelper.getTodayTotalFoodCalories();
        int totalExercise = dbHelper.getTodayTotalExerciseCalories();
        int streak = dbHelper.getStreak();
        int budget = userProfile.getDailyBudget();
        int entries = dbHelper.getTodayEntryCount();

        StringBuilder insights = new StringBuilder();

        if (entries == 0) {
            insights.append("📝 No entries logged today.\nStart logging to track your progress!\n\n");
        } else if (totalNet > budget + 200) {
            insights.append("⚠️ You're OVER budget by " + (totalNet - budget) + " cal today.\nTry adding some exercise.\n\n");
        } else if (totalNet < budget - 200) {
            insights.append("✅ You're UNDER budget by " + (budget - totalNet) + " cal today.\nGreat job!\n\n");
        } else {
            insights.append("✅ You're ON TRACK today!\nKeep up the good work!\n\n");
        }

        if (totalExercise == 0 && totalFood > 0) {
            insights.append("🏃 No exercise logged today.\nEven a 20-minute walk helps!\n\n");
        } else if (totalExercise > 300) {
            insights.append("🏃 Great workout! You burned " + totalExercise + " cal.\n\n");
        }

        if (streak > 0) {
            insights.append("🔥 " + streak + "-day logging streak!\n");
            if (streak >= 7) {
                insights.append("Amazing consistency!\n");
            } else {
                insights.append("Keep going!\n");
            }
        }

        if (insights.length() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("💡 Daily Insights")
                    .setMessage(insights.toString())
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Toast.makeText(this, "Log some food or exercise to get insights!", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================================================
    // DEFAULT DATA
    // ============================================================
    private void addDefaultFoods() {
        // Add default foods if database is empty
        String[][] defaultFoods = {
                {"Pizza", "285", "12", "36", "10", "1 slice"},
                {"Cheeseburger", "303", "15", "30", "13", "1 sandwich"},
                {"Hot Dog", "290", "10", "29", "15", "1 serving"},
                {"Rice", "206", "4.3", "45", "0.4", "1 cup"},
                {"Chicken Breast", "165", "31", "0", "3.6", "1 breast"},
                {"Egg", "140", "12", "1", "10", "2 eggs"},
                {"Apple", "95", "0.5", "25", "0.3", "1 medium"},
                {"Banana", "105", "1.3", "27", "0.4", "1 medium"},
                {"Bread", "79", "2.5", "15", "1", "1 slice"},
                {"Milk", "149", "8", "12", "8", "1 cup"},
        };
        for (String[] f : defaultFoods) {
            dbHelper.insertFood(f[0], Integer.parseInt(f[1]), (int)Double.parseDouble(f[2]),
                    (int)Double.parseDouble(f[3]), (int)Double.parseDouble(f[4]), f[5], "");
        }
    }

    private void addDefaultExercises() {
        String[][] defaultExercises = {
                {"Walking slow", "2.5"},
                {"Walking moderate", "3.5"},
                {"Jogging", "6.0"},
                {"Running", "8.0"},
                {"Cycling", "6.0"},
                {"Swimming", "5.0"},
                {"Weight Lifting", "3.0"},
                {"Yoga", "2.5"},
                {"Dancing", "4.5"},
        };
        for (String[] e : defaultExercises) {
            dbHelper.insertExercise(e[0], Double.parseDouble(e[1]));
        }
    }//adddefaultexercisez


    private void saveCurrentDayAsMeal() {
        // Get today's food entries (only foods, not exercises)
        List<LogEntry> todayEntries = dbHelper.getTodayEntries();
        List<LogEntry> foodEntries = new ArrayList<>();
        for (LogEntry entry : todayEntries) {
            if (entry.getFoodCalories() > 0 && !entry.getFoodName().equals("None")) {
                foodEntries.add(entry);
            }
        }

        if (foodEntries.isEmpty()) {
            Toast.makeText(this, "No food entries today to save as meal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prompt for meal name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as Meal");
        final EditText input = new EditText(this);
        input.setHint("Meal name (e.g., Breakfast1)");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String mealName = input.getText().toString().trim();
            if (mealName.isEmpty()) {
                Toast.makeText(this, "Please enter a meal name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build MealMacro from food entries
            List<MealItem> items = new ArrayList<>();
            for (LogEntry entry : foodEntries) {
                // We need to get quantity from serving or assume 1
                // For simplicity, we'll use quantity = 1
                items.add(new MealItem(
                        entry.getFoodName(),
                        entry.getFoodCalories(),
                        1,
                        "1 serving"
                ));
            }

            MealMacro macro = new MealMacro(mealName, items);
            dbHelper.saveMealMacro(macro);
            Toast.makeText(this, "✅ Meal '" + mealName + "' saved with " + items.size() + " items", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }//savecurrentdayasmeal

    private void showMyMeals() {
        List<MealMacro> meals = dbHelper.getAllMealMacros();
        if (meals.isEmpty()) {
            Toast.makeText(this, "No saved meals yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] mealNames = new String[meals.size()];
        for (int i = 0; i < meals.size(); i++) {
            MealMacro m = meals.get(i);
            mealNames[i] = m.name + " — " + m.getCaloriesPerServing() +
                    " cal/serving × " + m.servings;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("My Meals / Recipes")
                .setItems(mealNames, (dialog, which) -> showMealDetails(meals.get(which)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMealDetails(final MealMacro meal) {
        StringBuilder sb = new StringBuilder();
        sb.append("Servings in batch: ").append(meal.servings).append("\n");
        sb.append("Total batch calories: ").append(meal.totalCalories).append("\n");
        sb.append("Per serving: ").append(meal.getCaloriesPerServing()).append(" cal\n\n");
        sb.append("Items:\n");

        int batch = Math.max(1, meal.servings);
        for (MealItem item : meal.items) {
            int perServing = item.calories * item.quantity / batch;
            sb.append("• ").append(item.foodName)
                    .append(" — ").append(perServing).append(" cal/serving\n");
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(meal.name)
                .setMessage(sb.toString())
                .setPositiveButton("Log", (d, w) -> logMealMacro(meal))
                //.setNeutralButton("Delete", (d, w) -> confirmDeleteMeal(meal))//
                .setNeutralButton("Edit", (d, w) -> showEditMealDialog(meal)) //2610091931 Delete now lives inside the Edit dialog instead (below).
                .setNegativeButton("Close", null)
                .show();
    }

    private void confirmDeleteMeal(final MealMacro meal) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete " + meal.name + "?")
                .setMessage("This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    dbHelper.deleteMealMacro(meal.name);
                    Toast.makeText(this, "🗑️ Deleted: " + meal.name, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    //2609151756
    private void showMyMeals_old2() {
        List<MealMacro> meals = dbHelper.getAllMealMacros();
        if (meals.isEmpty()) {
            Toast.makeText(this, "No saved meals yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare list of meal names with total calories
        String[] mealNames = new String[meals.size()];
        for (int i = 0; i < meals.size(); i++) {
            mealNames[i] = meals.get(i).name + " (" + meals.get(i).totalCalories + " cal)";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("My Meals");
        builder.setItems(mealNames, (dialog, which) -> {
            MealMacro selected = meals.get(which);
            logMealMacro(selected);
        });
        builder.setPositiveButton("Cancel", null);
        builder.show();
    }

    private void logMealMacro_old1(MealMacro meal) {
        // Log each item in the meal
        for (MealItem item : meal.items) {
            // Insert each item as a log entry (without exercise)
            dbHelper.insertLog(
                    item.foodName,
                    item.calories,
                    "None",
                    0,
                    item.calories,
                    meal.name // meal name as category (optional)
            );
        }
        Toast.makeText(this, "✅ Logged: " + meal.name, Toast.LENGTH_SHORT).show();
        updateUI();
    }//logmealmacro


    private void logMealMacro(final MealMacro meal) {
        // Ask how many servings eaten
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Number of servings");
        input.setText("1");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Log " + meal.name)
                .setMessage("This batch makes " + meal.servings + " serving(s).\n\n" +
                        "How many servings did you eat?")
                .setView(input)
                .setPositiveButton("Log", (d, w) -> {
                    int eaten;
                    try {
                        eaten = Integer.parseInt(input.getText().toString().trim());
                        if (eaten < 1) eaten = 1;
                    } catch (Exception e) {
                        eaten = 1;
                    }
                    performLog(meal, eaten);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLog(MealMacro meal, int servingsEaten) {
        int batch = meal.servings > 0 ? meal.servings : 1;
        // Portion size = 1/batch of the full recipe.
        // If you ate `servingsEaten` portions, total = full × (eaten / batch).
        // Per-food calories logged = itemCal × quantity × eaten / batch.

        for (MealItem item : meal.items) {
            int fullCal = item.calories * item.quantity;
            int loggedCal = fullCal * servingsEaten / batch;

            dbHelper.insertLog(
                    item.foodName + " (" + servingsEaten + "/" + batch + " of " + meal.name + ")",
                    loggedCal,
                    "None",
                    0,
                    loggedCal,
                    meal.name
            );
        }

        Toast.makeText(this, "✅ Logged: " + meal.name +
                " × " + servingsEaten + " serving(s)", Toast.LENGTH_SHORT).show();
        updateUI();
    }
    //logMealmacro,performLog

    /*
    private void showMyMeals() {
        List<MealMacro> meals = dbHelper.getAllMealMacros();
        if (meals.isEmpty()) {
            Toast.makeText(this, "No saved meals yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare list of meal names with total calories
        String[] mealNames = new String[meals.size()];
        for (int i = 0; i < meals.size(); i++) {
            mealNames[i] = meals.get(i).name + " (" + meals.get(i).totalCalories + " cal)";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("My Meals");
        builder.setItems(mealNames, (dialog, which) -> {
            MealMacro selected = meals.get(which);
            logMealMacro(selected);
        });
        builder.setPositiveButton("Cancel", null);
        builder.show();
    }
   */

    /*
    private void logMealMacro(MealMacro meal) {
        // Log each item in the meal
        for (MealItem item : meal.items) {
            // Insert each item as a log entry (without exercise)
            dbHelper.insertLog(
                    item.foodName,
                    item.calories,
                    "None",
                    0,
                    item.calories,
                    meal.name // meal name as category (optional)
            );
        }
        Toast.makeText(this, "✅ Logged: " + meal.name, Toast.LENGTH_SHORT).show();
        updateUI();
    }//showmymeal

     */



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                saveUserImageForFood(imageUri);
            }
        }
        // Take photo with camera (for food image)

        else if (requestCode == TAKE_PHOTO_REQUEST  && resultCode == RESULT_OK && data != null) {
            // Camera returns a Bitmap
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                saveBitmapForFood(photo);
            }
        }

        // Camera activity (AI/Google search)

        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String foodName = data.getStringExtra("FOOD_NAME");
                if (foodName != null && !foodName.isEmpty()) {
                    // Set the text in the autocomplete field
                    foodInput.setText(foodName);
                    // Trigger search
                    searchFoodByName(foodName);
                }
            }
        }
        //barcode  2609040207
        else if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String foodName = data.getStringExtra("FOOD_NAME");
                if (foodName != null && !foodName.isEmpty()) {
                    // Fill the search field
                    foodInput.setText(foodName);
                    // Trigger search
                    searchFoodByName(foodName);
                }
            }
        }

        else if (requestCode == PICK_BACKUP_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) confirmRestoreFromUri(uri);
        }

        else if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //String food = data.getStringExtra("recognized_food");
            String food = data.getStringExtra("FOOD_NAME");
            if (food != null) {
                android.widget.AutoCompleteTextView input = findViewById(R.id.foodInput);
                input.setText(food);
                findViewById(R.id.searchFoodBtn).performClick();
            }
        }



    }//onActivityResult

    private void performBackup() {
        String backupPath = BackupHelper.backupDatabase(this);
        if (backupPath != null) {
            Toast.makeText(this, "✅ Backup saved to:\n" + backupPath, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "❌ Backup failed", Toast.LENGTH_SHORT).show();
        }
    }//performbackup 26090922

    private static final int PICK_BACKUP_REQUEST = 700;

    private void showRecoveryDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Restore Database")
                .setMessage("Choose a .db file from Downloads/CalSnap/backup/")
                .setPositiveButton("Choose File", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_BACKUP_REQUEST);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    /* //only shows most recent db file
    private void showRecoveryDialog() {
        String[] backups = BackupHelper.listBackups();
        if (backups.length == 0) {
            Toast.makeText(this, "No backups found", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Backup to Restore");
        builder.setItems(backups, (dialog, which) -> {
            String backupPath = BackupHelper.getBackupFolder() + "/" + backups[which];
            confirmRestore(backupPath);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }//showrecoverydialog  2609092242

     */

    private void confirmRestore(String backupPath) {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Restore Database")
                .setMessage("This will overwrite ALL current data with the backup. Continue?")
                .setPositiveButton("Restore", (dialog, which) -> {
                    if (BackupHelper.restoreDatabase(this, backupPath)) {
                        Toast.makeText(this, "✅ Database restored successfully", Toast.LENGTH_LONG).show();
                        // Restart activity to refresh data
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(this, "❌ Restore failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }//confirmrestore 2609092241


    //
    private void confirmRestoreFromUri(final Uri uri) {//2609131857
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("⚠️ Restore Database")
                .setMessage("This will overwrite ALL current data. Continue?")
                .setPositiveButton("Restore", (dialog, which) -> {
                    try {
                        java.io.InputStream is = getContentResolver().openInputStream(uri);
                        java.io.File dbFile = getDatabasePath("CalSnap.db");
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(dbFile);
                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
                        fos.close();
                        is.close();

                        Toast.makeText(this, "✅ Restored. Restarting...", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                    } catch (Exception e) {
                        Toast.makeText(this, "❌ " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    //

    //dbhelper:for food image paths 2609100207
    private void preloadSampleImagesOnce_old() {
        int copied = BackupHelper.preloadSampleImages(this);
        android.util.Log.d("Preload", "Copied " + copied + " images");

        String[] imageFiles = BackupHelper.listImageFiles();
        for (String filename : imageFiles) {
            String baseName = filename;
            int dotIdx = baseName.lastIndexOf('.');
            if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);

            String withSpace = baseName.replace("_", " ");
            String withUnderscore = baseName.replace(" ", "_");

            dbHelper.updateFoodImagePath(withSpace, filename);
            dbHelper.updateFoodImagePath(withUnderscore, filename);
        }
    }


    //android 11 cannot read externally created files thus this doesnt work 2609110235
    private void preloadSampleImagesOnce_old1() {
        android.util.Log.d("Preload", "=== START ===");

        int copied = BackupHelper.preloadSampleImages(this);
        android.util.Log.d("Preload", "Copied: " + copied);

        String folder = BackupHelper.getImagesFolder();
        android.util.Log.d("Preload", "Folder: " + folder);

        String[] imageFiles = BackupHelper.listImageFiles();
        android.util.Log.d("Preload", "Found " + imageFiles.length + " files");

        for (String filename : imageFiles) {
            String baseName = filename;
            int dotIdx = baseName.lastIndexOf('.');
            if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);

            String withSpace = baseName.replace("_", " ");
            String withUnderscore = baseName.replace(" ", "_");

            android.util.Log.d("Preload", "Updating: '" + withSpace + "' / '" + withUnderscore + "' -> " + filename);

            dbHelper.updateFoodImagePath(withSpace, filename);
            dbHelper.updateFoodImagePath(withUnderscore, filename);
        }

        android.util.Log.d("Preload", "=== END ===");
    }

    private void preloadSampleImagesOnce_old2() {
        android.util.Log.d("Preload", "=== START ===");

        // Copy images from assets to Downloads (works — app creates them)
        int copied = BackupHelper.preloadSampleImages(this);
        android.util.Log.d("Preload", "Copied: " + copied);

        // List from ASSETS (not from Downloads)
        try {
            android.content.res.AssetManager am = getAssets();
            String[] imageFiles = am.list("sample_images");
            android.util.Log.d("Preload", "Found " + (imageFiles != null ? imageFiles.length : 0) + " files in assets");

            if (imageFiles != null) {
                for (String filename : imageFiles) {
                    String baseName = filename;
                    int dotIdx = baseName.lastIndexOf('.');
                    if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);

                    String withSpace = baseName.replace("_", " ");
                    String withUnderscore = baseName.replace(" ", "_");

                    android.util.Log.d("Preload", "Updating: '" + withSpace + "' -> " + filename);

                    dbHelper.updateFoodImagePath(withSpace, filename);
                    dbHelper.updateFoodImagePath(withUnderscore, filename);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Preload", "Error: " + e.getMessage());
        }

        android.util.Log.d("Preload", "=== END ===");
    }

    private void preloadSampleImagesOnce() {
        try {
            android.content.res.AssetManager am = getAssets();
            String[] imageFiles = am.list("sample_images");

            if (imageFiles != null) {
                for (String filename : imageFiles) {
                    String baseName = filename;
                    int dotIdx = baseName.lastIndexOf('.');
                    if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);

                    String withSpace = baseName.replace("_", " ");
                    String withUnderscore = baseName.replace(" ", "_");

                    dbHelper.updateFoodImagePath(withSpace, filename);
                    dbHelper.updateFoodImagePath(withUnderscore, filename);
                }

                // Manual aliases for mismatched names
                dbHelper.updateFoodImagePath("baked potatoes", "baked_potato.jpg");
            }
        } catch (Exception e) {
            android.util.Log.e("Preload", "Error: " + e.getMessage());
        }
    }

    //img add del related 260911
    private void pickImageForFood() {
        if (currentFoodNameForImage.isEmpty()) {
            Toast.makeText(this, "Search a food first", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    private void saveUserImageForFood(Uri imageUri) {
        try {
            // Filename: food name with underscores
            String filename = currentFoodNameForImage.toLowerCase().replace(" ", "_") + ".jpg";

            // Save to Downloads/CalSnap/food_images/
            String folder = BackupHelper.getImagesFolder();
            File destFile = new File(folder, filename);

            InputStream is = getContentResolver().openInputStream(imageUri);
            OutputStream os = new FileOutputStream(destFile);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.close();
            is.close();

            // Update DB
            dbHelper.updateFoodImagePath(currentFoodNameForImage, filename);

            // Refresh display
            loadFoodImage(currentFoodNameForImage);

            Toast.makeText(this, "✅ Image saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            android.util.Log.e("ImageSave", e.getMessage());
        }
    }

    private void deleteImageForFood() {
        if (currentFoodNameForImage.isEmpty()) return;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Remove image for '" + currentFoodNameForImage + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String imageFilename = dbHelper.getFoodImagePath(currentFoodNameForImage);
                    if (imageFilename != null && !imageFilename.isEmpty()) {
                        File file = new File(BackupHelper.getImagesFolder(), imageFilename);
                        if (file.exists()) file.delete();
                    }
                    dbHelper.updateFoodImagePath(currentFoodNameForImage, "");
                    foodImage.setVisibility(View.GONE);
                    Toast.makeText(this, "🗑️ Image deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    //end img all del related end

    private void showImageSourceDialog() {
        if (currentFoodNameForImage.isEmpty()) {
            Toast.makeText(this, "Search a food first", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = {"📷 Take Photo", "🖼️ Choose from Gallery"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Add Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        requestCameraAndTakePhoto();
                    } else {
                        pickImageFromGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestCameraAndTakePhoto() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    //select existing image or camera knips 2609110948
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //end imagesrc sel gallery or camera


    //2609110954
    private void saveBitmapForFood(Bitmap bitmap) {
        try {
            String filename = currentFoodNameForImage.toLowerCase().replace(" ", "_") + ".jpg";
            String folder = BackupHelper.getImagesFolder();
            File destFile = new File(folder, filename);

            FileOutputStream fos = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();

            dbHelper.updateFoodImagePath(currentFoodNameForImage, filename);
            loadFoodImage(currentFoodNameForImage);

            Toast.makeText(this, "✅ Photo saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //sharetoday 2609142008

    private void shareToday() {
        TextView dailyTotal    = findViewById(R.id.dailyTotal);
        TextView todaySummary  = findViewById(R.id.todaySummary);
        TextView budgetDisplay = findViewById(R.id.budgetDisplay);
        TextView streakText    = findViewById(R.id.streakText);

        String net     = dailyTotal    != null ? dailyTotal.getText().toString()    : "";
        String eaten   = todaySummary  != null ? todaySummary.getText().toString()  : "";
        String budget  = budgetDisplay != null ? budgetDisplay.getText().toString() : "";
        String streak  = streakText    != null ? streakText.getText().toString()    : "";

        String msg = "My CalMatrix Offline today:\n"
                + net    + "\n"
                + eaten  + "\n"
                + budget + "\n"
                + streak + "\n\n"
                + "Tracked 100% offline with CalMatrix Offline 📸";

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(send, "Share via"));
    }
    //end sharetoday

    private void shareDailySummary() {
        int totalFood = dbHelper.getTodayTotalFoodCalories();
        int totalExercise = dbHelper.getTodayTotalExerciseCalories();
        int totalNet = dbHelper.getTodayTotalNetCalories();
        int streak = dbHelper.getStreak();
        int budget = userProfile.getDailyBudget();

        if (totalFood == 0 && totalExercise == 0) {
            Toast.makeText(this, "Log some food or exercise first", Toast.LENGTH_SHORT).show();
            return;
        }

        String status;
        if (totalNet > budget + 200) status = "⚠️ Over budget";
        else if (totalNet < budget - 200) status = "✅ Under budget";
        else status = "✅ On track";

        String text =
                "📊 My CalMatrix day\n" +
                        "🔥 Streak: " + streak + " day(s)\n" +
                        "🍔 Food: " + totalFood + " cal\n" +
                        "🏃 Exercise: -" + totalExercise + " cal\n" +
                        "📈 Net: " + totalNet + " / " + budget + " cal\n" +
                        status+
                        "\n\n" + "Tracked 100% offline with CalMatrix Offline 📸";



                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My CalMatrix Day");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void exportUserFoods() {
        String csv = dbHelper.exportUserFoodsToCSV();

        // If only header row, nothing to export
        if (csv.trim().split("\n").length <= 1) {
            Toast.makeText(this, "No user-added foods to export yet",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault()).format(new java.util.Date());
        String filename = "user_foods_" + timestamp + ".csv";

        boolean ok = BackupHelper.exportCSV(this, csv, filename);
        if (ok) {
            Toast.makeText(this,
                    "✅ Exported to Downloads/CalSnap/csv/" + filename,
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "❌ Export failed", Toast.LENGTH_SHORT).show();
        }
    }//2609152148

    private void showDataDialog() {
        String[] options = {
                "📥 Import CSV",
                "📤 Export User Foods",
                "💾 Backup Database",
                "🔄 Recover Database",
                "📱 Share Today's Summary"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Data")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new android.content.Intent(this, ImportActivity.class));
                            break;
                        case 1:
                            exportUserFoods();
                            break;
                        case 2:
                            performBackup();
                            break;
                        case 3:
                            showRecoveryDialog();
                            break;
                        case 4:
                            shareDailySummary();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }//2609161257


    //moment? shouldnt it share the csv saved food?2609161641
    private void shareCurrentFood() {
        TextView resultFood = findViewById(R.id.resultFood);
        String text = resultFood != null ? resultFood.getText().toString().trim() : "";
        if (text.isEmpty() || text.equals("Enter a food name")) {
            Toast.makeText(this, "Search a food first, then tap Share Food", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "🍽 " + text + "\n\nTracked with CalMatrix Offline 📸");
        startActivity(Intent.createChooser(share, "Share via"));
    }

    private void shareUserFoods() {
        String csv = dbHelper.exportUserFoodsToCSV();

        // If only header row, nothing to share
        if (csv.trim().split("\n").length <= 1) {
            Toast.makeText(this, "No user-added foods to share yet",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String header = "🍽 My CalMatrix custom foods\n\n";
        String footer = "\n\nTracked with CalMatrix Offline 📸";

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "My CalMatrix custom foods");
        share.putExtra(Intent.EXTRA_TEXT, header + csv + footer);
        startActivity(Intent.createChooser(share, "Share via"));
    }

    private void showEditMealDialog(final MealMacro meal) {//2610091931
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Meal name");
        nameInput.setText(meal.name);
        layout.addView(nameInput);

        final EditText servingsInput = new EditText(this);
        servingsInput.setHint("Servings in batch");
        servingsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        servingsInput.setText(String.valueOf(meal.servings));
        layout.addView(servingsInput);

        TextView itemsView = new TextView(this);
        itemsView.setPadding(0, 24, 0, 0);
        StringBuilder sb = new StringBuilder("Items (" + meal.items.size() + "):\n");
        for (MealItem item : meal.items) {
            sb.append("• ").append(item.foodName)
                    .append(" — ").append(item.calories).append(" cal\n");
        }
        sb.append("\n(Item editing coming in v1.2)");
        itemsView.setText(sb.toString());
        layout.addView(itemsView);

        new AlertDialog.Builder(this)
                .setTitle("Edit meal")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = nameInput.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int newServings;
                    try {
                        newServings = Integer.parseInt(servingsInput.getText().toString().trim());
                        if (newServings < 1) newServings = 1;
                    } catch (Exception e) {
                        newServings = 1;
                    }

                    MealMacro updated = new MealMacro(newName, meal.items);
                    updated.servings = newServings;

                    // saveMealMacro uses INSERT (not REPLACE), so delete the old row first
                    dbHelper.deleteMealMacro(meal.name);
                    dbHelper.saveMealMacro(updated);

                    Toast.makeText(this, "✅ Updated: " + newName + " (" + newServings + " servings)", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Delete", (d, w) -> confirmDeleteMeal(meal))
                .setNegativeButton("Cancel", null)
                .show();
    }

    //6. added 2610111539
    private void showEditUserFoodDialog(final DatabaseHelper.FoodItem food) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        nameInput.setText(food.name);
        layout.addView(nameInput);

        final EditText calInput = new EditText(this);
        calInput.setHint("Calories");
        calInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        calInput.setText(String.valueOf(food.calories));
        layout.addView(calInput);

        final EditText proInput = new EditText(this);
        proInput.setHint("Protein (g)");
        proInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        proInput.setText(String.valueOf(food.protein));
        layout.addView(proInput);

        final EditText carbInput = new EditText(this);
        carbInput.setHint("Carbs (g)");
        carbInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        carbInput.setText(String.valueOf(food.carbs));
        layout.addView(carbInput);

        final EditText fatInput = new EditText(this);
        fatInput.setHint("Fat (g)");
        fatInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        fatInput.setText(String.valueOf(food.fat));
        layout.addView(fatInput);

        final EditText servingInput = new EditText(this);
        servingInput.setHint("Serving");
        servingInput.setText(food.serving);
        layout.addView(servingInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit food")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = nameInput.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int cal, pro, carb, fat;
                    try {
                        cal  = Integer.parseInt(calInput.getText().toString().trim());
                        pro  = Integer.parseInt(proInput.getText().toString().trim());
                        carb = Integer.parseInt(carbInput.getText().toString().trim());
                        fat  = Integer.parseInt(fatInput.getText().toString().trim());
                    } catch (Exception e) {
                        Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String serving = servingInput.getText().toString().trim();
                    if (serving.isEmpty()) serving = "1 serving";

                    boolean ok = dbHelper.updateUserFood(food.name, newName, cal, pro, carb, fat, serving);
                    if (ok) {
                        Toast.makeText(this, "✅ Updated: " + newName, Toast.LENGTH_SHORT).show();
                        searchFoodByName(newName);
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }//end showEditUserFoodDialog 2610111540



    //coordination consistency  of add food procedyre 2610112211
    private void showAddFoodDialog(String prefillName) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        nameInput.setText(prefillName);
        layout.addView(nameInput);

        final EditText calInput = new EditText(this);
        calInput.setHint("Calories");
        calInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(calInput);

        final EditText proInput = new EditText(this);
        proInput.setHint("Protein (g)");
        proInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(proInput);

        final EditText carbInput = new EditText(this);
        carbInput.setHint("Carbs (g)");
        carbInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(carbInput);

        final EditText fatInput = new EditText(this);
        fatInput.setHint("Fat (g)");
        fatInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(fatInput);

        final EditText servingInput = new EditText(this);
        servingInput.setHint("Serving (e.g. 1 cup)");
        layout.addView(servingInput);

        new AlertDialog.Builder(this)
                .setTitle("Add new food")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int cal, pro, carb, fat;
                    try {
                        cal  = Integer.parseInt(calInput.getText().toString().trim());
                        pro  = Integer.parseInt(proInput.getText().toString().trim());
                        carb = Integer.parseInt(carbInput.getText().toString().trim());
                        fat  = Integer.parseInt(fatInput.getText().toString().trim());
                    } catch (Exception e) {
                        Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String serving = servingInput.getText().toString().trim();
                    if (serving.isEmpty()) serving = "1 serving";

                    dbHelper.insertUserFood(name, cal, pro, carb, fat, serving, "");
                    Toast.makeText(this, "✅ Added: " + name, Toast.LENGTH_SHORT).show();
                    searchFoodByName(name);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }//end showadddialog

    private java.io.File ensureModelInstalled() {//2610181454
        java.io.File model = new java.io.File(getFilesDir(), "ggml-base.bin");
        if (model.exists()) return model;

        try {
            java.io.InputStream is = getAssets().open("models/ggml-base.bin");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(model);
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) fos.write(buf, 0, n);
            is.close();
            fos.close();
        } catch (Exception e) {
            android.util.Log.e("WhisperTest", "copy failed", e);
            return null;
        }
        return model;
    }

    // ============================================================
    // ON RESUME
    // ============================================================
    @Override
    protected void onResume() {
        super.onResume();
        dailyBudget = userProfile.getDailyBudget();
        if (userProfile.hasProfile()) {
            weightInput.setText(String.valueOf(userProfile.getWeight()));
        }
        updateUI();
    }
}