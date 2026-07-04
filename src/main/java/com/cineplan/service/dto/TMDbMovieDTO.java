package com.cineplan.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TMDbMovieDTO {
    private int id;
    private String title;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
    @JsonProperty("original_language")
    private String originalLanguage;
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("vote_average")
    private double voteAverage;
    @JsonProperty("vote_count")
    private int voteCount;
    private double popularity;
    @JsonProperty("imdb_id")
    private String imdbId;
    @JsonProperty("origin_country")
    private List<String> originCountry;
    // Additional fields that we may want to expose
    private String tagline;
    private String status;
    private String homepage;
    private String runtime; // may be string in some API responses

    // Getters and Setters (generated)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public double getPopularity() { return popularity; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    public List<String> getOriginCountry() { return originCountry; }
    public void setOriginCountry(List<String> originCountry) { this.originCountry = originCountry; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    public String getRuntime() { return runtime; }
    public void setRuntime(String runtime) { this.runtime = runtime; }

    /**
     * Helper to turn genre IDs into a human‑readable, comma separated string.
     * In a full implementation you would query TMDb genre list once and cache it.
     * Here we simply return the IDs as a placeholder.
     */
    public String getGenreNames() {
        if (genreIds == null || genreIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++) {
            sb.append("Genre" + genreIds.get(i)); // placeholder
            if (i < genreIds.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * Build a watch URL – for now we just return an empty string; real implementation
     * could construct a Netflix/Prime link based on movie title.
     */
    public String getWatchUrl() {
        return "";
    }
}
