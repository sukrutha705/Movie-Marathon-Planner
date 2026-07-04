package com.cineplan.controller;

import com.cineplan.model.User;
import com.cineplan.repository.UserRepository;
import com.cineplan.utils.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class QuizController {

    @FXML private VBox cardQuiz;
    @FXML private Label lblQuestionNum;
    @FXML private Label lblQuestionText;
    @FXML private Button btnOptionA;
    @FXML private Button btnOptionB;

    @FXML private VBox cardResult;
    @FXML private Label lblArchetypeTitle;
    @FXML private Label lblArchetypeDesc;
    @FXML private Label lblFavGenreInfo;

    private int currentQuestionIndex = 0;
    
    // Quiz Questions and Answers
    private final String[] questions = {
        "What is your preferred morning beverage?",
        "What is your dream vacation destination?",
        "When do you feel most active and creative?",
        "Which of these hobbies speaks to you more?"
    };

    private final String[][] options = {
        {"Coffee (Strong & Energizing)", "Tea (Calm & Reflective)"},
        {"Sunny Beach (Relaxing & Fun)", "Foggy Mountains (Mysterious & Quiet)"},
        {"Bright Day (Structured & Social)", "Quiet Night (Solitary & Deep)"},
        {"Reading Books (Immersive & Story-driven)", "Watching Movies (Visual & Fast-paced)"}
    };

    // Genre scoring
    private final Map<String, Integer> genreScores = new HashMap<>();
    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        resetQuizState();
        showQuestion();
    }

    private void resetQuizState() {
        currentQuestionIndex = 0;
        genreScores.clear();
        genreScores.put("Action", 0);
        genreScores.put("Comedy", 0);
        genreScores.put("Sci-Fi", 0);
        genreScores.put("Fantasy", 0);
        genreScores.put("Horror", 0);
        genreScores.put("Drama", 0);
        genreScores.put("Animation", 0);

        cardQuiz.setVisible(true);
        cardQuiz.setManaged(true);
        cardResult.setVisible(false);
        cardResult.setManaged(false);
    }

    private void showQuestion() {
        lblQuestionNum.setText("Question " + (currentQuestionIndex + 1) + " of 4");
        lblQuestionText.setText(questions[currentQuestionIndex]);
        btnOptionA.setText(options[currentQuestionIndex][0]);
        btnOptionB.setText(options[currentQuestionIndex][1]);
    }

    @FXML
    private void handleOptionA() {
        recordChoice(0);
        advanceQuiz();
    }

    @FXML
    private void handleOptionB() {
        recordChoice(1);
        advanceQuiz();
    }

    private void recordChoice(int optionIndex) {
        // Simple heuristic rules for genre weights
        if (currentQuestionIndex == 0) { // Coffee vs Tea
            if (optionIndex == 0) { // Coffee
                addScore("Action", 3);
                addScore("Sci-Fi", 2);
            } else { // Tea
                addScore("Drama", 3);
                addScore("Fantasy", 2);
            }
        } else if (currentQuestionIndex == 1) { // Beach vs Mountains
            if (optionIndex == 0) { // Beach
                addScore("Comedy", 3);
                addScore("Animation", 2);
            } else { // Mountains
                addScore("Horror", 3);
                addScore("Sci-Fi", 2);
            }
        } else if (currentQuestionIndex == 2) { // Day vs Night
            if (optionIndex == 0) { // Day
                addScore("Comedy", 2);
                addScore("Animation", 3);
            } else { // Night
                addScore("Horror", 3);
                addScore("Sci-Fi", 2);
            }
        } else if (currentQuestionIndex == 3) { // Books vs Movies
            if (optionIndex == 0) { // Books
                addScore("Drama", 3);
                addScore("Fantasy", 3);
            } else { // Movies
                addScore("Action", 3);
                addScore("Comedy", 1);
            }
        }
    }

    private void addScore(String genre, int points) {
        genreScores.put(genre, genreScores.getOrDefault(genre, 0) + points);
    }

    private void advanceQuiz() {
        currentQuestionIndex++;
        if (currentQuestionIndex < 4) {
            showQuestion();
        } else {
            showResult();
        }
    }

    private void showResult() {
        cardQuiz.setVisible(false);
        cardQuiz.setManaged(false);
        cardResult.setVisible(true);
        cardResult.setManaged(true);

        // Find genre with highest score
        String recommendedGenre = "Drama"; // default fallback
        int maxScore = -1;
        for (Map.Entry<String, Integer> entry : genreScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                recommendedGenre = entry.getKey();
            }
        }

        // Map recommended genre to personality archetype
        String archetypeTitle;
        String archetypeDesc;
        switch (recommendedGenre) {
            case "Action":
                archetypeTitle = "Action Enthusiast";
                archetypeDesc = "You crave high adrenaline, high-stakes thrills, heroic showdowns, and non-stop movement!";
                break;
            case "Comedy":
                archetypeTitle = "Comedy Lover";
                archetypeDesc = "You believe laughter is the best medicine. You enjoy lighthearted fun, sharp humor, and feel-good stories!";
                break;
            case "Sci-Fi":
                archetypeTitle = "Sci-Fi Explorer";
                archetypeDesc = "You love questioning reality, pondering futuristic tech, and exploring deep philosophical concept spaces.";
                break;
            case "Fantasy":
                archetypeTitle = "Fantasy Adventurer";
                archetypeDesc = "You have a vivid imagination, drawn to magical realms, epic world-saving quests, and mythical lore.";
                break;
            case "Horror":
                archetypeTitle = "Horror Survivor";
                archetypeDesc = "You love facing fear head-on! Creepy atmospheres, jumpscares, and survival stories keep you on your toes.";
                break;
            case "Animation":
                archetypeTitle = "Animation Admirer";
                archetypeDesc = "You appreciate creative styling, emotional depths, and the limitless boundaries of animated storytelling.";
                break;
            case "Drama":
            default:
                archetypeTitle = "Drama Scholar";
                archetypeDesc = "You value rich character studies, realistic human relationships, and emotional, thought-provoking storylines.";
                break;
        }

        lblArchetypeTitle.setText(archetypeTitle);
        lblArchetypeDesc.setText(archetypeDesc);
        lblFavGenreInfo.setText("Recommended Genre: " + recommendedGenre);
    }

    @FXML
    private void handleApplyRecommendation() {
        // Extract the recommended genre from the label
        String text = lblFavGenreInfo.getText();
        String recommendedGenre = text.substring(text.indexOf(":") + 1).trim();

        // Update in SQLite
        User user = userRepository.getUser();
        if (user != null) {
            user.setFavoriteGenre(recommendedGenre);
            boolean updated = userRepository.updateUser(user);
            if (updated) {
                AlertHelper.showInfo("Preferences Saved", "Your favorite genre has been set to: " + recommendedGenre);
            }
        }

        // Switch to dashboard and pre-select the genre!
        DashboardController dashboard = MainController.getInstance().showViewAndGetController("dashboard.fxml");
        if (dashboard != null) {
            // Find and set the genre combobox value using a reflection or standard property lookup if we export it.
            // Since we get the controller instance, we can configure a helper method or let the controller load it from DB!
            // Wait, inside DashboardController we initialize it. If we load the favorite genre from user DB, it would automatically pre-select it!
            // Yes! Let's ensure DashboardController reads the favorite genre from DB if initialized.
            // In DashboardController, we can query userRepository.getUser().getFavoriteGenre() and set the value of comboGenre to it.
            // Let's check how DashboardController works. Yes! We can query the user and set it.
            // Wait, we can navigate directly, and since DashboardController reads user preferences, it will display correctly.
        }
        
        MainController.getInstance().showView("dashboard.fxml");
    }

    @FXML
    private void handleRetakeQuiz() {
        resetQuizState();
        showQuestion();
    }
}
