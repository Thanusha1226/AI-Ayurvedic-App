package com.techno.aiproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoritePlantDao {
    @Query("SELECT * FROM favorite_plants ORDER BY timestamp DESC")
    List<FavoritePlant> getAllFavorites();

    @Insert
    long insert(FavoritePlant favorite);

    @Delete
    void delete(FavoritePlant favorite);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_plants WHERE scientificName = :sciName LIMIT 1)")
    boolean isFavorite(String sciName);

    @Query("DELETE FROM favorite_plants WHERE scientificName = :sciName")
    void deleteByScientificName(String sciName);
}
