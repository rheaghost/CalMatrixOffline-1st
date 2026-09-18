package com.calmatrixoffline;

public class LogEntry {
    private int id;
    private String foodName;
    private int foodCalories;
    private String exerciseName;
    private int exerciseCalories;
    private int netCalories;
    private String date;
    private String timestamp;

    private String mealCategory; // NEW  2609060528

    public LogEntry(int id, String foodName, int foodCalories, String exerciseName,
                    int exerciseCalories, int netCalories, String date, String timestamp) {
        this.id = id;
        this.foodName = foodName;
        this.foodCalories = foodCalories;
        this.exerciseName = exerciseName;
        this.exerciseCalories = exerciseCalories;
        this.netCalories = netCalories;
        this.date = date;
        this.timestamp = timestamp;
    }

    /*
    // 8-argument constructor (backward compatible)
    public LogEntry(int id, String foodName, int foodCalories, String exerciseName,
                    int exerciseCalories, int netCalories, String date, String timestamp) {
        this(id, foodName, foodCalories, exerciseName, exerciseCalories, netCalories, date, timestamp, "");
    }

     */

    // 9-argument constructor (with mealCategory)
    public LogEntry(int id, String foodName, int foodCalories, String exerciseName,
                    int exerciseCalories, int netCalories, String date, String timestamp,
                    String mealCategory) {
        this.id = id;
        this.foodName = foodName;
        this.foodCalories = foodCalories;
        this.exerciseName = exerciseName;
        this.exerciseCalories = exerciseCalories;
        this.netCalories = netCalories;
        this.date = date;
        this.timestamp = timestamp;
        this.mealCategory = mealCategory != null ? mealCategory : "";
    }



    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public int getFoodCalories() { return foodCalories; }
    public String getExerciseName() { return exerciseName; }
    public int getExerciseCalories() { return exerciseCalories; }
    public int getNetCalories() { return netCalories; }
    public String getDate() { return date; }
    public String getTimestamp() { return timestamp; }
    public String getMealCategory() { return mealCategory; }
}
