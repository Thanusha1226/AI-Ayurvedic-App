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
import com.techno.aiproject.adapters.HistoryAdapter;
import com.techno.aiproject.database.PlantHistory;
import com.techno.aiproject.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView recyclerHistory;
    private LinearLayout emptyState;
    
    private HistoryViewModel historyViewModel;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnBack = findViewById(R.id.btnBack);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        emptyState = findViewById(R.id.emptyState);

        btnBack.setOnClickListener(v -> finish());

        // Setup Adapter
        adapter = new HistoryAdapter(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PlantHistory item) {
                Intent intent = new Intent(HistoryActivity.this, ResultsActivity.class);
                intent.putExtra("scientific_name", item.scientificName);
                intent.putExtra("common_name", item.commonName);
                intent.putExtra("explanation", item.explanationText);
                intent.putExtra("image_path", item.imagePath);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(PlantHistory item) {
                historyViewModel.deleteHistoryItem(item);
            }
        });

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);

        // ViewModel Setup
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.getHistoryList().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerHistory.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerHistory.setVisibility(View.VISIBLE);
                adapter.setData(list);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyViewModel.loadHistory();
    }
}
