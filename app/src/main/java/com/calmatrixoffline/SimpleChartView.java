package com.calmatrixoffline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class SimpleChartView extends View {
    private List<ChartData> data;
    private Paint barPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint labelPaint = new Paint();
    private Paint zeroLinePaint = new Paint();
    private Paint titlePaint = new Paint();
    private int maxValue = 1000;
    private int minValue = -1000;
    private String chartTitle = "7-Day Calorie Balance"; // default

    public SimpleChartView(Context context) {
        super(context);
        init();
    }

    public SimpleChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setColor(Color.GRAY);
        labelPaint.setTextSize(24f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        zeroLinePaint.setColor(Color.rgb(100, 100, 100));
        zeroLinePaint.setStrokeWidth(2f);
        titlePaint.setColor(Color.rgb(245, 176, 65));
        titlePaint.setTextSize(28f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
    }

    // Set chart title
    public void setChartTitle(String title) {
        this.chartTitle = title;
        invalidate();
    }

    public void setData(List<ChartData> chartData) {
        this.data = chartData;
        calculateRange();
        invalidate();
    }

    private void calculateRange() {
        if (data == null || data.isEmpty()) return;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (ChartData d : data) {
            if (d.value > max) max = d.value;
            if (d.value < min) min = d.value;
        }
        int absMax = Math.max(Math.abs(max), Math.abs(min));
        absMax = (int) Math.ceil(absMax / 100.0) * 100 + 100;
        maxValue = absMax;
        minValue = -absMax;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.isEmpty()) {
            textPaint.setColor(Color.GRAY);
            textPaint.setTextSize(32f);
            canvas.drawText("No data yet", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int paddingTop = 80;      // extra space for title
        int paddingBottom = 60;
        int paddingLeft = 70;
        int paddingRight = 40;

        int chartHeight = height - paddingTop - paddingBottom;
        int chartWidth = width - paddingLeft - paddingRight;

        // Draw title
        titlePaint.setColor(Color.rgb(245, 176, 65));
        titlePaint.setTextSize(28f);
        canvas.drawText(chartTitle, width / 2f, 40, titlePaint);

        // Draw subtitle (optional — you can remove if not needed)
        titlePaint.setColor(Color.GRAY);
        titlePaint.setTextSize(18f);
        // Show min/max label based on data
        String subtitle = "Min: " + minValue + "  |  Max: " + maxValue;
        canvas.drawText(subtitle, width / 2f, 68, titlePaint);

        // Zero line
        float zeroY = paddingTop + chartHeight / 2f;
        canvas.drawLine(paddingLeft, zeroY, width - paddingRight, zeroY, zeroLinePaint);

        int barCount = data.size();
        int barWidth = (chartWidth / barCount) - 16;
        if (barWidth < 10) barWidth = 10;

        float totalRange = maxValue - minValue;

        // Draw bars
        for (int i = 0; i < barCount; i++) {
            ChartData d = data.get(i);
            float x = paddingLeft + i * (chartWidth / barCount) + 8;

            float barHeight = (float) d.value / totalRange * chartHeight;
            float y;

            if (d.value >= 0) {
                y = zeroY - barHeight;
                barPaint.setColor(Color.rgb(76, 175, 80));
            } else {
                y = zeroY;
                barHeight = -barHeight;
                barPaint.setColor(Color.rgb(244, 67, 54));
            }

            if (y < paddingTop) y = paddingTop;
            if (y + barHeight > paddingTop + chartHeight) {
                barHeight = paddingTop + chartHeight - y;
            }

            RectF rect = new RectF(x, y, x + barWidth, y + barHeight);
            canvas.drawRoundRect(rect, 8, 8, barPaint);

            // Draw value on bar
            String valueStr = String.valueOf(d.value);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(26f);
            textPaint.setTextAlign(Paint.Align.CENTER);

            float textY;
            if (d.value >= 0) {
                textY = y - 12;
                if (textY < paddingTop + 20) textY = y + barHeight + 28;
            } else {
                textY = y + barHeight + 28;
                if (textY > height - paddingBottom) textY = y - 12;
            }
            canvas.drawText(valueStr, x + barWidth / 2, textY, textPaint);

            // Draw day label
            labelPaint.setColor(Color.GRAY);
            labelPaint.setTextSize(24f);
            labelPaint.setTextAlign(Paint.Align.CENTER);
            float labelY = paddingTop + chartHeight + 36;
            canvas.drawText(d.label, x + barWidth / 2, labelY, labelPaint);
        }

        // Y-axis labels
        labelPaint.setColor(Color.GRAY);
        labelPaint.setTextSize(20f);
        labelPaint.setTextAlign(Paint.Align.RIGHT);

        int step = maxValue / 4;
        if (step < 100) step = 100;

        for (int val = -maxValue; val <= maxValue; val += step) {
            if (val == 0) continue;
            float y = paddingTop + chartHeight / 2f - ((float) val / totalRange * chartHeight);
            if (y >= paddingTop && y <= paddingTop + chartHeight) {
                canvas.drawText(String.valueOf(val), paddingLeft - 12, y + 8, labelPaint);
            }
        }

        // Draw "0" label
        labelPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("0", paddingLeft - 12, zeroY + 8, labelPaint);
    }

    public static class ChartData {
        public String label;
        public int value;

        public ChartData(String label, int value) {
            this.label = label;
            this.value = value;
        }
    }
}