package com.calmatrixoffline;

import java.util.HashMap;

public class FoodDatabase {
    private static final HashMap<String, FoodItem> FOODS = new HashMap<>();

    static {
        // ===== FAST FOOD =====
        addFood("cheeseburger", "Cheeseburger", 303, 15, 30, 13, "1 sandwich");
        addFood("hamburger", "Hamburger", 303, 15, 30, 13, "1 sandwich");
        addFood("double cheeseburger", "Double Cheeseburger", 450, 25, 34, 24, "1 sandwich");
        addFood("bacon cheeseburger", "Bacon Cheeseburger", 350, 18, 32, 18, "1 sandwich");
        addFood("pizza", "Cheese Pizza", 285, 12, 36, 10, "1 slice");
        addFood("pepperoni pizza", "Pepperoni Pizza", 310, 14, 36, 13, "1 slice");
        addFood("fries", "French Fries", 365, 4, 47, 17, "1 medium");
        addFood("french fries", "French Fries", 365, 4, 47, 17, "1 medium");
        addFood("chicken nuggets", "Chicken Nuggets", 280, 15, 18, 17, "6 pieces");
        addFood("hot dog", "Hot Dog", 290, 10, 29, 15, "1 serving");
        addFood("taco", "Taco", 200, 10, 20, 10, "1 taco");
        addFood("burrito", "Burrito", 400, 15, 45, 18, "1 burrito");
        addFood("quesadilla", "Quesadilla", 350, 12, 30, 18, "1 serving");
        addFood("onion rings", "Onion Rings", 300, 4, 35, 16, "1 serving");
        addFood("milkshake", "Milkshake", 400, 8, 55, 16, "1 medium");
        addFood("soda", "Cola Soda", 150, 0, 39, 0, "1 can");
        addFood("diet soda", "Diet Soda", 2, 0, 0, 0, "1 can");
        addFood("iced tea", "Iced Tea", 90, 0, 23, 0, "1 glass");

        // ===== MEATS =====
        addFood("chicken", "Chicken Breast", 165, 31, 0, 3.6, "1 breast");
        addFood("chicken breast", "Chicken Breast", 165, 31, 0, 3.6, "1 breast");
        addFood("chicken thigh", "Chicken Thigh", 209, 26, 0, 11, "1 thigh");
        addFood("chicken wing", "Chicken Wing", 100, 8, 0, 7, "1 wing");
        addFood("chicken drumstick", "Chicken Drumstick", 150, 13, 0, 10, "1 drumstick");
        addFood("salmon", "Salmon", 367, 40, 0, 22, "1 fillet");
        addFood("steak", "Beef Steak", 210, 26, 0, 12, "100g");
        addFood("sirloin", "Sirloin Steak", 250, 28, 0, 15, "100g");
        addFood("ribeye", "Ribeye Steak", 290, 24, 0, 20, "100g");
        addFood("ground beef", "Ground Beef", 250, 20, 0, 18, "100g");
        addFood("pork chop", "Pork Chop", 240, 22, 0, 16, "1 chop");
        addFood("bacon", "Bacon", 86, 6, 0.5, 7, "2 slices");
        addFood("sausage", "Sausage", 180, 12, 2, 14, "1 link");
        addFood("egg", "Egg (scrambled)", 140, 12, 1, 10, "2 eggs");
        addFood("fried egg", "Fried Egg", 90, 6, 0.5, 7, "1 egg");
        addFood("boiled egg", "Boiled Egg", 78, 6, 0.5, 5, "1 egg");
        addFood("omelette", "Omelette", 200, 15, 2, 15, "2 eggs");
        addFood("tofu", "Tofu", 70, 8, 2, 4, "100g");
        addFood("tempeh", "Tempeh", 190, 18, 12, 10, "100g");

        // ===== SEAFOOD =====
        addFood("tuna", "Tuna", 130, 28, 0, 1, "100g");
        addFood("shrimp", "Shrimp", 84, 18, 0, 1, "100g");
        addFood("lobster", "Lobster", 90, 19, 0, 1, "100g");
        addFood("crab", "Crab", 87, 18, 0, 1, "100g");
        addFood("mackerel", "Mackerel", 262, 18, 0, 20, "100g");
        addFood("sardines", "Sardines", 208, 24, 0, 12, "100g");
        addFood("cod", "Cod", 82, 18, 0, 1, "100g");
        addFood("tilapia", "Tilapia", 96, 20, 0, 2, "100g");

        // ===== GRAINS =====
        addFood("rice", "White Rice", 206, 4.3, 45, 0.4, "1 cup");
        addFood("white rice", "White Rice", 206, 4.3, 45, 0.4, "1 cup");
        addFood("brown rice", "Brown Rice", 216, 5, 45, 1.8, "1 cup");
        addFood("fried rice", "Fried Rice", 330, 8, 45, 12, "1 cup");
        addFood("pasta", "Pasta", 221, 8, 43, 1.3, "1 cup");
        addFood("spaghetti", "Spaghetti", 220, 8, 43, 1.3, "1 cup");
        addFood("macaroni", "Macaroni", 220, 8, 43, 1.3, "1 cup");
        addFood("bread", "White Bread", 79, 2.5, 15, 1, "1 slice");
        addFood("wheat bread", "Wheat Bread", 70, 3, 12, 1, "1 slice");
        addFood("rye bread", "Rye Bread", 83, 2.7, 16, 1, "1 slice");
        addFood("bagel", "Bagel", 250, 10, 48, 2, "1 bagel");
        addFood("croissant", "Croissant", 260, 5, 30, 14, "1 croissant");
        addFood("muffin", "Muffin", 320, 5, 40, 15, "1 muffin");
        addFood("pancake", "Pancake", 130, 4, 18, 5, "1 pancake");
        addFood("waffle", "Waffle", 240, 6, 30, 10, "1 waffle");
        addFood("oatmeal", "Oatmeal", 166, 6, 28, 3.6, "1 cup");
        addFood("cereal", "Cereal", 150, 3, 30, 2, "1 cup");
        addFood("granola", "Granola", 300, 10, 40, 12, "1 cup");
        addFood("quinoa", "Quinoa", 222, 8, 39, 4, "1 cup");
        addFood("couscous", "Couscous", 176, 6, 36, 0.5, "1 cup");
        addFood("popcorn", "Popcorn", 30, 1, 6, 0.5, "1 cup");
        addFood("pretzel", "Pretzel", 180, 5, 36, 2, "1 large");
        addFood("cracker", "Cracker", 16, 0.3, 2.5, 0.5, "1 cracker");

        // ===== FRUITS =====
        addFood("apple", "Apple", 95, 0.5, 25, 0.3, "1 medium");
        addFood("banana", "Banana", 105, 1.3, 27, 0.4, "1 medium");
        addFood("orange", "Orange", 62, 1.2, 15, 0.2, "1 medium");
        addFood("strawberry", "Strawberries", 49, 1, 12, 0.5, "1 cup");
        addFood("grape", "Grapes", 62, 0.6, 16, 0.3, "1 cup");
        addFood("watermelon", "Watermelon", 46, 0.9, 11, 0.2, "1 cup");
        addFood("cantaloupe", "Cantaloupe", 54, 1.3, 13, 0.3, "1 cup");
        addFood("honeydew", "Honeydew", 64, 1, 16, 0.2, "1 cup");
        addFood("mango", "Mango", 100, 1.4, 25, 0.6, "1 cup");
        addFood("pineapple", "Pineapple", 82, 0.9, 22, 0.2, "1 cup");
        addFood("peach", "Peach", 58, 1.4, 14, 0.4, "1 medium");
        addFood("pear", "Pear", 96, 0.6, 25, 0.2, "1 medium");
        addFood("plum", "Plum", 46, 0.7, 11, 0.3, "1 medium");
        addFood("cherries", "Cherries", 87, 1.5, 22, 0.3, "1 cup");
        addFood("blueberry", "Blueberries", 84, 1.1, 21, 0.5, "1 cup");
        addFood("raspberry", "Raspberries", 65, 1.5, 15, 0.8, "1 cup");
        addFood("blackberry", "Blackberries", 62, 2, 14, 0.7, "1 cup");
        addFood("kiwi", "Kiwi", 42, 0.8, 10, 0.3, "1 kiwi");
        addFood("lemon", "Lemon", 17, 0.6, 5, 0.2, "1 lemon");
        addFood("lime", "Lime", 20, 0.5, 7, 0.1, "1 lime");
        addFood("grapefruit", "Grapefruit", 52, 1, 13, 0.2, "1/2 fruit");
        addFood("avocado", "Avocado", 160, 2, 9, 15, "1/2 fruit");
        addFood("coconut", "Coconut", 350, 3.3, 15, 33, "1 cup");

        // ===== VEGETABLES =====
        addFood("broccoli", "Broccoli", 55, 3.7, 11, 0.6, "1 cup");
        addFood("salad", "Garden Salad", 45, 3, 8, 0.8, "1 bowl");
        addFood("potato", "Potato", 161, 4.3, 37, 0.2, "1 medium");
        addFood("mashed potato", "Mashed Potato", 210, 4, 35, 8, "1 cup");
        addFood("baked potato", "Baked Potato", 160, 4.3, 37, 0.2, "1 medium");
        addFood("carrot", "Carrots", 52, 1.2, 12, 0.3, "1 cup");
        addFood("tomato", "Tomato", 22, 1.1, 4.8, 0.2, "1 medium");
        addFood("corn", "Corn", 132, 4.6, 29, 1.9, "1 cup");
        addFood("spinach", "Spinach", 23, 2.9, 3.6, 0.4, "1 cup");
        addFood("kale", "Kale", 33, 3, 6, 0.6, "1 cup");
        addFood("lettuce", "Lettuce", 15, 1.4, 3, 0.2, "1 cup");
        addFood("cucumber", "Cucumber", 16, 0.7, 3.6, 0.1, "1 cup");
        addFood("bell pepper", "Bell Pepper", 30, 1.2, 7, 0.3, "1 medium");
        addFood("celery", "Celery", 16, 0.7, 3.5, 0.2, "1 stalk");
        addFood("onion", "Onion", 44, 1.2, 10, 0.1, "1 medium");
        addFood("garlic", "Garlic", 4, 0.2, 1, 0, "1 clove");
        addFood("mushroom", "Mushrooms", 15, 2.2, 2, 0.2, "1 cup");
        addFood("zucchini", "Zucchini", 19, 1.4, 3.5, 0.3, "1 cup");
        addFood("eggplant", "Eggplant", 20, 0.8, 5, 0.2, "1 cup");
        addFood("asparagus", "Asparagus", 27, 2.9, 5, 0.2, "1 cup");
        addFood("green beans", "Green Beans", 34, 2, 8, 0.2, "1 cup");
        addFood("peas", "Peas", 117, 8, 21, 0.4, "1 cup");
        addFood("sweet potato", "Sweet Potato", 180, 4, 41, 0.3, "1 medium");
        addFood("pumpkin", "Pumpkin", 30, 1.2, 7, 0.1, "1 cup");
        addFood("beetroot", "Beetroot", 58, 2.2, 13, 0.2, "1 cup");
        addFood("cabbage", "Cabbage", 22, 1.1, 5, 0.1, "1 cup");
        addFood("cauliflower", "Cauliflower", 27, 2, 5, 0.3, "1 cup");
        addFood("brussels sprouts", "Brussels Sprouts", 56, 4, 11, 0.8, "1 cup");
        addFood("radish", "Radish", 19, 0.8, 4, 0.1, "1 cup");
        addFood("turnip", "Turnip", 34, 1.1, 8, 0.1, "1 cup");

        // ===== DAIRY =====
        addFood("yogurt", "Greek Yogurt", 100, 17, 6, 0.5, "1 container");
        addFood("milk", "Whole Milk", 149, 8, 12, 8, "1 cup");
        addFood("skim milk", "Skim Milk", 83, 8, 12, 0.2, "1 cup");
        addFood("almond milk", "Almond Milk", 30, 1, 1, 2.5, "1 cup");
        addFood("soy milk", "Soy Milk", 80, 7, 4, 4, "1 cup");
        addFood("oat milk", "Oat Milk", 120, 3, 16, 5, "1 cup");
        addFood("cheese", "Cheddar Cheese", 80, 5, 0.5, 7, "1 slice");
        addFood("swiss cheese", "Swiss Cheese", 80, 6, 0.5, 6, "1 slice");
        addFood("american cheese", "American Cheese", 70, 4, 1, 5, "1 slice");
        addFood("cream cheese", "Cream Cheese", 35, 1, 0.5, 3.5, "1 tbsp");
        addFood("sour cream", "Sour Cream", 30, 0.5, 0.7, 3, "1 tbsp");
        addFood("butter", "Butter", 102, 0.1, 0, 11.5, "1 tbsp");
        addFood("margarine", "Margarine", 100, 0, 0, 11, "1 tbsp");
        addFood("ice cream", "Ice Cream", 207, 3.5, 24, 11, "1 scoop");
        addFood("frozen yogurt", "Frozen Yogurt", 180, 3.5, 30, 5, "1 serving");
        addFood("whipped cream", "Whipped Cream", 15, 0.2, 0.5, 1.5, "1 tbsp");

        // ===== NUTS & SEEDS =====
        addFood("almond", "Almonds", 170, 6, 6, 15, "1 handful");
        addFood("peanut butter", "Peanut Butter", 94, 4, 3.5, 8, "1 tbsp");
        addFood("walnut", "Walnuts", 185, 4.3, 3.9, 18.5, "1 handful");
        addFood("cashew", "Cashews", 157, 5.2, 8.6, 12.4, "1 handful");
        addFood("pecan", "Pecans", 196, 2.6, 4, 20, "1 handful");
        addFood("pistachio", "Pistachios", 160, 6, 8, 13, "1 handful");
        addFood("hazelnut", "Hazelnuts", 178, 4.2, 5, 17, "1 handful");
        addFood("sunflower seeds", "Sunflower Seeds", 165, 5.8, 6.8, 14, "1 handful");
        addFood("pumpkin seeds", "Pumpkin Seeds", 180, 8, 4, 15, "1 handful");
        addFood("chia seeds", "Chia Seeds", 60, 2.5, 5, 3.5, "1 tbsp");
        addFood("flax seeds", "Flax Seeds", 37, 1.3, 2, 3, "1 tbsp");
        addFood("sesame seeds", "Sesame Seeds", 52, 1.6, 2.1, 4.5, "1 tbsp");

        // ===== BEANS & LEGUMES =====
        addFood("black beans", "Black Beans", 114, 8, 20, 0.5, "1/2 cup");
        addFood("kidney beans", "Kidney Beans", 110, 8, 20, 0.5, "1/2 cup");
        addFood("chickpeas", "Chickpeas", 135, 7, 22, 2, "1/2 cup");
        addFood("lentils", "Lentils", 115, 9, 20, 0.4, "1/2 cup");
        addFood("green peas", "Green Peas", 67, 4.5, 12, 0.4, "1/2 cup");
        addFood("edamame", "Edamame", 95, 9, 8, 4, "1/2 cup");
        addFood("hummus", "Hummus", 80, 2.5, 7, 5, "2 tbsp");
        addFood("falafel", "Falafel", 120, 6, 15, 5, "1 piece");

        // ===== DRINKS =====
        addFood("coffee", "Black Coffee", 2, 0, 0, 0, "1 cup");
        addFood("espresso", "Espresso", 5, 0, 0, 0, "1 shot");
        addFood("latte", "Latte", 150, 8, 14, 8, "1 cup");
        addFood("cappuccino", "Cappuccino", 120, 7, 10, 6, "1 cup");
        addFood("tea", "Black Tea", 2, 0, 0, 0, "1 cup");
        addFood("green tea", "Green Tea", 2, 0, 0, 0, "1 cup");
        addFood("herbal tea", "Herbal Tea", 2, 0, 0, 0, "1 cup");
        addFood("juice", "Orange Juice", 112, 1.7, 25, 0.5, "1 cup");
        addFood("apple juice", "Apple Juice", 95, 0.2, 24, 0.2, "1 cup");
        addFood("cranberry juice", "Cranberry Juice", 110, 0.5, 27, 0.2, "1 cup");
        addFood("grape juice", "Grape Juice", 120, 0.5, 30, 0.2, "1 cup");
        addFood("lemonade", "Lemonade", 100, 0, 26, 0, "1 cup");
        addFood("smoothie", "Smoothie", 200, 3, 35, 6, "1 cup");
        addFood("protein shake", "Protein Shake", 120, 24, 3, 1.5, "1 serving");
        addFood("energy drink", "Energy Drink", 110, 0, 27, 0, "1 can");

        // ===== ASIAN FOOD =====
        addFood("ramen", "Ramen", 450, 18, 55, 16, "1 bowl");
        addFood("sushi", "Sushi Roll", 280, 12, 45, 5, "6 pieces");
        addFood("sashimi", "Sashimi", 150, 20, 0, 7, "6 pieces");
        addFood("bibimbap", "Bibimbap", 550, 22, 70, 18, "1 bowl");
        addFood("kimchi", "Kimchi", 25, 1.5, 4, 0.5, "1 cup");
        addFood("curry", "Curry Rice", 450, 12, 65, 15, "1 bowl");
        addFood("curry rice", "Curry Rice", 450, 12, 65, 15, "1 bowl");
        addFood("pad thai", "Pad Thai", 450, 14, 60, 16, "1 serving");
        addFood("fried rice", "Fried Rice", 330, 8, 45, 12, "1 cup");
        addFood("dim sum", "Dim Sum", 120, 6, 12, 6, "1 piece");
        addFood("spring roll", "Spring Roll", 80, 2, 12, 3, "1 piece");
        addFood("dumpling", "Dumpling", 80, 4, 10, 3, "1 piece");
        addFood("tempura", "Tempura", 150, 5, 12, 10, "1 piece");
        addFood("teriyaki", "Teriyaki Chicken", 280, 18, 20, 14, "1 serving");
        addFood("katsu", "Chicken Katsu", 300, 15, 25, 16, "1 serving");
        addFood("pho", "Pho", 350, 16, 45, 12, "1 bowl");
        addFood("banh mi", "Banh Mi", 350, 12, 45, 14, "1 sandwich");
        addFood("satay", "Satay", 150, 12, 4, 10, "1 skewer");
        addFood("nasi goreng", "Nasi Goreng", 400, 10, 50, 18, "1 serving");
        addFood("laksa", "Laksa", 500, 15, 55, 24, "1 bowl");

        // ===== DESSERTS =====
        addFood("cake", "Cake", 250, 3, 35, 10, "1 slice");
        addFood("chocolate cake", "Chocolate Cake", 280, 4, 35, 14, "1 slice");
        addFood("cheesecake", "Cheesecake", 320, 5, 25, 22, "1 slice");
        addFood("cookie", "Cookie", 160, 2, 20, 8, "1 cookie");
        addFood("chocolate chip cookie", "Chocolate Chip Cookie", 180, 2.5, 22, 9, "1 cookie");
        addFood("donut", "Donut", 250, 4, 31, 14, "1 donut");
        addFood("chocolate", "Chocolate Bar", 210, 2.5, 24, 12, "1 bar");
        addFood("dark chocolate", "Dark Chocolate", 170, 2, 14, 12, "1 oz");
        addFood("milk chocolate", "Milk Chocolate", 190, 2.5, 20, 12, "1 oz");
        addFood("candy", "Candy", 120, 0.5, 28, 1, "1 serving");
        addFood("gummy bears", "Gummy Bears", 100, 2, 23, 0, "1 serving");
        addFood("marshmallow", "Marshmallow", 90, 0.5, 23, 0, "1 serving");
        addFood("jelly", "Jelly", 50, 0.5, 13, 0, "1 tbsp");
        addFood("jam", "Strawberry Jam", 56, 0.1, 14, 0, "1 tbsp");
        addFood("honey", "Honey", 60, 0, 17, 0, "1 tbsp");
        addFood("maple syrup", "Maple Syrup", 52, 0, 13, 0, "1 tbsp");
        addFood("nutella", "Nutella", 100, 1.5, 10, 6, "1 tbsp");
        addFood("pudding", "Pudding", 120, 2, 22, 3, "1/2 cup");
        addFood("custard", "Custard", 130, 4, 18, 5, "1/2 cup");
        addFood("mousse", "Mousse", 150, 3, 20, 7, "1/2 cup");

        // ===== BREAKFAST =====
        addFood("cereal", "Cereal", 150, 3, 30, 2, "1 cup");
        addFood("granola", "Granola", 300, 10, 40, 12, "1 cup");
        addFood("oatmeal", "Oatmeal", 166, 6, 28, 3.6, "1 cup");
        addFood("pancake", "Pancake", 130, 4, 18, 5, "1 pancake");
        addFood("waffle", "Waffle", 240, 6, 30, 10, "1 waffle");
        addFood("french toast", "French Toast", 150, 5, 18, 6, "1 slice");
        addFood("bagel", "Bagel", 250, 10, 48, 2, "1 bagel");
        addFood("croissant", "Croissant", 260, 5, 30, 14, "1 croissant");
        addFood("muffin", "Muffin", 320, 5, 40, 15, "1 muffin");
        addFood("toast", "Toast", 79, 2.5, 15, 1, "1 slice");
        addFood("english muffin", "English Muffin", 130, 4, 26, 1, "1 muffin");
        addFood("biscuit", "Biscuit", 150, 3, 20, 7, "1 biscuit");
        addFood("scone", "Scone", 180, 4, 22, 8, "1 scone");
        addFood("danish", "Danish", 280, 4, 30, 16, "1 danish");

        // ===== SAUCES & CONDIMENTS =====
        addFood("ketchup", "Ketchup", 15, 0.2, 4, 0, "1 tbsp");
        addFood("mustard", "Mustard", 5, 0.3, 0.5, 0.3, "1 tsp");
        addFood("mayonnaise", "Mayonnaise", 94, 0.1, 0.1, 10, "1 tbsp");
        addFood("ranch dressing", "Ranch Dressing", 73, 0.3, 1, 7.5, "1 tbsp");
        addFood("caesar dressing", "Caesar Dressing", 80, 0.5, 1, 8, "1 tbsp");
        addFood("italian dressing", "Italian Dressing", 43, 0.1, 2, 4, "1 tbsp");
        addFood("vinegar", "Vinegar", 3, 0, 0.1, 0, "1 tbsp");
        addFood("soy sauce", "Soy Sauce", 10, 1.5, 1, 0, "1 tbsp");
        addFood("teriyaki sauce", "Teriyaki Sauce", 15, 0.5, 3, 0, "1 tbsp");
        addFood("hot sauce", "Hot Sauce", 2, 0.1, 0.5, 0, "1 tsp");
        addFood("bbq sauce", "BBQ Sauce", 30, 0.5, 7, 0, "1 tbsp");
        addFood("salsa", "Salsa", 10, 0.5, 2, 0, "1 tbsp");
        addFood("guacamole", "Guacamole", 25, 0.5, 1.5, 2.5, "1 tbsp");
        addFood("pesto", "Pesto", 80, 1.5, 2, 8, "1 tbsp");
    }

    private static void addFood(String key, String name, int calories, int protein, int carbs, int fat, String serving) {
        FOODS.put(key.toLowerCase(), new FoodItem(name, calories, protein, carbs, fat, serving));
    }

    private static void addFood(String key, String name, int calories, double protein, int carbs, double fat, String serving) {
        FOODS.put(key.toLowerCase(), new FoodItem(name, calories, (int)Math.round(protein), carbs, (int)Math.round(fat), serving));
    }

    private static void addFood(String key, String name, int calories, double protein, double carbs, double fat, String serving) {
        FOODS.put(key.toLowerCase(), new FoodItem(name, calories, (int)Math.round(protein), (int)Math.round(carbs), (int)Math.round(fat), serving));
    }

    public static FoodItem search(String query) {
        String q = query.toLowerCase().trim();
        if (FOODS.containsKey(q)) return FOODS.get(q);
        for (String key : FOODS.keySet()) {
            if (key.contains(q) || q.contains(key)) {
                return FOODS.get(key);
            }
        }
        return null;
    }

    static class FoodItem {
        String name;
        int calories;
        int protein;
        int carbs;
        int fat;
        String serving;

        FoodItem(String name, int calories, int protein, int carbs, int fat, String serving) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.serving = serving;
        }
    }
}