package com.calmatrixoffline;

public class MealCategory {
    public static final int BREAKFAST = 1;
    public static final int LUNCH = 2;
    public static final int DINNER = 3;
    public static final int SNACK = 4;

    public static String getCategoryName(int category) {
        switch (category) {
            case BREAKFAST: return "🍳 Breakfast";
            case LUNCH: return "🥗 Lunch";
            case DINNER: return "🍽️ Dinner";
            case SNACK: return "🍿 Snack";
            default: return "🍽️ Meal";
        }
    }
}
