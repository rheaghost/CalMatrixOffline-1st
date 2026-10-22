package com.calmatrixoffline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CalSnap.db";
    //private static final int DATABASE_VERSION = 2;
    //private static final int DATABASE_VERSION = 3;  // ← CHANGE FROM 2 TO 3
    //private static final int DATABASE_VERSION = 4;  // ← CHANGE FROM 2 TO 3
    //private static final int DATABASE_VERSION = 5;//correction verson 26--825
    //private static final int DATABASE_VERSION = 6; // or whatever your current version is + 1
    //private static final int DATABASE_VERSION = 7; // or whatever your current version is + 1 because of meal macros table conflict 2609060851
    //private static final int DATABASE_VERSION = 8; //food image columns added 2609110202
    //private static final int DATABASE_VERSION = 9;//add servings column for recipe 2609151649
    //private static final int DATABASE_VERSION = 10;//export user made content to csv 2609151959
    private static final int DATABASE_VERSION = 11;//for proper handling of user supplied food data 2610111130

    private static final String COLUMN_MACRO_SERVINGS = "servings"; //1609151649



    private static final String COLUMN_MEAL_CATEGORY = "meal_category";//260905

    // Meal macros table
    private static final String TABLE_MEAL_MACROS = "meal_macros";
    private static final String COLUMN_MACRO_ID = "id";
    private static final String COLUMN_MACRO_NAME = "name";
    private static final String COLUMN_MACRO_ITEMS = "items";

    // Logs table
    private static final String TABLE_LOGS = "logs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FOOD_NAME = "food_name";
    private static final String COLUMN_FOOD_CALORIES = "food_calories";

    private static final String COLUMN_FOOD_IS_USER = "is_user_added";//2609152115

    private static final String COLUMN_FOOD_IS_EDITABLE = "is_editable";//2610111132
    private static final String COLUMN_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_EXERCISE_CALORIES = "exercise_calories";
    private static final String COLUMN_NET_CALORIES = "net_calories";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Weight table
    private static final String TABLE_WEIGHT = "weight_logs";
    private static final String COLUMN_WEIGHT_ID = "id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_WEIGHT_DATE = "date";
    private static final String COLUMN_WEIGHT_TIMESTAMP = "timestamp";

    // Water table
    private static final String TABLE_WATER = "water_logs";
    private static final String COLUMN_WATER_ID = "id";
    private static final String COLUMN_WATER_AMOUNT = "amount";
    private static final String COLUMN_WATER_DATE = "date";
    private static final String COLUMN_WATER_TIMESTAMP = "timestamp";

    //exercies

    // Add to constants at top
    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_EXERCISE_ID = "id";
    //private static final String COLUMN_EXERCISE_NAME = "name";
    private static final String COLUMN_EXERCISE_MET = "met";

    //cotr related 26082518
    // Add to constants at the top
    private static final String TABLE_CORRECTION_IMAGES = "correction_images";
    private static final String COL_CORRECTION_IMAGE_ID = "id";
    private static final String COL_CORRECTION_IMAGE_MODEL = "model_output";
    private static final String COL_CORRECTION_IMAGE_USER = "user_name";
    private static final String COL_CORRECTION_IMAGE_PATH = "image_path";
    private static final String COL_CORRECTION_IMAGE_DATE = "date";

    //private static final String COLUMN_FOOD_IS_USER = "is_user_added"; //2609152007


    //unified
    private static final String TABLE_FOODS = "foods";
    private static final String COLUMN_FOOD_ID = "id";
    //private static final String COLUMN_FOOD_NAME = "name";
   // private static final String COLUMN_FOOD_CALORIES = "calories";
    private static final String COLUMN_FOOD_PROTEIN = "protein";
    private static final String COLUMN_FOOD_CARBS = "carbs";
    private static final String COLUMN_FOOD_FAT = "fat";
    private static final String COLUMN_FOOD_SERVING = "serving";
    private static final String COLUMN_FOOD_BARCODE = "barcode";

    //correctuons related
    // Add to constants at the top
    private static final String TABLE_CORRECTIONS = "corrections";
    private static final String COL_CORRECTION_MODEL = "model_output";
    private static final String COL_CORRECTION_USER = "user_name";

    //food and exercise image entries 2609110148
    private static final String COLUMN_FOOD_IMAGE = "image_path";
    private static final String COLUMN_EXERCISE_IMAGE = "image_path";






    //end corrections related 26082511
    private Context context; //26082519
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; //26082519 //2609060727
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Logs table
        
        String createLogsTable = "CREATE TABLE " + TABLE_LOGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_FOOD_CALORIES + " INTEGER,"
                + COLUMN_EXERCISE_NAME + " TEXT,"
                + COLUMN_EXERCISE_CALORIES + " INTEGER,"
                + COLUMN_NET_CALORIES + " INTEGER,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_MEAL_CATEGORY + " TEXT"
                + ")";
        db.execSQL(createLogsTable);

        // Weight table
        String createWeightTable = "CREATE TABLE " + TABLE_WEIGHT + "("
                + COLUMN_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_WEIGHT_DATE + " TEXT,"
                + COLUMN_WEIGHT_TIMESTAMP + " TEXT"
                + ")";
        db.execSQL(createWeightTable);

        // Water table
        String createWaterTable = "CREATE TABLE " + TABLE_WATER + "("
                + COLUMN_WATER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WATER_AMOUNT + " INTEGER,"
                + COLUMN_WATER_DATE + " TEXT,"
                + COLUMN_WATER_TIMESTAMP + " TEXT"
                + ")";
        db.execSQL(createWaterTable);
        /*
        // Add to onCreate()
        String createFoodsTable = "CREATE TABLE " + TABLE_FOODS + "("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_FOOD_CALORIES + " INTEGER,"
                + COLUMN_FOOD_PROTEIN + " INTEGER,"
                + COLUMN_FOOD_CARBS + " INTEGER,"
                + COLUMN_FOOD_FAT + " INTEGER,"
                + COLUMN_FOOD_SERVING + " TEXT,"
                + COLUMN_FOOD_BARCODE + " TEXT,"
                + COLUMN_FOOD_IMAGE + " TEXT"
                + ")";
        db.execSQL(createFoodsTable);


         */
        String createFoodsTable = "CREATE TABLE " + TABLE_FOODS + "("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_FOOD_CALORIES + " INTEGER,"
                + COLUMN_FOOD_PROTEIN + " INTEGER,"
                + COLUMN_FOOD_CARBS + " INTEGER,"
                + COLUMN_FOOD_FAT + " INTEGER,"
                + COLUMN_FOOD_SERVING + " TEXT,"
                + COLUMN_FOOD_BARCODE + " TEXT,"
                + COLUMN_FOOD_IMAGE + " TEXT,"
                + COLUMN_FOOD_IS_USER + " INTEGER DEFAULT 0,"
                + COLUMN_FOOD_IS_EDITABLE + " INTEGER DEFAULT 0"
                + ")";

        db.execSQL(createFoodsTable);

        // Add to onCreate()
        String createExercisesTable = "CREATE TABLE " + TABLE_EXERCISES + "("
                + COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EXERCISE_NAME + " TEXT,"
                + COLUMN_EXERCISE_MET + " REAL"
                + ")";
        db.execSQL(createExercisesTable);

        //correct table
        String createCorrectionsTable = "CREATE TABLE " + TABLE_CORRECTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CORRECTION_MODEL + " TEXT,"
                + COL_CORRECTION_USER + " TEXT"
                + ")";
        db.execSQL(createCorrectionsTable);

        // Correction images table
        String createCorrectionImagesTable = "CREATE TABLE " + TABLE_CORRECTION_IMAGES + "("
                + COL_CORRECTION_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CORRECTION_IMAGE_MODEL + " TEXT,"
                + COL_CORRECTION_IMAGE_USER + " TEXT,"
                + COL_CORRECTION_IMAGE_PATH + " TEXT,"
                + COL_CORRECTION_IMAGE_DATE + " TEXT"
                + ")";
        db.execSQL(createCorrectionImagesTable);
        //2609060754


        //end correct table 26082511
        //meals table
        String createMealMacrosTable = "CREATE TABLE " + TABLE_MEAL_MACROS + "("
                + COLUMN_MACRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MACRO_NAME + " TEXT,"
                + COLUMN_MACRO_ITEMS + " TEXT"
                + ")";
        db.execSQL(createMealMacrosTable);
        //end meals table 2609051527

        //recipe table 2609151652
        /*
        String createFoodsTable = "CREATE TABLE " + TABLE_FOODS + "("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_FOOD_CALORIES + " INTEGER,"
                + COLUMN_FOOD_PROTEIN + " INTEGER,"
                + COLUMN_FOOD_CARBS + " INTEGER,"
                + COLUMN_FOOD_FAT + " INTEGER,"
                + COLUMN_FOOD_SERVING + " TEXT,"
                + COLUMN_FOOD_BARCODE + " TEXT,"
                + COLUMN_FOOD_IMAGE + " TEXT,"
                + COLUMN_FOOD_IS_USER + " INTEGER DEFAULT 0"   // NEW
                + ")";
        db.execSQL(createFoodsTable);



         */



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String createWaterTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WATER + "("
                    + COLUMN_WATER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_WATER_AMOUNT + " INTEGER,"
                    + COLUMN_WATER_DATE + " TEXT,"
                    + COLUMN_WATER_TIMESTAMP + " TEXT"
                    + ")";
            db.execSQL(createWaterTable);//ook 2609060756
        }

        // Add to onUpgrade()
        if (oldVersion < 3) {
            String createFoodsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_FOODS + "("
                    + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FOOD_NAME + " TEXT,"
                    + COLUMN_FOOD_CALORIES + " INTEGER,"
                    + COLUMN_FOOD_PROTEIN + " INTEGER,"
                    + COLUMN_FOOD_CARBS + " INTEGER,"
                    + COLUMN_FOOD_FAT + " INTEGER,"
                    + COLUMN_FOOD_SERVING + " TEXT,"
                    + COLUMN_FOOD_BARCODE + " TEXT"
                    + ")";
            db.execSQL(createFoodsTable);
        }
        //add food image folder path 2609110203

        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_FOODS + " ADD COLUMN " + COLUMN_FOOD_IMAGE + " TEXT");
            } catch (Exception e) { }
        }
        if (oldVersion < 9) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_MEAL_MACROS +
                        " ADD COLUMN " + COLUMN_MACRO_SERVINGS + " INTEGER DEFAULT 1");
            } catch (Exception e) {
                // Column may already exist — ignore
            }
        }

        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_FOODS +
                        " ADD COLUMN " + COLUMN_FOOD_IS_USER + " INTEGER DEFAULT 0");
            } catch (Exception e) { }
        }

        if (oldVersion < 11) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_FOODS +
                        " ADD COLUMN " + COLUMN_FOOD_IS_EDITABLE + " INTEGER DEFAULT 0");
            } catch (Exception e) { }
        }//2610111141 proper handling of user supplied data




        // Foods table (for imported foods + barcodes)
        String createFoodsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_FOODS + "("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_FOOD_CALORIES + " INTEGER,"
                + COLUMN_FOOD_PROTEIN + " INTEGER,"
                + COLUMN_FOOD_CARBS + " INTEGER,"
                + COLUMN_FOOD_FAT + " INTEGER,"
                + COLUMN_FOOD_SERVING + " TEXT,"
                + COLUMN_FOOD_BARCODE + " TEXT"
                + ")";
        db.execSQL(createFoodsTable);
        // Add to onUpgrade()
        if (oldVersion < 4) {
            String createExercisesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXERCISES + "("
                    + COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EXERCISE_NAME + " TEXT,"
                    + COLUMN_EXERCISE_MET + " REAL"
                    + ")";
            db.execSQL(createExercisesTable);
        }
        //corrections
        if (oldVersion < 5) {
            String createCorrectionsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CORRECTIONS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_CORRECTION_MODEL + " TEXT,"
                    + COL_CORRECTION_USER + " TEXT"
                    + ")";
            db.execSQL(createCorrectionsTable);
        }
        //end corrections

        //meals
        if (oldVersion < 7) {  //meal_macros table was not created so change 5 to 6 and see what happens 2609060844
            String createMealMacrosTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MEAL_MACROS + "("
                    + COLUMN_MACRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MACRO_NAME + " TEXT,"
                    + COLUMN_MACRO_ITEMS + " TEXT"
                    + ")";
            db.execSQL(createMealMacrosTable);
        }
        //end meals 2609051528
        if (oldVersion < 6) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_LOGS + " ADD COLUMN " + COLUMN_MEAL_CATEGORY + " TEXT");
            } catch (Exception e) {
                // Column may already exist
            }
        }

    }

    // ============================================================
    //  LOGS METHODS
    // ============================================================

    public long insertLog_oldok(String foodName, int foodCalories, String exerciseName,
                          int exerciseCalories, int netCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_FOOD_CALORIES, foodCalories);
        values.put(COLUMN_EXERCISE_NAME, exerciseName);
        values.put(COLUMN_EXERCISE_CALORIES, exerciseCalories);
        values.put(COLUMN_NET_CALORIES, netCalories);
        values.put(COLUMN_DATE, currentDate);
        values.put(COLUMN_TIMESTAMP, currentTime);

        return db.insert(TABLE_LOGS, null, values);
    }//5 args



    public long insertLog(String foodName, int foodCalories, String exerciseName,
                          int exerciseCalories, int netCalories, String mealCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_FOOD_CALORIES, foodCalories);
        values.put(COLUMN_EXERCISE_NAME, exerciseName);
        values.put(COLUMN_EXERCISE_CALORIES, exerciseCalories);
        values.put(COLUMN_NET_CALORIES, netCalories);
        values.put(COLUMN_DATE, currentDate);
        values.put(COLUMN_TIMESTAMP, currentTime);
        values.put(COLUMN_MEAL_CATEGORY, mealCategory != null ? mealCategory : ""); // optional

        return db.insert(TABLE_LOGS, null, values);
    }// 6 args

    public long insertLog(String foodName, int foodCalories, String exerciseName,
                          int exerciseCalories, int netCalories) {
        return insertLog(foodName, foodCalories, exerciseName, exerciseCalories, netCalories, "");
    }//5 args


    public List<LogEntry> getTodayEntries_Old() {
        List<LogEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGS + " WHERE " + COLUMN_DATE + " = ? ORDER BY " + COLUMN_ID + " DESC", new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                entries.add(new LogEntry(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CALORIES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NET_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }//getTodaysEntries_old

    public List<LogEntry> getTodayEntries() {
        List<LogEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGS + " WHERE " + COLUMN_DATE + " = ? ORDER BY " + COLUMN_ID + " DESC", new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME));
                int foodCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES));
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME));
                int exerciseCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CALORIES));
                int netCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NET_CALORIES));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                // Read meal_category (if column exists, else default to "")
                String mealCategory = "";
                try {
                    int idx = cursor.getColumnIndexOrThrow(COLUMN_MEAL_CATEGORY);
                    mealCategory = cursor.getString(idx);
                } catch (IllegalArgumentException e) {
                    // Column might not exist in older versions
                    mealCategory = "";
                }
                entries.add(new LogEntry(id, foodName, foodCal, exerciseName, exerciseCal, netCal, date, timestamp, mealCategory));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }//getTodaysEntries new 2609060549
    //ok 2609060816




    public List<LogEntry> getAllEntries_old() {
        List<LogEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGS + " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_ID + " DESC LIMIT 100", null);

        if (cursor.moveToFirst()) {
            do {
                entries.add(new LogEntry(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CALORIES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NET_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public List<LogEntry> getAllEntries() {
        List<LogEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGS + " WHERE " + COLUMN_DATE + " = ? ORDER BY " + COLUMN_ID + " DESC", new String[]{currentDate});
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGS + " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_ID + " DESC LIMIT 100", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME));
                int foodCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES));
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME));
                int exerciseCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CALORIES));
                int netCal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NET_CALORIES));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                // Read meal_category (if column exists, else default to "")
                String mealCategory = "";
                try {
                    int idx = cursor.getColumnIndexOrThrow(COLUMN_MEAL_CATEGORY);
                    mealCategory = cursor.getString(idx);
                } catch (IllegalArgumentException e) {
                    // Column might not exist in older versions
                    mealCategory = "";
                }
                entries.add(new LogEntry(id, foodName, foodCal, exerciseName, exerciseCal, netCal, date, timestamp, mealCategory));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }//getAllEntries new 2609060602

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});//2609060823  060825
    }

    public int getTodayTotalNetCalories() {
        List<LogEntry> entries = getTodayEntries();
        int total = 0;
        for (LogEntry entry : entries) {
            total += entry.getNetCalories();
        }
        return total;
    }

    public int getTodayTotalFoodCalories() {
        List<LogEntry> entries = getTodayEntries();
        int total = 0;
        for (LogEntry entry : entries) {
            total += entry.getFoodCalories();
        }
        return total;
    }

    public int getTodayTotalExerciseCalories() {
        List<LogEntry> entries = getTodayEntries();
        int total = 0;
        for (LogEntry entry : entries) {
            total += entry.getExerciseCalories();
        }
        return total;
    }

    public int getTodayEntryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LOGS + " WHERE " + COLUMN_DATE + " = ?", new String[]{currentDate});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getDayTotalNetCalories(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_NET_CALORIES + ") FROM " + TABLE_LOGS + " WHERE " + COLUMN_DATE + " = ?", new String[]{date});
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public int getStreak() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_DATE + " FROM " + TABLE_LOGS + " ORDER BY " + COLUMN_DATE + " DESC", null);
        int streak = 0;
        if (cursor.moveToFirst()) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String currentDate = today;
            int index = 0;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            while (true) {
                if (index >= cursor.getCount()) break;
                cursor.moveToPosition(index);
                String date = cursor.getString(0);
                if (date.equals(currentDate)) {
                    streak++;
                    try {
                        cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentDate));
                        cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
                        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
                        index++;
                    } catch (Exception e) { break; }
                } else { break; }
            }
        }
        cursor.close();
        return streak;
    }

    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Time,Food,Food Calories,Exercise,Exercise Calories,Net Calories\n");
        for (LogEntry entry : getAllEntries()) {
            csv.append(entry.getDate()).append(",")
                    .append(entry.getTimestamp()).append(",")
                    .append(entry.getFoodName()).append(",")
                    .append(entry.getFoodCalories()).append(",")
                    .append(entry.getExerciseName()).append(",")
                    .append(entry.getExerciseCalories()).append(",")
                    .append(entry.getNetCalories()).append("\n");
        }
        return csv.toString();




    }

    // ============================================================
    //  WEIGHT METHODS
    // ============================================================

    public long insertWeight(double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_WEIGHT_DATE, currentDate);
        values.put(COLUMN_WEIGHT_TIMESTAMP, currentTime);

        return db.insert(TABLE_WEIGHT, null, values);
    }

    public List<WeightEntry> getWeightHistory() {
        List<WeightEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WEIGHT + " ORDER BY " + COLUMN_WEIGHT_DATE + " DESC, " + COLUMN_WEIGHT_ID + " DESC LIMIT 30", null);

        if (cursor.moveToFirst()) {
            do {
                entries.add(new WeightEntry(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_TIMESTAMP))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public double getLatestWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_WEIGHT + " ORDER BY " + COLUMN_WEIGHT_DATE + " DESC, " + COLUMN_WEIGHT_ID + " DESC LIMIT 1", null);
        double weight = 0;
        if (cursor.moveToFirst()) {
            weight = cursor.getDouble(0);
        }
        cursor.close();
        return weight;
    }

    public double getWeightChange() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_WEIGHT + " ORDER BY " + COLUMN_WEIGHT_DATE + " DESC, " + COLUMN_WEIGHT_ID + " DESC LIMIT 2", null);
        double latest = 0;
        double previous = 0;
        int count = 0;
        if (cursor.moveToFirst()) {
            latest = cursor.getDouble(0);
            count++;
            if (cursor.moveToNext()) {
                previous = cursor.getDouble(0);
                count++;
            }
        }
        cursor.close();
        return count >= 2 ? latest - previous : 0;
    }

    public void deleteWeightEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHT, COLUMN_WEIGHT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ============================================================
    //  WATER METHODS
    // ============================================================

    public long insertWater(int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COLUMN_WATER_AMOUNT, amount);
        values.put(COLUMN_WATER_DATE, currentDate);
        values.put(COLUMN_WATER_TIMESTAMP, currentTime);
        return db.insert(TABLE_WATER, null, values);
    }

    public int getTodayWaterTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_WATER_AMOUNT + ") FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + " = ?", new String[]{currentDate});
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public void insertUserFood(String name, int calories, int protein, int carbs,
                               int fat, String serving, String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_CALORIES, calories);
        values.put(COLUMN_FOOD_PROTEIN, protein);
        values.put(COLUMN_FOOD_CARBS, carbs);
        values.put(COLUMN_FOOD_FAT, fat);
        values.put(COLUMN_FOOD_SERVING, serving);
        values.put(COLUMN_FOOD_BARCODE, barcode != null ? barcode : "");
        values.put(COLUMN_FOOD_IS_USER, 1);
        values.put(COLUMN_FOOD_IS_EDITABLE, 1);  //2610111660
        db.insert(TABLE_FOODS, null, values);
    }//2609152123

    public String exportUserFoodsToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("name,calories,protein,carbs,fat,serving,barcode\n");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_FOOD_NAME + ", "
                        + COLUMN_FOOD_CALORIES + ", "
                        + COLUMN_FOOD_PROTEIN + ", "
                        + COLUMN_FOOD_CARBS + ", "
                        + COLUMN_FOOD_FAT + ", "
                        + COLUMN_FOOD_SERVING + ", "
                        + COLUMN_FOOD_BARCODE +
                        " FROM " + TABLE_FOODS +
                        " WHERE " + COLUMN_FOOD_IS_USER + " = 1" +
                        " ORDER BY " + COLUMN_FOOD_NAME,
                null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            csv.append("\"").append(name.replace("\"", "\"\"")).append("\",")
                    .append(cursor.getInt(1)).append(",")
                    .append(cursor.getInt(2)).append(",")
                    .append(cursor.getInt(3)).append(",")
                    .append(cursor.getInt(4)).append(",")
                    .append("\"").append(cursor.getString(5)).append("\",")
                    .append(cursor.getString(6)).append("\n");
        }
        cursor.close();
        return csv.toString();
    }//2609152130




    /*
    public int getTodayWaterTotal_Old1() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_WATER_AMOUNT + ") FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + " = ?", new String[]{currentDate});
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }

     */

    public int getDayWaterTotal(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_WATER_AMOUNT + ") FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + " = ?", new String[]{date});
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }


    public void resetTodayWater() {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.delete(TABLE_WATER, COLUMN_WATER_DATE + " = ?", new String[]{currentDate});
    }



    //graph related 260909
    public int getDayWaterTotal_Old2(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_WATER_AMOUNT + ") FROM " + TABLE_WATER + " WHERE " + COLUMN_WATER_DATE + " = ?", new String[]{date});
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }


    // ===== FOOD DATABASE METHODS =====

    public void insertFood(String name, int calories, int protein, int carbs, int fat, String serving, String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_CALORIES, calories);
        values.put(COLUMN_FOOD_PROTEIN, protein);
        values.put(COLUMN_FOOD_CARBS, carbs);
        values.put(COLUMN_FOOD_FAT, fat);
        values.put(COLUMN_FOOD_SERVING, serving);
        values.put(COLUMN_FOOD_BARCODE, barcode);
        db.insert(TABLE_FOODS, null, values);
    }


    public FoodItem searchFood(String query) {//cprrect 8 args 2610112021
        SQLiteDatabase db = this.getReadableDatabase();
        String q = query.toLowerCase().trim();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS + " WHERE LOWER(" + COLUMN_FOOD_NAME + ") LIKE ?", new String[]{"%" + q + "%"});
        FoodItem result = null;
        if (cursor.moveToFirst()) {
            int userIdx = cursor.getColumnIndex(COLUMN_FOOD_IS_USER);
            int editIdx = cursor.getColumnIndex(COLUMN_FOOD_IS_EDITABLE);
            boolean isUser     = userIdx >= 0 && cursor.getInt(userIdx) == 1;
            boolean isEditable = editIdx >= 0 && cursor.getInt(editIdx) == 1;

            result = new FoodItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PROTEIN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CARBS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_FAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_SERVING)),
                    isUser,
                    isEditable
            );
        }
        cursor.close();
        return result;
    }
    public FoodItem searchFood_fail(String query) {//incorrect 6 arg constructor 2610112021
        SQLiteDatabase db = this.getReadableDatabase();
        String q = query.toLowerCase().trim();

        // Search by name
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS + " WHERE LOWER(" + COLUMN_FOOD_NAME + ") LIKE ?", new String[]{"%" + q + "%"});
        FoodItem result = null;
        if (cursor.moveToFirst()) {
            result = new FoodItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PROTEIN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CARBS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_FAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_SERVING))
            );
        }
        cursor.close();
        return result;
    }//end searchFood 2610111512

    //replace 2610111512
    public boolean updateUserFood(String originalName, String newName, int calories,
                                  int protein, int carbs, int fat, String serving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, newName);
        values.put(COLUMN_FOOD_CALORIES, calories);
        values.put(COLUMN_FOOD_PROTEIN, protein);
        values.put(COLUMN_FOOD_CARBS, carbs);
        values.put(COLUMN_FOOD_FAT, fat);
        values.put(COLUMN_FOOD_SERVING, serving);
        int rows = db.update(TABLE_FOODS, values,
                COLUMN_FOOD_NAME + " = ?", new String[]{originalName});
        return rows > 0;
    }//end updateuserfood 2610111512

    public FoodItem searchFoodByBarcode_old_6arg(String barcode) {//isEditable = false for foods found by barcode — so no Edit button appears on them. 2610121446
        SQLiteDatabase db = this.getReadableDatabase();
        if (barcode == null || barcode.isEmpty()) return null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS + " WHERE " + COLUMN_FOOD_BARCODE + " = ?", new String[]{barcode});
        FoodItem result = null;
        if (cursor.moveToFirst()) {
            result = new FoodItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PROTEIN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CARBS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_FAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_SERVING))
            );
        }
        cursor.close();
        return result;
    }

    //2610121447 8args version
    public FoodItem searchFoodByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (barcode == null || barcode.isEmpty()) return null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS + " WHERE " + COLUMN_FOOD_BARCODE + " = ?", new String[]{barcode});
        FoodItem result = null;
        if (cursor.moveToFirst()) {
            int userIdx = cursor.getColumnIndex(COLUMN_FOOD_IS_USER);
            int editIdx = cursor.getColumnIndex(COLUMN_FOOD_IS_EDITABLE);
            boolean isUser     = userIdx >= 0 && cursor.getInt(userIdx) == 1;
            boolean isEditable = editIdx >= 0 && cursor.getInt(editIdx) == 1;

            result = new FoodItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CALORIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PROTEIN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CARBS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_FAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_SERVING)),
                    isUser,
                    isEditable
            );
        }
        cursor.close();
        return result;
    }



    public int getFoodCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FOODS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void clearAllFoods() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOODS, null, null);
    }

    // FoodItem class (already exists in FoodDatabase, but we need this for compatibility)
    //new 26100111509
    public static class FoodItem {
        public String name;
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public String serving;
        public boolean isUserAdded;
        public boolean isEditable;

        // Existing 6-arg constructor — kept so nothing else breaks
        public FoodItem(String name, int calories, int protein, int carbs, int fat, String serving) {
            this(name, calories, protein, carbs, fat, serving, false, false);
        }

        // New 8-arg constructor
        public FoodItem(String name, int calories, int protein, int carbs, int fat, String serving,
                        boolean isUserAdded, boolean isEditable) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.serving = serving;
            this.isUserAdded = isUserAdded;
            this.isEditable = isEditable;
        }
    }//end class FoodItem

    /*
    public static class FoodItem_old {//replaced 2610111509
        public String name;
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public String serving;

        public FoodItem(String name, int calories, int protein, int carbs, int fat, String serving) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.serving = serving;
        }
    }*/



    // Add these methods
    public void insertExercise(String name, double met) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_NAME, name);
        values.put(COLUMN_EXERCISE_MET, met);
        db.insert(TABLE_EXERCISES, null, values);
    }

    public ExerciseItem searchExercise(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = query.toLowerCase().trim();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXERCISES + " WHERE LOWER(" + COLUMN_EXERCISE_NAME + ") LIKE ? LIMIT 1", new String[]{"%" + q + "%"});
        ExerciseItem result = null;
        if (cursor.moveToFirst()) {
            result = new ExerciseItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_MET))
            );
        }
        cursor.close();
        return result;
    }

    public int getExerciseCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXERCISES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    //correct relstrd
    public String getCorrection(String modelOutput) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_CORRECTION_USER + " FROM " + TABLE_CORRECTIONS +
                " WHERE " + COL_CORRECTION_MODEL + " = ?", new String[]{modelOutput});
        String result = null;
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    public void saveCorrection(String modelOutput, String userFood) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete any existing correction for this model output
        db.delete(TABLE_CORRECTIONS, COL_CORRECTION_MODEL + " = ?", new String[]{modelOutput});
        // Insert new correction
        ContentValues values = new ContentValues();
        values.put(COL_CORRECTION_MODEL, modelOutput);
        values.put(COL_CORRECTION_USER, userFood);
        db.insert(TABLE_CORRECTIONS, null, values);
    }
    public List<String> getAllCorrections() {
        List<String> corrections = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Use valid SQL syntax — correct table name, no "->" in SQL
        Cursor cursor = db.rawQuery("SELECT " + COL_CORRECTION_MODEL + ", " + COL_CORRECTION_USER +
                " FROM " + TABLE_CORRECTIONS, null);

        while (cursor.moveToNext()) {
            String model = cursor.getString(0);
            String user = cursor.getString(1);
            corrections.add(model + " -> " + user);  // Format in Java, not SQL
        }
        cursor.close();
        return corrections;
    }

    /*
    public List<String> getAllCorrections() {
        List<String> corrections = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_CORRECTION_MODEL + " -> " + COL_CORRECTION_USER + " FROM " + TABLE_CORRECTIONS, null);
        while (cursor.moveToNext()) {
            corrections.add(cursor.getString(0) + " -> " + cursor.getString(1));
        }
        cursor.close();
        return corrections;
    }

     */


    public void saveCorrectionWithImage(String modelOutput, String userFood, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Save the correction in the main corrections table
        saveCorrection(modelOutput, userFood);

        // Save the image if provided
        if (image != null && context!=null) {
            String imagePath = saveImageToInternalStorage(image);
            if (imagePath != null) {
                ContentValues values = new ContentValues();
                values.put(COL_CORRECTION_IMAGE_MODEL, modelOutput);
                values.put(COL_CORRECTION_IMAGE_USER, userFood);
                values.put(COL_CORRECTION_IMAGE_PATH, imagePath);
                values.put(COL_CORRECTION_IMAGE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                db.insert(TABLE_CORRECTION_IMAGES, null, values);
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap image) {
        try {
            String filename = "correction_" + System.currentTimeMillis() + ".jpg";
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CorrectionImage> getCorrectionImages() {
        List<CorrectionImage> images = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CORRECTION_IMAGES + " ORDER BY " + COL_CORRECTION_IMAGE_DATE + " DESC", null);
        while (cursor.moveToNext()) {
            CorrectionImage img = new CorrectionImage();
            img.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CORRECTION_IMAGE_ID));
            img.modelOutput = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECTION_IMAGE_MODEL));
            img.userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECTION_IMAGE_USER));
            img.imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECTION_IMAGE_PATH));
            img.date = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECTION_IMAGE_DATE));
            images.add(img);
        }
        cursor.close();
        return images;
    }

    //for autocomplete 26082520

    public List<String> getAllFoodNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FOOD_NAME + " FROM " + TABLE_FOODS + " ORDER BY " + COLUMN_FOOD_NAME, null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        return names;
    }
    //26082520
    //26082520 get exercise names for autocomplete
    public List<String> getAllExerciseNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_EXERCISE_NAME + " FROM " + TABLE_EXERCISES + " ORDER BY " + COLUMN_EXERCISE_NAME, null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        return names;
    }
    //end exercise names 26082520


    // Simple model class for correction images
    public static class CorrectionImage {
        public int id;
        public String modelOutput;
        public String userName;
        public String imagePath;
        public String date;
    }


    //end correct related 260825

    // ExerciseItem class
    public static class ExerciseItem {
        public String name;
        public double met;

        public ExerciseItem(String name, double met) {
            this.name = name;
            this.met = met;
        }

        public int calculateCalories(double weightKg, int durationMinutes) {
            double calories = met * 3.5 * (weightKg / 200.0) * durationMinutes;
            return (int) Math.round(calories);
        }
    }

    //meal related
    public void saveMealMacro(MealMacro macro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MACRO_NAME, macro.name);

        // Convert items to JSON
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        try {
            for (MealItem item : macro.items) {
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("name", item.foodName);
                obj.put("calories", item.calories);
                obj.put("quantity", item.quantity);
                obj.put("serving", item.serving);
                jsonArray.put(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.put(COLUMN_MACRO_ITEMS, jsonArray.toString());
        values.put(COLUMN_MACRO_SERVINGS, macro.servings);//was missing 2609151729
        android.util.Log.d("MealSave", "Saving " + macro.name + " with servings=" + macro.servings);



        db.insert(TABLE_MEAL_MACROS, null, values);
    }

    public List<MealMacro> getAllMealMacros() {
        List<MealMacro> macros = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEAL_MACROS, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MACRO_NAME));
            String itemsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MACRO_ITEMS));
            List<MealItem> items = new ArrayList<>();
            try {
                org.json.JSONArray jsonArray = new org.json.JSONArray(itemsJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject obj = jsonArray.getJSONObject(i);
                    items.add(new MealItem(
                            obj.getString("name"),
                            obj.getInt("calories"),
                            obj.getInt("quantity"),
                            obj.getString("serving")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //macros.add(new MealMacro(name, items));//2610091922 bug servings is saved to the DB but not loaded back. So any recipe with servings > 1 shows as "× 1" when you reopen the app
            MealMacro macro = new MealMacro(name, items);
            macro.servings = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MACRO_SERVINGS));
            macros.add(macro);//fepair 2610091923

        }
        cursor.close();
        return macros;
    }

    public void deleteMealMacro(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEAL_MACROS, COLUMN_MACRO_NAME + " = ?", new String[]{name});
    }
    //end meal related 2609051532

    public void updateFoodImagePath_old1(String foodName, String imageFilename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_IMAGE, imageFilename);
        db.update(TABLE_FOODS, values,
                "LOWER(" + COLUMN_FOOD_NAME + ") = ?",
                new String[]{foodName.toLowerCase()});
    }
    //add logging
    public void updateFoodImagePath(String foodName, String imageFilename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_IMAGE, imageFilename);
        int rows = db.update(TABLE_FOODS, values,
                "LOWER(" + COLUMN_FOOD_NAME + ") = ?",
                new String[]{foodName.toLowerCase()});
        android.util.Log.d("Preload", "  → Updated " + rows + " rows for '" + foodName + "'");
    }

    public String getFoodImagePath(String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_FOOD_IMAGE + " FROM " + TABLE_FOODS +
                        " WHERE LOWER(" + COLUMN_FOOD_NAME + ") = ?",
                new String[]{foodName.toLowerCase()});
        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(0);
        }
        cursor.close();
        return path;
    }//2609110633

    public void fixExistingUserFoodsEditable() {//2610111611 make the old ones like gahanchocolate editable
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_FOODS + " SET " + COLUMN_FOOD_IS_EDITABLE + " = 1 WHERE " + COLUMN_FOOD_IS_USER + " = 1");
    }



}
