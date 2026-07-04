package com.cineplan.service;

import com.cineplan.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MoodEngine {
    
    public enum Mood {
        COMEDY_NIGHT("Comedy Night"),
        MIND_BENDING("Mind Bending"),
        DATE_NIGHT("Date Night"),
        HORROR_MARATHON("Horror Marathon"),
        FANTASY_ADVENTURE("Fantasy Adventure"),
        WEEKEND_RELAXATION("Weekend Relaxation");

        private final String displayName;

        Mood(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Mood fromDisplayName(String name) {
            for (Mood m : values()) {
                if (m.getDisplayName().equalsIgnoreCase(name)) {
                    return m;
                }
            }
            return WEEKEND_RELAXATION; // default fallback
        }
    }

    /**
     * Modifies the entertainment scores of movies dynamically based on the selected mood.
     * Clones movies to prevent modifying database-level state.
     */
    public static List<Movie> applyMoodModifier(List<Movie> originalMovies, String moodStr) {
        Mood mood = Mood.fromDisplayName(moodStr);
        List<Movie> modifiedMovies = new ArrayList<>();

        for (Movie m : originalMovies) {
            Movie copy = new Movie(
                m.getId(),
                m.getTitle(),
                m.getGenre(),
                m.getRuntime(),
                m.getRating(),
                m.getEntertainmentScore(),
                m.getPosterUrl(),
                m.getDescription(),
                m.getWatchUrl(),
                m.getLanguage()
            );

            int bonus = 0;
            switch (mood) {
                case COMEDY_NIGHT:
                    if ("Comedy".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 5;
                    }
                    break;
                case MIND_BENDING:
                    if ("Sci-Fi".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 5;
                    }
                    break;
                case DATE_NIGHT:
                    if ("Drama".equalsIgnoreCase(copy.getGenre()) || "Comedy".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 4;
                    }
                    break;
                case HORROR_MARATHON:
                    if ("Horror".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 6;
                    }
                    break;
                case FANTASY_ADVENTURE:
                    if ("Fantasy".equalsIgnoreCase(copy.getGenre()) || "Animation".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 5;
                    }
                    break;
                case WEEKEND_RELAXATION:
                    if ("Comedy".equalsIgnoreCase(copy.getGenre()) || "Animation".equalsIgnoreCase(copy.getGenre()) || "Drama".equalsIgnoreCase(copy.getGenre())) {
                        bonus = 4;
                    }
                    break;
            }

            int newScore = Math.min(10, copy.getEntertainmentScore() + bonus);
            copy.setEntertainmentScore(newScore);
            modifiedMovies.add(copy);
        }

        return modifiedMovies;
    }
}
