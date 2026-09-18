package com.calmatrixoffline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText apiKeyInput;
    private Button saveBtn, backBtn;

    private EditText reminderHourInput;
    private Button saveReminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        apiKeyInput = findViewById(R.id.apiKeyInput);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        reminderHourInput = findViewById(R.id.reminderHourInput);
        saveReminderBtn = findViewById(R.id.saveReminderBtn);

        final SharedPreferences prefs = getSharedPreferences("CalSnapPrefs", MODE_PRIVATE);

        // API key
        apiKeyInput.setText(prefs.getString("imgbb_api_key", ""));

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 900);
            }
        }

        // Exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= 31) {
            android.app.AlarmManager am = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                Intent i = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(i);
            }
        }

        saveBtn.setOnClickListener(v -> {
            String key = apiKeyInput.getText().toString().trim();
            if (key.isEmpty()) {
                Toast.makeText(this, "Please enter your API key", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("imgbb_api_key", key).apply();
            Toast.makeText(this, "✅ API key saved", Toast.LENGTH_SHORT).show();
        });

        saveReminderBtn.setOnClickListener(v -> {
            String s = reminderHourInput.getText().toString().trim();
            if (s.isEmpty()) return;
            int minutesFromNow;
            try {
                minutesFromNow = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (minutesFromNow < 1) {
                Toast.makeText(this, "Enter 1 or more", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit().putInt("reminder_minutes", minutesFromNow).apply();

            // Schedule alarm
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, minutesFromNow);

            android.app.AlarmManager am = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
            Intent nextIntent = new Intent(SettingsActivity.this, ReminderReceiver.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                    SettingsActivity.this, 0, nextIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT
                            | android.app.PendingIntent.FLAG_IMMUTABLE);

            if (am != null) {
                am.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pi);
            }

            Toast.makeText(this, "✅ Reminder set in " + minutesFromNow + " min",
                    Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}