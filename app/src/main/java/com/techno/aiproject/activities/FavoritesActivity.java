package com.techno.aiproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techno.aiproject.R;
import com.techno.aiproject.adapters.FavoritesAdapter;
import com.techno.aiproject.database.FavoritePlant;
import com.techno.aiproject.viewmodel.FavoritesViewModel;

public class FavoritesActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView recyclerFavorites;
    private LinearLayout emptyState;

    private FavoritesViewModel favoritesViewModel;
    private FavoritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        btnBack = findViewById(R.id.btnBack);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        emptyState = findViewById(R.id.emptyState);

        btnBack.setOnClickListener(v -> finish());

        // Setup Adapter
        adapter = new FavoritesAdapter(new FavoritesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FavoritePlant item) {
                Intent intent = new Intent(FavoritesActivity.this, ResultsActivity.class);
                intent.putExtra("scientific_name", item.scientificName);
                intent.putExtra("common_name", item.commonName);
                intent.putExtra("explanation", item.explanationText);
                intent.putExtra("image_path", item.imagePath);
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(FavoritePlant item) {
                favoritesViewModel.toggleFavorite(item.scientificName, item.commonName, item.imagePath, item.explanationText);
            }
        });

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerFavorites.setAdapter(adapter);

        // ViewModel Setup
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        favoritesViewModel.getFavoritesList().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerFavorites.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerFavorites.setVisibility(View.VISIBLE);
                adapter.setData(list);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoritesViewModel.loadFavorites();
    }
}
