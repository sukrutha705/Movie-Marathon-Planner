package com.cineplan.repository;

import com.cineplan.database.DatabaseManager;
import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MarathonRepository {

    public boolean saveMarathon(Marathon marathon) {
        String insertMarathonSql = "INSERT INTO marathons (marathon_name, mood, total_runtime, total_score, created_at) VALUES (?, ?, ?, ?, ?)";
        String insertMovieLinkSql = "INSERT INTO marathon_movies (marathon_id, movie_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // start transaction

            // 1. Insert Marathon metadata
            try (PreparedStatement pstmt = conn.prepareStatement(insertMarathonSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, marathon.getMarathonName());
                pstmt.setString(2, marathon.getMood());
                pstmt.setInt(3, marathon.getTotalRuntime());
                pstmt.setInt(4, marathon.getTotalScore());
                pstmt.setString(5, marathon.getCreatedAt());
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                int marathonId = 0;
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        marathonId = generatedKeys.getInt(1);
                        marathon.setId(marathonId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

                // 2. Insert movie association links
                try (PreparedStatement linkPstmt = conn.prepareStatement(insertMovieLinkSql)) {
                    for (Movie movie : marathon.getMovies()) {
                        linkPstmt.setInt(1, marathonId);
                        linkPstmt.setInt(2, movie.getId());
                        linkPstmt.addBatch();
                    }
                    linkPstmt.executeBatch();
                }
            }

            conn.commit(); // commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving marathon: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back save transaction: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        return false;
    }

    public List<Marathon> getAllMarathons() {
        List<Marathon> marathons = new ArrayList<>();
        String sql = "SELECT * FROM marathons ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Marathon m = new Marathon(
                    rs.getInt("id"),
                    rs.getString("marathon_name"),
                    rs.getString("mood"),
                    rs.getInt("total_runtime"),
                    rs.getInt("total_score"),
                    rs.getString("created_at")
                );
                // Load associated movies
                m.setMovies(getMoviesForMarathon(m.getId(), conn));
                marathons.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all marathons: " + e.getMessage());
        }
        return marathons;
    }

    public boolean deleteMarathon(int id) {
        String sql = "DELETE FROM marathons WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting marathon: " + e.getMessage());
        }
        return false;
    }

    private List<Movie> getMoviesForMarathon(int marathonId, Connection conn) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT m.* FROM movies m " +
                     "INNER JOIN marathon_movies mm ON m.id = mm.movie_id " +
                     "WHERE mm.marathon_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, marathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("runtime"),
                        rs.getDouble("rating"),
                        rs.getInt("entertainment_score"),
                        rs.getString("poster_url"),
                        rs.getString("description"),
                        rs.getString("watch_url"),
                        rs.getString("language")
                    ));
                }
            }
        }
        return movies;
    }
}
