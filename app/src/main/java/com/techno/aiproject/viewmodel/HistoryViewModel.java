package com.techno.aiproject.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.techno.aiproject.database.AppDatabase;
import com.techno.aiproject.database.PlantHistory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final MutableLiveData<List<PlantHistory>> historyList = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<List<PlantHistory>> getHistoryList() {
        return historyList;
    }

    public void loadHistory() {
        executor.execute(() -> {
            List<PlantHistory> list = database.plantHistoryDao().getAllHistory();
            historyList.postValue(list);
        });
    }

    public void deleteHistoryItem(PlantHistory item) {
        executor.execute(() -> {
            database.plantHistoryDao().delete(item);
            loadHistory();
        });
    }
}
