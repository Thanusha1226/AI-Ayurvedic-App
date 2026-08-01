package com.techno.aiproject.models;

import java.util.List;

public class PlantNetResponse {
    public List<Result> results;

    public static class Result {
        public double score;
        public Species species;
    }

    public static class Species {
        public String scientificNameWithoutAuthor;
        public String scientificName;
        public List<String> commonNames;
    }
}
