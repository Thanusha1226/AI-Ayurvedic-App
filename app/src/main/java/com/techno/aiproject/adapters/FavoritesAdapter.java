package com.techno.aiproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.aiproject.R;
import com.techno.aiproject.database.FavoritePlant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private List<FavoritePlant> favoriteList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FavoritePlant item);
        void onRemoveClick(FavoritePlant item);
    }

    public FavoritesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<FavoritePlant> data) {
        this.favoriteList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_card, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoritePlant item = favoriteList.get(position);
        holder.tvScientificName.setText(item.scientificName);
        holder.tvCommonName.setText(item.commonName);
        holder.tvDate.setVisibility(View.GONE);

        holder.btnDelete.setImageResource(R.drawable.ic_heart_filled);

        if (item.imagePath != null && !item.imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(item.imagePath))
                    .placeholder(R.drawable.ic_plant_placeholder)
                    .centerCrop()
                    .into(holder.imgPlant);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onRemoveClick(item));
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlant, btnDelete;
        TextView tvScientificName, tvCommonName, tvDate;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlant = itemView.findViewById(R.id.imgPlant);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvScientificName = itemView.findViewById(R.id.tvScientificName);
            tvCommonName = itemView.findViewById(R.id.tvCommonName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
