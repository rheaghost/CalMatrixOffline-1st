package com.calmatrixoffline;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.helpText);
        Button backBtn = findViewById(R.id.backBtn);

        helpText.setText(
                "📸 TAKING PHOTOS\n" +
                        "Tap Camera → take or choose photo → tap AI Search. " +
                        "Google search opens in your browser.\n\n" +

                        "🔍 SEARCHING FOOD\n" +
                        "Type in the food box. Suggestions appear as you type. " +
                        "Tap a suggestion or press Search.\n\n" +

                        "📱 BARCODE\n" +
                        "Tap Barcode → scan a product → name and calories are filled in.\n\n" +

                        "🍱 MEALS AND RECIPES\n" +
                        "Meals: log foods → tap 'Save as Meal' → name it.\n" +
                        "Recipes: tap 'Create Meal' → add foods → set servings → save.\n" +
                        "To log: My Meals → tap → choose servings eaten.\n" +
                        "Meals (1 serving) log full amounts. Recipes divide by servings.\n\n" +

                        "🖼️ FOOD IMAGES\n" +
                        "Search a food → Add Image → Take Photo or Choose from Gallery. " +
                        "Delete removes it. Images are stored in Downloads/CalSnap/food_images/.\n\n" +

                        "💧 WATER AND WEIGHT\n" +
                        "Tap Water or Weight. Graphs show 14 days. Use the scrollbar at the bottom.\n\n" +

                        "📊 TRENDS\n" +
                        "Tap Trends for 30-day view. Scroll horizontally.\n\n" +

                        "💾 BACKUP AND RESTORE\n" +
                        "Tap Backup to save a .db file to Downloads/CalSnap/backup/.\n" +
                        "To restore: Recovery → Choose File → navigate to your device " +
                        "(e.g., Galaxy S20) → Download → CalSnap → backup → select file.\n" +
                        "NOTE: Android's file picker is different from Windows. " +
                        "Look for your device name in the sidebar.\n\n" +

                        "📥 IMPORT CSV\n" +
                        "Tap Import → choose foods.csv, exercises.csv, or barcodes.csv " +
                        "from Downloads. After import, the CSVs are not needed.\n\n" +

                        "🔔 REMINDER\n" +
                        "Settings → enter minutes → Save. " +
                        "On Samsung phones, set the app to Unrestricted battery " +
                        "to ensure reminders fire.\n\n" +

                        "━━━━━━━━━━━━━━━━━━\n" +
                        "⚖️ DISCLAIMER\n\n" +
                        "This app is NOT medical advice. It is a general-purpose " +
                        "calorie and nutrition tracker for personal use only.\n\n" +
                        "Calorie and nutrition values are estimates. " +
                        "Individual needs vary. Consult a qualified healthcare " +
                        "professional before making dietary changes.\n\n" +
                        "Do not use this app to diagnose, treat, or manage any " +
                        "medical condition.\n\n" +

                        "🔒 PRIVACY\n\n" +
                        "All data (food logs, weight, water, images, backups) " +
                        "stays on your device. No personal information is sent " +
                        "to any server.\n\n" +
                        "The only network access is:\n" +
                        "• Google/Bing/Yandex image search — opens in your browser " +
                        "when you tap those buttons\n" +
                        "• imgbb.com — only if you enter your own API key in Settings " +
                        "and use the online search feature\n\n" +
                        "If you do not use online search, no data leaves your device.\n\n" +

                        "📜 LICENSE\n\n" +
                        "Personal use only. Commercial use requires a written " +
                        "license. Contact the developer for terms.\n\n" +

                        "━━━━━━━━━━━━━━━━━━\n" +
                        "Version 1.1"
        );

        backBtn.setOnClickListener(v -> finish());
    }
}
