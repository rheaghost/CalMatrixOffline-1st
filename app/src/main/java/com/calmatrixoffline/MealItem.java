package com.calmatrixoffline;

public class MealItem {
    public String foodName;
    public int calories;
    public int quantity;
    public String serving;

    public MealItem(String foodName, int calories, int quantity, String serving) {
        this.foodName = foodName;
        this.calories = calories;
        this.quantity = quantity;
        this.serving = serving;
    }
}