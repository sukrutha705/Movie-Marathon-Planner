package com.cineplan.service;

import com.cineplan.model.Achievement;
import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import com.cineplan.repository.AchievementRepository;
import java.util.ArrayList;
import java.util.List;

public class AchievementService {
    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    /**
     * Evaluates a generated marathon for achievements and updates the database.
     * Returns a list of achievements that were newly unlocked.
     */
    public List<Achievement> checkAndUnlockMarathonAchievements(Marathon marathon) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        List<Achievement> allAchievements = achievementRepository.getAllAchievements();

        // 1. Weekend Warrior: total_runtime > 300 minutes
        if (marathon.getTotalRuntime() > 300) {
            triggerUnlock("Weekend Warrior", allAchievements, newlyUnlocked);
        }

        // 2. Marvel Addict: at least 3 Action movies in one marathon
        int actionCount = 0;
        for (Movie m : marathon.getMovies()) {
            if ("Action".equalsIgnoreCase(m.getGenre())) {
                actionCount++;
            }
        }
        if (actionCount >= 3) {
            triggerUnlock("Marvel Addict", allAchievements, newlyUnlocked);
        }

        // 3. Cinema God: total score >= 40
        if (marathon.getTotalScore() >= 40) {
            triggerUnlock("Cinema God", allAchievements, newlyUnlocked);
        }

        // 4. Horror Survivor: mood is 'Horror Marathon'
        if ("Horror Marathon".equalsIgnoreCase(marathon.getMood())) {
            triggerUnlock("Horror Survivor", allAchievements, newlyUnlocked);
        }

        // 5. Sci-Fi Explorer: mood is 'Mind Bending'
        if ("Mind Bending".equalsIgnoreCase(marathon.getMood())) {
            triggerUnlock("Sci-Fi Explorer", allAchievements, newlyUnlocked);
        }

        return newlyUnlocked;
    }

    private void triggerUnlock(String name, List<Achievement> all, List<Achievement> newlyUnlocked) {
        for (Achievement ach : all) {
            if (ach.getName().equalsIgnoreCase(name)) {
                if (!ach.isUnlocked()) {
                    achievementRepository.unlockAchievement(ach.getId());
                    ach.setUnlocked(true);
                    newlyUnlocked.add(ach);
                }
                break;
            }
        }
    }
}
