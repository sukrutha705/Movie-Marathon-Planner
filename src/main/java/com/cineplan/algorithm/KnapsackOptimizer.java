package com.cineplan.algorithm;

import com.cineplan.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class KnapsackOptimizer {

    private static class DPCell {
        int score;      // Total entertainment score (primary)
        double rating;  // Sum of IMDb ratings (secondary)
        int runtime;    // Total runtime (tertiary - want to minimize)

        DPCell(int score, double rating, int runtime) {
            this.score = score;
            this.rating = rating;
            this.runtime = runtime;
        }

        boolean isBetterThan(DPCell other) {
            if (this.score != other.score) {
                return this.score > other.score;
            }
            if (Math.abs(this.rating - other.rating) > 0.001) {
                return this.rating > other.rating;
            }
            return this.runtime < other.runtime; // Prefer shorter duration
        }
    }

    /**
     * Solves the 0/1 Knapsack problem for movie selection with tie-breaking optimization.
     * 
     * @param movies The list of available movies to select from.
     * @param availableMinutes The capacity of the knapsack (user's available time).
     * @return List of selected movies that fit within available time and maximize entertainment score.
     */
    public static List<Movie> optimize(List<Movie> movies, int availableMinutes) {
        if (movies == null || movies.isEmpty() || availableMinutes <= 0) {
            return new ArrayList<>();
        }

        int n = movies.size();
        int W = availableMinutes;
        
        // 1D DP array representing optimal subproblem states
        DPCell[] dp = new DPCell[W + 1];
        for (int j = 0; j <= W; j++) {
            dp[j] = new DPCell(0, 0.0, 0);
        }

        // keep[i][j] is true if movie at index i-1 was included at capacity j
        boolean[][] keep = new boolean[n + 1][W + 1];

        // Fill the DP table
        for (int i = 1; i <= n; i++) {
            Movie movie = movies.get(i - 1);
            int w = movie.getRuntime();
            int v = movie.getEntertainmentScore();
            double r = movie.getRating();

            for (int j = W; j >= w; j--) {
                DPCell optionWithout = dp[j];
                DPCell optionWith = new DPCell(dp[j - w].score + v, dp[j - w].rating + r, dp[j - w].runtime + w);

                if (optionWith.isBetterThan(optionWithout)) {
                    dp[j] = optionWith;
                    keep[i][j] = true;
                } else {
                    keep[i][j] = false;
                }
            }
        }

        // Backtrack to identify selected movies
        List<Movie> selectedMovies = new ArrayList<>();
        int j = W;
        for (int i = n; i > 0; i--) {
            if (keep[i][j]) {
                Movie movie = movies.get(i - 1);
                selectedMovies.add(movie);
                j -= movie.getRuntime();
            }
        }

        return selectedMovies;
    }
}
