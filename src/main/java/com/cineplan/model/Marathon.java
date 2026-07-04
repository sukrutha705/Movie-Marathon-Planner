package com.cineplan.model;

import java.util.ArrayList;
import java.util.List;

public class Marathon {
    private int id;
    private String marathonName;
    private String mood;
    private int totalRuntime;
    private int totalScore;
    private String createdAt;
    private List<Movie> movies = new ArrayList<>();

    public Marathon() {}

    public Marathon(int id, String marathonName, String mood, int totalRuntime, int totalScore, String createdAt) {
        this.id = id;
        this.marathonName = marathonName;
        this.mood = mood;
        this.totalRuntime = totalRuntime;
        this.totalScore = totalScore;
        this.createdAt = createdAt;
    }

    public Marathon(String marathonName, String mood, int totalRuntime, int totalScore, String createdAt) {
        this.marathonName = marathonName;
        this.mood = mood;
        this.totalRuntime = totalRuntime;
        this.totalScore = totalScore;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMarathonName() { return marathonName; }
    public void setMarathonName(String marathonName) { this.marathonName = marathonName; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public int getTotalRuntime() { return totalRuntime; }
    public void setTotalRuntime(int totalRuntime) { this.totalRuntime = totalRuntime; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<Movie> getMovies() { return movies; }
    public void setMovies(List<Movie> movies) { this.movies = movies; }
    
    public void addMovie(Movie movie) {
        this.movies.add(movie);
    }
}
