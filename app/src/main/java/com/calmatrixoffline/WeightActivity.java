package com.calmatrixoffline;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WeightActivity extends AppCompatActivity {
    private EditText weightInput;
    private TextView latestWeightText, changeText, summaryText, graphSummary;
    private RecyclerView historyRecyclerView;
    private Button saveBtn, clearBtn, backBtn;
    private DatabaseHelper dbHelper;
    private WeightAdapter adapter;
    private SimpleChartView chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        dbHelper = new DatabaseHelper(this);

        weightInput = findViewById(R.id.weightInput);
        latestWeightText = findViewById(R.id.latestWeightText);
        changeText = findViewById(R.id.changeText);
        summaryText = findViewById(R.id.summaryText);
        graphSummary = findViewById(R.id.graphSummary);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        chartView = findViewById(R.id.chartView);
        saveBtn = findViewById(R.id.saveBtn);
        clearBtn = findViewById(R.id.clearBtn);
        backBtn = findViewById(R.id.backBtn);

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeightAdapter(dbHelper.getWeightHistory(), this::onDeleteEntry);
        historyRecyclerView.setAdapter(adapter);

        UserProfile profile = new UserProfile(this);
        if (profile.hasProfile()) {
            weightInput.setText(String.valueOf(profile.getWeight()));
        }

        updateUI();
        loadWeightGraph();

        saveBtn.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString().trim();
            if (weightStr.isEmpty()) {
                Toast.makeText(this, "Enter your weight", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double weight = Double.parseDouble(weightStr);
                if (weight <= 0 || weight > 500) {
                    Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                    return;
                }
                long id = dbHelper.insertWeight(weight);
                if (id != -1) {
                    Toast.makeText(this, "Weight logged: " + weight + " kg", Toast.LENGTH_SHORT).show();
                    weightInput.setText("");
                    updateUI();
                    loadWeightGraph();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn.setOnClickListener(v -> weightInput.setText(""));
        backBtn.setOnClickListener(v -> finish());
    }

    private void updateUI() {
        List<WeightEntry> entries = dbHelper.getWeightHistory();
        adapter.updateData(entries);

        double latest = dbHelper.getLatestWeight();
        double change = dbHelper.getWeightChange();

        if (latest > 0) {
            latestWeightText.setText("Current: " + latest + " kg");
        } else {
            latestWeightText.setText("No weight logged yet");
        }

        if (change != 0) {
            String direction = change > 0 ? "↑" : "↓";
            changeText.setText("Change: " + direction + " " + Math.abs(change) + " kg");
            changeText.setTextColor(getColor(change > 0 ? android.R.color.holo_red_light : android.R.color.holo_green_light));
        } else if (latest > 0) {
            changeText.setText("No previous weight to compare");
        } else {
            changeText.setText("Log your weight to see changes");
        }

        summaryText.setText(entries.size() + " weight entries logged");
    }

    /*
    private void loadWeightGraph() {
        List<WeightEntry> entries = dbHelper.getWeightHistory();
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();

        if (entries.isEmpty()) {
            graphSummary.setText("📊 No weight data yet");
            chartView.setData(chartData);
            return;
        }

        // Show last 14 entries
        int count = Math.min(entries.size(), 14);
        for (int i = count - 1; i >= 0; i--) {
            WeightEntry entry = entries.get(i);
            // Scale weight to int for display (e.g., 72.5 -> 725)
            int scaledWeight = (int) (entry.getWeight() * 10);
            String label = entry.getDate().substring(5); // MM-DD
            chartData.add(new SimpleChartView.ChartData(label, scaledWeight));
        }

        chartView.setData(chartData);

        double latest = dbHelper.getLatestWeight();
        if (latest > 0) {
            graphSummary.setText("📊 Latest: " + latest + " kg");
        } else {
            graphSummary.setText("📊 No weight data yet");
        }
    }

     */
    /*
    private void loadWeightGraph_old2() {
        List<WeightEntry> entries = dbHelper.getWeightHistory();
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();

        if (entries.isEmpty()) {
            graphSummary.setText("📊 No weight data yet");
            chartView.setData(chartData);
            return;
        }

        // Show last 14 entries
        int count = Math.min(entries.size(), 14);
        for (int i = count - 1; i >= 0; i--) {
            WeightEntry entry = entries.get(i);
            // Keep as kg * 10 to show one decimal place as integer (e.g., 72.5 -> 725)
            int scaledWeight = (int) (entry.getWeight() * 10);
            String label = entry.getDate().substring(5); // MM-DD
            chartData.add(new SimpleChartView.ChartData(label, scaledWeight));
        }

        chartView.setData(chartData);
        chartView.setChartTitle("⚖️ Weight Trend (kg)"); // <-- ADD THIS

        double latest = dbHelper.getLatestWeight();
        if (latest > 0) {
            graphSummary.setText("📊 Latest: " + latest + " kg");
        } else {
            graphSummary.setText("📊 No weight data yet");
        }
    }*/

    private void loadWeightGraph() {
        List<WeightEntry> entries = dbHelper.getWeightHistory();
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();

        if (entries.isEmpty()) {
            graphSummary.setText("📊 No weight data yet");
            chartView.setChartTitle("⚖️ Weight");
            chartView.setData(chartData);
            return;
        }

        int count = Math.min(entries.size(), 14);
        for (int i = count - 1; i >= 0; i--) {
            WeightEntry entry = entries.get(i);
            int scaledWeight = (int) (entry.getWeight() * 10);
            String label = entry.getDate().substring(5);
            chartData.add(new SimpleChartView.ChartData(label, scaledWeight));
        }

        chartView.setChartTitle("⚖️ Weight (kg × 10)");
        chartView.setData(chartData);

        double latest = dbHelper.getLatestWeight();
        if (latest > 0) {
            graphSummary.setText("📊 Latest: " + latest + " kg");
        } else {
            graphSummary.setText("📊 No weight data yet");
        }
    }

    private void onDeleteEntry(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Delete this weight entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteWeightEntry(id);
                    updateUI();
                    loadWeightGraph();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        loadWeightGraph();
    }
}