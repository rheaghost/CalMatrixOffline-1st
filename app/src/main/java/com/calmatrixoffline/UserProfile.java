package com.calmatrixoffline;

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfile {
    private static final String PREF_NAME = "CalSnapProfile";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_GOAL = "goal";

    private SharedPreferences prefs;

    public UserProfile(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveProfile(double weight, double height, int age, String gender, String activity, String goal) {
        prefs.edit()
                .putFloat(KEY_WEIGHT, (float) weight)
                .putFloat(KEY_HEIGHT, (float) height)
                .putInt(KEY_AGE, age)
                .putString(KEY_GENDER, gender)
                .putString(KEY_ACTIVITY, activity)
                .putString(KEY_GOAL, goal)
                .apply();
    }

    public double getWeight() { return prefs.getFloat(KEY_WEIGHT, 70f); }
    public double getHeight() { return prefs.getFloat(KEY_HEIGHT, 175f); }
    public int getAge() { return prefs.getInt(KEY_AGE, 30); }
    public String getGender() { return prefs.getString(KEY_GENDER, "Male"); }
    public String getActivity() { return prefs.getString(KEY_ACTIVITY, "Moderate"); }
    public String getGoal() { return prefs.getString(KEY_GOAL, "Maintain"); }
    public boolean hasProfile() { return prefs.contains(KEY_WEIGHT) && prefs.contains(KEY_HEIGHT); }

    public int getDailyBudget() {
        if (!hasProfile()) return 2000;
        double bmr = (getGender().equals("Male")) ?
                10 * getWeight() + 6.25 * getHeight() - 5 * getAge() + 5 :
                10 * getWeight() + 6.25 * getHeight() - 5 * getAge() - 161;
        double multiplier = 1.55;
        switch (getActivity()) {
            case "Sedentary": multiplier = 1.2; break;
            case "Light": multiplier = 1.375; break;
            case "Moderate": multiplier = 1.55; break;
            case "Active": multiplier = 1.725; break;
            case "Very Active": multiplier = 1.9; break;
        }
        double tdee = bmr * multiplier;
        switch (getGoal()) {
            case "Lose": return (int) Math.round(tdee - 500);
            case "Gain": return (int) Math.round(tdee + 500);
            default: return (int) Math.round(tdee);
        }
    }

    public void clearProfile() { prefs.edit().clear().apply(); }
}