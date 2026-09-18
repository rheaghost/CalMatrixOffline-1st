package com.calmatrixoffline;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText weightInput, heightInput, ageInput;
    private RadioGroup genderGroup, activityGroup, goalGroup;
    private TextView bmrResult, budgetResult, savedStatus;
    private Button saveBtn, calculateBtn, clearProfileBtn;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfile = new UserProfile(this);

        // Initialize views
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        activityGroup = findViewById(R.id.activityGroup);
        goalGroup = findViewById(R.id.goalGroup);
        bmrResult = findViewById(R.id.bmrResult);
        budgetResult = findViewById(R.id.budgetResult);
        savedStatus = findViewById(R.id.savedStatus);
        saveBtn = findViewById(R.id.saveBtn);
        calculateBtn = findViewById(R.id.calculateBtn);
        clearProfileBtn = findViewById(R.id.clearProfileBtn);

        loadProfile();

        calculateBtn.setOnClickListener(v -> calculateAndDisplay());
        saveBtn.setOnClickListener(v -> saveProfile());

        clearProfileBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear Profile")
                    .setMessage("Are you sure you want to clear your profile data?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        userProfile.clearProfile();
                        Toast.makeText(this, "Profile cleared. Budget set to 2000 cal.", Toast.LENGTH_SHORT).show();
                        weightInput.setText("");
                        heightInput.setText("");
                        ageInput.setText("");
                        genderGroup.clearCheck();
                        activityGroup.clearCheck();
                        goalGroup.clearCheck();
                        bmrResult.setText("Enter your details and tap Calculate");
                        budgetResult.setText("Daily Budget: -- cal/day");
                        savedStatus.setText("No profile saved yet");
                        savedStatus.setTextColor(getColor(android.R.color.holo_orange_light));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadProfile() {
        if (userProfile.hasProfile()) {
            weightInput.setText(String.valueOf(userProfile.getWeight()));
            heightInput.setText(String.valueOf(userProfile.getHeight()));
            ageInput.setText(String.valueOf(userProfile.getAge()));

            if (userProfile.getGender().equals("Male")) {
                genderGroup.check(R.id.genderMale);
            } else {
                genderGroup.check(R.id.genderFemale);
            }

            String activity = userProfile.getActivity();
            switch (activity) {
                case "Sedentary": activityGroup.check(R.id.activitySedentary); break;
                case "Light": activityGroup.check(R.id.activityLight); break;
                case "Moderate": activityGroup.check(R.id.activityModerate); break;
                case "Active": activityGroup.check(R.id.activityActive); break;
                case "Very Active": activityGroup.check(R.id.activityVeryActive); break;
            }

            String goal = userProfile.getGoal();
            switch (goal) {
                case "Lose": goalGroup.check(R.id.goalLose); break;
                case "Maintain": goalGroup.check(R.id.goalMaintain); break;
                case "Gain": goalGroup.check(R.id.goalGain); break;
            }

            savedStatus.setText("Profile loaded");
            savedStatus.setTextColor(getColor(android.R.color.holo_green_light));
            calculateAndDisplay();
        } else {
            savedStatus.setText("No profile saved yet");
            savedStatus.setTextColor(getColor(android.R.color.holo_orange_light));
        }
    }

    private void calculateAndDisplay() {
        String weightStr = weightInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();

        if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            int age = Integer.parseInt(ageStr);

            int genderId = genderGroup.getCheckedRadioButtonId();
            if (genderId == -1) {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }
            String gender = (genderId == R.id.genderMale) ? "Male" : "Female";

            int activityId = activityGroup.getCheckedRadioButtonId();
            if (activityId == -1) {
                Toast.makeText(this, "Please select activity level", Toast.LENGTH_SHORT).show();
                return;
            }
            String activity = getActivityFromId(activityId);

            int goalId = goalGroup.getCheckedRadioButtonId();
            if (goalId == -1) {
                Toast.makeText(this, "Please select your goal", Toast.LENGTH_SHORT).show();
                return;
            }
            String goal = getGoalFromId(goalId);

            double bmr = (gender.equals("Male")) ?
                    10 * weight + 6.25 * height - 5 * age + 5 :
                    10 * weight + 6.25 * height - 5 * age - 161;

            double multiplier = getActivityMultiplier(activity);
            double tdee = bmr * multiplier;

            int budget;
            switch (goal) {
                case "Lose": budget = (int) Math.round(tdee - 500); break;
                case "Gain": budget = (int) Math.round(tdee + 500); break;
                default: budget = (int) Math.round(tdee);
            }

            bmrResult.setText("BMR: " + (int) Math.round(bmr) + " cal/day");
            budgetResult.setText("Daily Budget: " + budget + " cal/day");
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String weightStr = weightInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();

        if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int genderId = genderGroup.getCheckedRadioButtonId();
        if (genderId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        int activityId = activityGroup.getCheckedRadioButtonId();
        if (activityId == -1) {
            Toast.makeText(this, "Please select activity level", Toast.LENGTH_SHORT).show();
            return;
        }

        int goalId = goalGroup.getCheckedRadioButtonId();
        if (goalId == -1) {
            Toast.makeText(this, "Please select your goal", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            int age = Integer.parseInt(ageStr);

            String gender = (genderId == R.id.genderMale) ? "Male" : "Female";
            String activity = getActivityFromId(activityId);
            String goal = getGoalFromId(goalId);

            userProfile.saveProfile(weight, height, age, gender, activity, goal);
            savedStatus.setText("Profile saved!");
            savedStatus.setTextColor(getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private String getActivityFromId(int id) {
        if (id == R.id.activitySedentary) return "Sedentary";
        if (id == R.id.activityLight) return "Light";
        if (id == R.id.activityModerate) return "Moderate";
        if (id == R.id.activityActive) return "Active";
        if (id == R.id.activityVeryActive) return "Very Active";
        return "Moderate";
    }

    private String getGoalFromId(int id) {
        if (id == R.id.goalLose) return "Lose";
        if (id == R.id.goalMaintain) return "Maintain";
        if (id == R.id.goalGain) return "Gain";
        return "Maintain";
    }

    private double getActivityMultiplier(String activity) {
        switch (activity) {
            case "Sedentary": return 1.2;
            case "Light": return 1.375;
            case "Moderate": return 1.55;
            case "Active": return 1.725;
            case "Very Active": return 1.9;
            default: return 1.55;
        }
    }
}