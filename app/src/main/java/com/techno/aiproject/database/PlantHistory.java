package com.techno.aiproject.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plant_history")
public class PlantHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String scientificName;
    public String commonName;
    public String imagePath;
    public String explanationText;
    public long timestamp;

    public PlantHistory() {}

    public PlantHistory(String scientificName, String commonName, String imagePath, String explanationText, long timestamp) {
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.imagePath = imagePath;
        this.explanationText = explanationText;
        this.timestamp = timestamp;
    }
}
