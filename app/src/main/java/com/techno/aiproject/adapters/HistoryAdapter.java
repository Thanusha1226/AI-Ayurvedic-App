package com.techno.aiproject.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.aiproject.R;
import com.techno.aiproject.database.PlantHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<PlantHistory> historyList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PlantHistory item);
        void onDeleteClick(PlantHistory item);
    }

    public HistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<PlantHistory> data) {
        this.historyList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_card, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        PlantHistory item = historyList.get(position);
        holder.tvScientificName.setText(item.scientificName);
        holder.tvCommonName.setText(item.commonName);
        String dateStr = DateFormat.format("dd MMM yyyy, hh:mm a", item.timestamp).toString();
        holder.tvDate.setText(dateStr);

        if (item.imagePath != null && !item.imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(item.imagePath))
                    .placeholder(R.drawable.ic_plant_placeholder)
                    .centerCrop()
                    .into(holder.imgPlant);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlant, btnDelete;
        TextView tvScientificName, tvCommonName, tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlant = itemView.findViewById(R.id.imgPlant);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvScientificName = itemView.findViewById(R.id.tvScientificName);
            tvCommonName = itemView.findViewById(R.id.tvCommonName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
