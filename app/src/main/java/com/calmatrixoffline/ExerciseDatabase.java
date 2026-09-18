package com.calmatrixoffline;


import java.util.HashMap;

public class ExerciseDatabase {
    private static final HashMap<String, ExerciseItem> EXERCISES = new HashMap<>();

    static {
        // Walking
        addExercise("walking slow", "Walking (slow)", 2.5);
        addExercise("walking moderate", "Walking (moderate)", 3.5);
        addExercise("walking brisk", "Walking (brisk)", 4.5);

        // Running
        addExercise("jogging", "Jogging", 6.0);
        addExercise("running", "Running", 8.0);
        addExercise("sprinting", "Sprinting", 12.0);

        // Cycling
        addExercise("cycling slow", "Cycling (slow)", 4.0);
        addExercise("cycling moderate", "Cycling (moderate)", 6.0);
        addExercise("cycling fast", "Cycling (fast)", 8.0);

        // Swimming
        addExercise("swimming slow", "Swimming (slow)", 4.0);
        addExercise("swimming moderate", "Swimming (moderate)", 6.0);
        addExercise("swimming fast", "Swimming (fast)", 8.0);

        // Gym
        addExercise("weight lifting", "Weight Lifting", 3.0);
        addExercise("push ups", "Push-ups", 3.0);
        addExercise("pull ups", "Pull-ups", 3.5);
        addExercise("squats", "Squats", 3.5);
        addExercise("burpees", "Burpees", 6.0);

        // Sports
        addExercise("basketball", "Basketball", 6.5);
        addExercise("soccer", "Soccer", 7.0);
        addExercise("tennis", "Tennis", 6.0);
        addExercise("volleyball", "Volleyball", 4.0);
        addExercise("boxing", "Boxing", 8.0);
        addExercise("yoga", "Yoga", 2.5);
        addExercise("dance", "Dancing", 4.5);

        // Daily
        addExercise("housework", "Housework", 2.5);
        addExercise("gardening", "Gardening", 3.0);
        addExercise("walking dog", "Walking the Dog", 3.5);

        // Stairs
        addExercise("stairs slow", "Climbing Stairs (slow)", 4.0);
        addExercise("stairs fast", "Climbing Stairs (fast)", 8.0);
    }

    private static void addExercise(String key, String name, double met) {
        EXERCISES.put(key.toLowerCase(), new ExerciseItem(name, met));
    }

    public static ExerciseItem search(String query) {
        String q = query.toLowerCase().trim();
        if (EXERCISES.containsKey(q)) return EXERCISES.get(q);
        for (String key : EXERCISES.keySet()) {
            if (key.contains(q) || q.contains(key)) {
                return EXERCISES.get(key);
            }
        }
        return null;
    }

    static class ExerciseItem {
        String name;
        double met;

        ExerciseItem(String name, double met) {
            this.name = name;
            this.met = met;
        }

        public int calculateCalories(double weightKg, int durationMinutes) {
            double calories = met * 3.5 * (weightKg / 200.0) * durationMinutes;
            return (int) Math.round(calories);
        }
    }
}