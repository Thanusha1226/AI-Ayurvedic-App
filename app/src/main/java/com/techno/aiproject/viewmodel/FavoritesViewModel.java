package com.techno.aiproject.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.techno.aiproject.database.AppDatabase;
import com.techno.aiproject.database.FavoritePlant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final MutableLiveData<List<FavoritePlant>> favoritesList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFav = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<List<FavoritePlant>> getFavoritesList() {
        return favoritesList;
    }

    public LiveData<Boolean> getIsFav() {
        return isFav;
    }

    public void loadFavorites() {
        executor.execute(() -> {
            List<FavoritePlant> list = database.favoritePlantDao().getAllFavorites();
            favoritesList.postValue(list);
        });
    }

    public void checkIsFavorite(String scientificName) {
        executor.execute(() -> {
            boolean result = database.favoritePlantDao().isFavorite(scientificName);
            isFav.postValue(result);
        });
    }

    public void toggleFavorite(String scientificName, String commonName, String imagePath, String explanation) {
        executor.execute(() -> {
            boolean favorited = database.favoritePlantDao().isFavorite(scientificName);
            if (favorited) {
                database.favoritePlantDao().deleteByScientificName(scientificName);
                isFav.postValue(false);
            } else {
                FavoritePlant fav = new FavoritePlant(scientificName, commonName, imagePath, explanation, System.currentTimeMillis());
                database.favoritePlantDao().insert(fav);
                isFav.postValue(true);
            }
            loadFavorites();
        });
    }
}
