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

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {
    private List<WeightEntry> entries;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int id);
    }

    public WeightAdapter(List<WeightEntry> entries, OnDeleteClickListener listener) {
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
        item.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView weightText = new TextView(parent.getContext());
        weightText.setId(View.generateViewId());
        weightText.setTextColor(Color.parseColor("#cccccc"));
        weightText.setTextSize(16);
        weightText.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        item.addView(weightText);

        ImageButton deleteBtn = new ImageButton(parent.getContext());
        deleteBtn.setId(View.generateViewId());
        deleteBtn.setImageResource(android.R.drawable.ic_delete);
        deleteBtn.setBackgroundColor(Color.parseColor("#3a2a2a"));
        deleteBtn.setPadding(8, 8, 8, 8);
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        item.addView(deleteBtn);

        return new ViewHolder(item, weightText, deleteBtn);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightEntry entry = entries.get(position);
        holder.weightText.setText(entry.getWeight() + " kg  •  " + entry.getDate() + "  " + entry.getTimestamp());
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(entry.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return entries.size(); }

    public void updateData(List<WeightEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weightText;
        ImageButton deleteBtn;

        ViewHolder(View itemView, TextView weightText, ImageButton deleteBtn) {
            super(itemView);
            this.weightText = weightText;
            this.deleteBtn = deleteBtn;
        }
    }
}