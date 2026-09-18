package com.calmatrixoffline;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private List<LogEntry> entries;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int id);
    }

    public LogAdapter(List<LogEntry> entries, OnDeleteClickListener listener) {
        this.entries = entries;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout item = new LinearLayout(parent.getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(16, 16, 16, 16);
        item.setBackgroundColor(Color.parseColor("#121216"));
        item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout textContainer = new LinearLayout(parent.getContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView entryText = new TextView(parent.getContext());
        entryText.setId(View.generateViewId());
        entryText.setTextColor(Color.parseColor("#cccccc"));
        entryText.setTextSize(14);
        textContainer.addView(entryText);

        TextView entryTime = new TextView(parent.getContext());
        entryTime.setId(View.generateViewId());
        entryTime.setTextColor(Color.parseColor("#666666"));
        entryTime.setTextSize(11);
        textContainer.addView(entryTime);

        item.addView(textContainer);

        ImageButton deleteBtn = new ImageButton(parent.getContext());
        deleteBtn.setId(View.generateViewId());
        deleteBtn.setImageResource(android.R.drawable.ic_delete);
        deleteBtn.setBackgroundColor(Color.parseColor("#3a2a2a"));
        deleteBtn.setPadding(8, 8, 8, 8);
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        item.addView(deleteBtn);

        return new ViewHolder(item, entryText, entryTime, deleteBtn);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogEntry entry = entries.get(position);
        String food = entry.getFoodName().equals("None") ? "" : "🍔 " + entry.getFoodName() + " (" + entry.getFoodCalories() + " cal)";
        String exercise = entry.getExerciseName().equals("None") ? "" : "🏃 " + entry.getExerciseName() + " (-" + entry.getExerciseCalories() + " cal)";
        StringBuilder line = new StringBuilder();
        if (!food.isEmpty()) line.append(food);
        if (!food.isEmpty() && !exercise.isEmpty()) line.append(" | ");
        if (!exercise.isEmpty()) line.append(exercise);
        line.append(" = ").append(entry.getNetCalories()).append(" cal");
        holder.entryText.setText(line.toString());
        holder.entryTime.setText(entry.getTimestamp() + " • " + entry.getDate());
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(entry.getId());
        });
    }

    @Override
    public int getItemCount() { return entries.size(); }

    public void updateData(List<LogEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView entryText, entryTime;
        ImageButton deleteBtn;
        ViewHolder(View itemView, TextView entryText, TextView entryTime, ImageButton deleteBtn) {
            super(itemView);
            this.entryText = entryText;
            this.entryTime = entryTime;
            this.deleteBtn = deleteBtn;
        }
    }
}