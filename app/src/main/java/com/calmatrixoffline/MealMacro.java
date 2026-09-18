package com.calmatrixoffline;

import java.util.List;

/*
public class MealMacro {

    public String name;
    public List<MealItem> items;
    public int totalCalories;

    public MealMacro(String name, List<MealItem> items) {
        this.name = name;
        this.items = items;
        this.totalCalories = 0;
        for (MealItem item : items) {
            this.totalCalories += item.calories * item.quantity;
        }
    }


}

 */


public class MealMacro {
    public String name;
    public List<MealItem> items;
    public int totalCalories;
    public int servings = 1;   // NEW

    public MealMacro(String name, List<MealItem> items) {
        this.name = name;
        this.items = items;
        this.totalCalories = 0;
        for (MealItem item : items) {
            this.totalCalories += item.calories * item.quantity;
        }
    }

    public int getCaloriesPerServing() {
        if (servings < 1) return totalCalories;
        return totalCalories / servings;
    }
}