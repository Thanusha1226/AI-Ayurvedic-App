package com.techno.aiproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PlantHistoryDao {
    @Query("SELECT * FROM plant_history ORDER BY timestamp DESC")
    List<PlantHistory> getAllHistory();

    @Insert
    long insert(PlantHistory history);

    @Delete
    void delete(PlantHistory history);

    @Query("DELETE FROM plant_history")
    void deleteAll();
}
