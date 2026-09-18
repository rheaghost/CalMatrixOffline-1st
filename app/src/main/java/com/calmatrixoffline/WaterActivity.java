package com.calmatrixoffline;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WaterActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView waterCountText, waterGoalText;
    private Button addWaterBtn, resetWaterBtn, backBtn;

    private SimpleChartView chartView;
    private TextView graphSummary;


    private static final String TABLE_WATER = "water_logs";
    private static final String COLUMN_WATER_ID = "id";
    private static final String COLUMN_WATER_AMOUNT = "amount";
    private static final String COLUMN_WATER_DATE = "date";
    private static final String COLUMN_WATER_TIMESTAMP = "timestamp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        dbHelper = new DatabaseHelper(this);
        waterCountText = findViewById(R.id.waterCountText);
        waterGoalText = findViewById(R.id.waterGoalText);
        addWaterBtn = findViewById(R.id.addWaterBtn);
        resetWaterBtn = findViewById(R.id.resetWaterBtn);
        backBtn = findViewById(R.id.backBtn);
        graphSummary = findViewById(R.id.graphSummary);//add 26090919
        chartView = findViewById(R.id.chartView); //2609092004

        updateWaterDisplay();
        loadWaterGraph(); //add 26090919

        addWaterBtn.setOnClickListener(v -> {
            dbHelper.insertWater(250);
            updateWaterDisplay();
            loadWaterGraph();  //add 26090919
            Toast.makeText(this, "💧 +250ml water logged", Toast.LENGTH_SHORT).show();
        });

        resetWaterBtn.setOnClickListener(v -> {
            dbHelper.resetTodayWater();
            updateWaterDisplay();
            loadWaterGraph();  //added 26090919
            Toast.makeText(this, "💧 Today's water reset", Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void updateWaterDisplay() {
        int total = dbHelper.getTodayWaterTotal();
        waterCountText.setText("💧 " + total + " ml");

        int goal = 2000;
        if (total >= goal) {
            waterGoalText.setText("🎉 Goal reached! " + total + " / " + goal + " ml");
            waterGoalText.setTextColor(getColor(android.R.color.holo_green_light));
        } else {
            waterGoalText.setText("🎯 " + total + " / " + goal + " ml");
            waterGoalText.setTextColor(getColor(android.R.color.holo_orange_light));
        }
    }//updateWaterDisplay

    //add for uiseabilityu 2609091552
    /*
    private void loadWaterGraph_Old() {
        // Get last 14 days of water data
        List<Integer> waterData = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());//added 26090919


        for (int i = 13; i >= 0; i--) {
            cal.setTime(Calendar.getInstance().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            int water = dbHelper.getDayWaterTotal(date);
            waterData.add(water);
            dates.add(new SimpleDateFormat("d", Locale.getDefault()).format(cal.getTime()));
        }

        // Display as a simple bar chart (you can reuse SimpleChartView)
        // Convert to ChartData format
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();
        for (int i = 0; i < waterData.size(); i++) {
            chartData.add(new SimpleChartView.ChartData(dates.get(i), waterData.get(i)));
        }
        // Set chart data and show
    }
    */

    /*
    private void loadWaterGraph_old2() {
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int total = 0;
        int count = 0;

        // Show last 14 days
        for (int i = 13; i >= 0; i--) {
            cal.setTime(Calendar.getInstance().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            int water = dbHelper.getDayWaterTotal(date);
            total += water;
            if (water > 0) count++;
            String label = new SimpleDateFormat("d", Locale.getDefault()).format(cal.getTime());
            chartData.add(new SimpleChartView.ChartData(label, water));
        }

        chartView.setData(chartData);

        if (count > 0) {
            int avg = total / count;
            graphSummary.setText("📊 14-Day Avg: " + avg + " ml/day");
        } else {
            graphSummary.setText("📊 No water data yet");
        }
    }//26090919

     */

    /*
    private void loadWaterGraph_Old2() {
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int total = 0;
        int count = 0;

        // Show last 14 days
        for (int i = 13; i >= 0; i--) {
            cal.setTime(Calendar.getInstance().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            int water = dbHelper.getDayWaterTotal(date);
            total += water;
            if (water > 0) count++;
            String label = new SimpleDateFormat("d", Locale.getDefault()).format(cal.getTime());
            chartData.add(new SimpleChartView.ChartData(label, water));
        }

        chartView.setData(chartData);
        chartView.setChartTitle("💧 14-Day Water Intake (ml)"); // <-- ADD THIS

        if (count > 0) {
            int avg = total / count;
            graphSummary.setText("📊 14-Day Avg: " + avg + " ml/day");
        } else {
            graphSummary.setText("📊 No water data yet");
        }
    }

     */
    private void loadWaterGraph() {
        List<SimpleChartView.ChartData> chartData = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int total = 0;
        int count = 0;

        for (int i = 13; i >= 0; i--) {
            cal.setTime(Calendar.getInstance().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            int water = dbHelper.getDayWaterTotal(date);
            total += water;
            if (water > 0) count++;
            String label = new SimpleDateFormat("d", Locale.getDefault()).format(cal.getTime());
            chartData.add(new SimpleChartView.ChartData(label, water));
        }

        // Set title and data
        chartView.setChartTitle("💧 Water Intake (ml)");
        chartView.setData(chartData);

        if (count > 0) {
            int avg = total / count;
            graphSummary.setText("📊 14-Day Avg: " + avg + " ml/day");
        } else {
            graphSummary.setText("📊 No water data yet");
        }
    }
    //loadWaterGraph
    //
    // ============================================================
// WATER METHODS
// ============================================================



    //


    @Override
    protected void onResume() {
        super.onResume();
        updateWaterDisplay();
    }//onResume
}//wateractivityy
