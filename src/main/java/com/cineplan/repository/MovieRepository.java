package com.cineplan.repository;

import com.cineplan.database.DatabaseManager;
import com.cineplan.model.Movie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class MovieRepository {

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY title ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all movies: " + e.getMessage());
        }
        return movies;
    }

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovie(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching movie by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (title, genre, runtime, rating, entertainment_score, poster_url, description, watch_url, language, imdb_id, imdb_url, release_year, country, director, cast, streaming_platform, poster_local_path, backdrop_path, trailer_url, popularity_score, franchise, mood_tags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setInt(3, movie.getRuntime());
            pstmt.setDouble(4, movie.getRating());
            pstmt.setInt(5, movie.getEntertainmentScore());
            pstmt.setString(6, movie.getPosterUrl());
            pstmt.setString(7, movie.getDescription());
            pstmt.setString(8, movie.getWatchUrl());
            pstmt.setString(9, movie.getLanguage());
            pstmt.setString(10, movie.getImdbId());
            pstmt.setString(11, movie.getImdbUrl());
            if (movie.getReleaseYear() != null) {
                pstmt.setInt(12, movie.getReleaseYear());
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }
            pstmt.setString(13, movie.getCountry());
            pstmt.setString(14, movie.getDirector());
            pstmt.setString(15, movie.getCast());
            pstmt.setString(16, movie.getStreamingPlatform());
            pstmt.setString(17, movie.getPosterLocalPath());
            pstmt.setString(18, movie.getBackdropPath());
            pstmt.setString(19, movie.getTrailerUrl());
            if (movie.getPopularityScore() != null) {
                pstmt.setDouble(20, movie.getPopularityScore());
            } else {
                pstmt.setNull(20, Types.DOUBLE);
            }
            pstmt.setString(21, movie.getFranchise());
            pstmt.setString(22, movie.getMoodTags());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movie.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
        }
        return false;
    }

    public boolean updateMovie(Movie movie) {
        String sql = "UPDATE movies SET title = ?, genre = ?, runtime = ?, rating = ?, entertainment_score = ?, poster_url = ?, description = ?, watch_url = ?, language = ?, imdb_id = ?, imdb_url = ?, release_year = ?, country = ?, director = ?, cast = ?, streaming_platform = ?, poster_local_path = ?, backdrop_path = ?, trailer_url = ?, popularity_score = ?, franchise = ?, mood_tags = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setInt(3, movie.getRuntime());
            pstmt.setDouble(4, movie.getRating());
            pstmt.setInt(5, movie.getEntertainmentScore());
            pstmt.setString(6, movie.getPosterUrl());
            pstmt.setString(7, movie.getDescription());
            pstmt.setString(8, movie.getWatchUrl());
            pstmt.setString(9, movie.getLanguage());
            pstmt.setString(10, movie.getImdbId());
            pstmt.setString(11, movie.getImdbUrl());
            if (movie.getReleaseYear() != null) {
                pstmt.setInt(12, movie.getReleaseYear());
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }
            pstmt.setString(13, movie.getCountry());
            pstmt.setString(14, movie.getDirector());
            pstmt.setString(15, movie.getCast());
            pstmt.setString(16, movie.getStreamingPlatform());
            pstmt.setString(17, movie.getPosterLocalPath());
            pstmt.setString(18, movie.getBackdropPath());
            pstmt.setString(19, movie.getTrailerUrl());
            if (movie.getPopularityScore() != null) {
                pstmt.setDouble(20, movie.getPopularityScore());
            } else {
                pstmt.setNull(20, Types.DOUBLE);
            }
            pstmt.setString(21, movie.getFranchise());
            pstmt.setString(22, movie.getMoodTags());
            pstmt.setInt(23, movie.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating movie: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteMovie(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
        }
        return false;
    }
    /**
    * Checks if a movie with the same title and release year already exists.
    */
    public boolean movieExists(String title, Integer releaseYear) {
        String sql = "SELECT COUNT(*) FROM movies WHERE title = ? AND release_year = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            if (releaseYear != null) {
                pstmt.setInt(2, releaseYear);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking movie existence: " + e.getMessage());
        }
        return false;
    }
    public List<Movie> searchMovies(String query, String genreFilter) {
        List<Movie> movies = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM movies WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String paramVal = "%" + query.trim() + "%";
            params.add(paramVal);
            params.add(paramVal);
        }

        if (genreFilter != null && !genreFilter.trim().isEmpty() && !"All Genres".equalsIgnoreCase(genreFilter)) {
            sql.append(" AND genre = ?");
            params.add(genreFilter);
        }

        sql.append(" ORDER BY title ASC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching movies: " + e.getMessage());
        }
        return movies;
    }

    public List<Movie> searchMoviesWithLanguage(String query, String genreFilter, String languageFilter) {
        List<Movie> movies = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM movies WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String paramVal = "%" + query.trim() + "%";
            params.add(paramVal);
            params.add(paramVal);
        }

        if (genreFilter != null && !genreFilter.trim().isEmpty() && !"All Genres".equalsIgnoreCase(genreFilter)) {
            sql.append(" AND genre = ?");
            params.add(genreFilter);
        }

        if (languageFilter != null && !languageFilter.trim().isEmpty() && !"All Languages".equalsIgnoreCase(languageFilter)) {
            sql.append(" AND language = ?");
            params.add(languageFilter);
        }

        sql.append(" ORDER BY title ASC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching movies: " + e.getMessage());
        }
        return movies;
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setGenre(rs.getString("genre"));
        movie.setRuntime(rs.getInt("runtime"));
        movie.setRating(rs.getDouble("rating"));
        movie.setEntertainmentScore(rs.getInt("entertainment_score"));
        movie.setPosterUrl(rs.getString("poster_url"));
        movie.setDescription(rs.getString("description"));
        movie.setWatchUrl(rs.getString("watch_url"));
        movie.setLanguage(rs.getString("language"));
        movie.setImdbId(rs.getString("imdb_id"));
        movie.setImdbUrl(rs.getString("imdb_url"));
        movie.setReleaseYear(rs.getInt("release_year") == 0 ? null : rs.getInt("release_year"));
        movie.setCountry(rs.getString("country"));
        movie.setDirector(rs.getString("director"));
        movie.setCast(rs.getString("cast"));
        movie.setStreamingPlatform(rs.getString("streaming_platform"));
        movie.setPosterLocalPath(rs.getString("poster_local_path"));
        movie.setBackdropPath(rs.getString("backdrop_path"));
        movie.setTrailerUrl(rs.getString("trailer_url"));
        movie.setPopularityScore(rs.getDouble("popularity_score"));
        movie.setFranchise(rs.getString("franchise"));
        movie.setMoodTags(rs.getString("mood_tags"));
        return movie;
    }
}
