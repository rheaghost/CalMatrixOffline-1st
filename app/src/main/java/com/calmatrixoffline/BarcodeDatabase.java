package com.calmatrixoffline;

import java.util.HashMap;

public class BarcodeDatabase {
    private static final HashMap<String, BarcodeItem> BARCODES = new HashMap<>();

    static {
        // ===== DRINKS =====
        addBarcode("123456789012", "Coca-Cola Classic", 140, 0, 39, 0, "12 oz");
        addBarcode("123456789013", "Diet Coke", 0, 0, 0, 0, "12 oz");
        addBarcode("123456789014", "Sprite", 140, 0, 38, 0, "12 oz");
        addBarcode("123456789015", "Pepsi", 150, 0, 41, 0, "12 oz");
        addBarcode("123456789016", "Mountain Dew", 170, 0, 46, 0, "12 oz");
        addBarcode("123456789017", "Gatorade", 80, 0, 21, 0, "12 oz");
        addBarcode("123456789018", "Monster Energy", 110, 0, 27, 0, "1 can");
        addBarcode("123456789019", "Red Bull", 110, 0, 27, 0, "1 can");

        // ===== SNACKS =====
        addBarcode("123456789020", "Lay's Classic Chips", 160, 2, 15, 10, "1 oz");
        addBarcode("123456789021", "Doritos Nacho Cheese", 150, 2, 18, 8, "1 oz");
        addBarcode("123456789022", "Pringles Original", 150, 1, 15, 10, "1 oz");
        addBarcode("123456789023", "Oreo Cookies", 160, 1, 22, 7, "3 cookies");
        addBarcode("123456789024", "Cheez-It Crackers", 150, 3, 16, 8, "1 oz");
        addBarcode("123456789025", "Goldfish Crackers", 140, 3, 18, 6, "1 oz");

        // ===== BREAD & BAKERY =====
        addBarcode("123456789026", "Wonder White Bread", 79, 2.5, 15, 1, "1 slice");
        addBarcode("123456789027", "Thomas English Muffin", 130, 4, 26, 1, "1 muffin");
        addBarcode("123456789028", "Nature's Own Wheat Bread", 70, 3, 12, 1, "1 slice");

        // ===== DAIRY =====
        addBarcode("123456789029", "Chobani Greek Yogurt", 100, 17, 6, 0.5, "1 container");
        addBarcode("123456789030", "Horizon Organic Milk", 149, 8, 12, 8, "1 cup");
        addBarcode("123456789031", "Kraft Cheddar Cheese", 80, 5, 0.5, 7, "1 slice");

        // ===== CONDIMENTS =====
        addBarcode("123456789032", "Heinz Ketchup", 15, 0.2, 4, 0, "1 tbsp");
        addBarcode("123456789033", "Hellmann's Mayonnaise", 94, 0.1, 0.1, 10, "1 tbsp");
        addBarcode("123456789034", "French's Mustard", 5, 0.3, 0.5, 0.3, "1 tsp");
        addBarcode("123456789035", "Kikkoman Soy Sauce", 10, 1.5, 1, 0, "1 tbsp");

        // ===== ASIAN =====
        addBarcode("123456789036", "Nongshim Shin Ramyun", 450, 12, 55, 16, "1 pack");
        addBarcode("123456789037", "Samyang Buldak", 500, 14, 58, 18, "1 pack");
        addBarcode("123456789038", "Kimchi", 25, 1.5, 4, 0.5, "1 cup");

        // ===== BREAKFAST =====
        addBarcode("123456789039", "Quaker Oats Oatmeal", 150, 5, 27, 2.5, "1 packet");
        addBarcode("123456789040", "Eggo Pancakes", 130, 4, 18, 5, "1 pancake");
        addBarcode("123456789041", "Honey Nut Cheerios", 110, 3, 22, 1.5, "1 cup");

        // ===== CANNED =====
        addBarcode("123456789042", "Campbell's Tomato Soup", 90, 2, 20, 0.5, "1 cup");
        addBarcode("123456789043", "Del Monte Corn", 60, 2, 14, 0.5, "1/2 cup");
        addBarcode("123456789044", "Dole Pineapple Chunks", 80, 0.5, 20, 0, "1/2 cup");

        // ===== FROZEN =====
        addBarcode("123456789045", "Lean Cuisine Chicken Alfredo", 280, 14, 28, 12, "1 meal");
        addBarcode("123456789046", "Hot Pockets Pepperoni", 300, 12, 35, 14, "1 pocket");
        addBarcode("123456789047", "Breyers Vanilla Ice Cream", 130, 2, 17, 6, "1/2 cup");

        // ===== MORE SNACKS =====
        addBarcode("123456789048", "Pop-Tarts Strawberry", 200, 2, 36, 5, "1 pastry");
        addBarcode("123456789049", "Nutella", 100, 1.5, 10, 6, "1 tbsp");
        addBarcode("123456789050", "Ritz Crackers", 80, 1, 10, 4, "5 crackers");

        // ok barcode now works 2608200242  remove if duplicate
        addBarcode("123456789052", "Samyang Buldak Ramen", 500, 14, 58, 18, "1 pack");
        addBarcode("123456789053", "Pocky Chocolate", 80, 1.5, 10, 3.5, "1 stick");
        addBarcode("123456789054", "Kit Kat", 210, 3, 26, 11, "1 bar");
        addBarcode("123456789055", "Snickers", 250, 4, 30, 12, "1 bar");
        addBarcode("12345678905t", "Twix", 220, 2, 28, 11, "1 bar");

    }
    // ============================================================
    //  SINGLE addBarcode METHOD — handles ALL types
    // ============================================================
    private static void addBarcode(String barcode, String name,
                                   double calories, double protein,
                                   double carbs, double fat, String serving) {
        BARCODES.put(barcode, new BarcodeItem(
                name,
                (int) Math.round(calories),
                (int) Math.round(protein),
                (int) Math.round(carbs),
                (int) Math.round(fat),
                serving
        ));
    }

    public static BarcodeItem search(String barcode) {
        return BARCODES.get(barcode);
    }

    public static class BarcodeItem {
        public String name;
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public String serving;

        public BarcodeItem(String name, int calories, int protein, int carbs, int fat, String serving) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.serving = serving;
        }
    }
}