package com.cineplan.repository;

import com.cineplan.database.DatabaseManager;
import com.cineplan.model.Achievement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AchievementRepository {

    public List<Achievement> getAllAchievements() {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT * FROM achievements ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Achievement(
                    rs.getInt("id"),
                    rs.getString("achievement_name"),
                    rs.getString("description"),
                    rs.getInt("unlocked") == 1
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching achievements: " + e.getMessage());
        }
        return list;
    }

    public boolean unlockAchievement(int id) {
        String sql = "UPDATE achievements SET unlocked = 1 WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unlocking achievement: " + e.getMessage());
        }
        return false;
    }

    public boolean unlockAchievementByName(String name) {
        String sql = "UPDATE achievements SET unlocked = 1 WHERE achievement_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unlocking achievement by name: " + e.getMessage());
        }
        return false;
    }
}
