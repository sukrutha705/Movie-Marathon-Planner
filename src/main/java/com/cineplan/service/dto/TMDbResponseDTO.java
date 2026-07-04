package com.cineplan.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TMDbResponseDTO {
    private int page;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_results")
    private int totalResults;
    private List<TMDbMovieDTO> results;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    public List<TMDbMovieDTO> getResults() { return results; }
    public void setResults(List<TMDbMovieDTO> results) { this.results = results; }
}
