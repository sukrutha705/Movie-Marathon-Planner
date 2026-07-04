package com.cineplan.model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private int runtime; // in minutes
    private double rating;
    private int entertainmentScore; // 1 to 10
    private String posterUrl;
    private String description;
    private String watchUrl;
    private String language;
    private String imdbId;
    private String imdbUrl;
    private Integer releaseYear;
    private String country;
    private String director;
    private String cast;
    private String streamingPlatform;
    private String posterLocalPath;
    private String backdropPath;
    private String trailerUrl;
    private Double popularityScore;
    private String franchise;
    private String moodTags;

    // Constructors
    public Movie() {}

    public Movie(int id, String title, String genre, int runtime, double rating, int entertainmentScore, String posterUrl, String description) {
        this(id, title, genre, runtime, rating, entertainmentScore, posterUrl, description, "", "English");
    }

    public Movie(String title, String genre, int runtime, double rating, int entertainmentScore, String posterUrl, String description) {
        this(title, genre, runtime, rating, entertainmentScore, posterUrl, description, "", "English");
    }

    public Movie(int id, String title, String genre, int runtime, double rating, int entertainmentScore, String posterUrl, String description, String watchUrl, String language) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.runtime = runtime;
        this.rating = rating;
        this.entertainmentScore = entertainmentScore;
        this.posterUrl = posterUrl;
        this.description = description;
        this.watchUrl = watchUrl;
        this.language = language;
    }

    public Movie(String title, String genre, int runtime, double rating, int entertainmentScore, String posterUrl, String description, String watchUrl, String language) {
        this.title = title;
        this.genre = genre;
        this.runtime = runtime;
        this.rating = rating;
        this.entertainmentScore = entertainmentScore;
        this.posterUrl = posterUrl;
        this.description = description;
        this.watchUrl = watchUrl;
        this.language = language;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getEntertainmentScore() { return entertainmentScore; }
    public void setEntertainmentScore(int entertainmentScore) { this.entertainmentScore = entertainmentScore; }

    public String getPosterUrl() { return posterUrl; }
    public String getImdbId() { return imdbId; }
    public String getImdbUrl() { return imdbUrl; }
    public Integer getReleaseYear() { return releaseYear; }
    public String getCountry() { return country; }
    public String getDirector() { return director; }
    public String getCast() { return cast; }
    public String getStreamingPlatform() { return streamingPlatform; }
    public String getPosterLocalPath() { return posterLocalPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getTrailerUrl() { return trailerUrl; }
    public Double getPopularityScore() { return popularityScore; }
    public String getFranchise() { return franchise; }
    public String getMoodTags() { return moodTags; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    public void setImdbUrl(String imdbUrl) { this.imdbUrl = imdbUrl; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public void setCountry(String country) { this.country = country; }
    public void setDirector(String director) { this.director = director; }
    public void setCast(String cast) { this.cast = cast; }
    public void setStreamingPlatform(String streamingPlatform) { this.streamingPlatform = streamingPlatform; }
    public void setPosterLocalPath(String posterLocalPath) { this.posterLocalPath = posterLocalPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public void setPopularityScore(Double popularityScore) { this.popularityScore = popularityScore; }
    public void setFranchise(String franchise) { this.franchise = franchise; }
    public void setMoodTags(String moodTags) { this.moodTags = moodTags; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWatchUrl() { return watchUrl; }
    public void setWatchUrl(String watchUrl) { this.watchUrl = watchUrl; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    @Override
    public String toString() {
        return title + " (" + runtime + "m, ★" + rating + ") [" + language + "]" + (releaseYear != null ? " (" + releaseYear + ")" : "");
    }
}
