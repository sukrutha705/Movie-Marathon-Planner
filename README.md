# 🎬 CinePlan - Movie Marathon Planner

> A JavaFX-based Movie Marathon Planner that uses the **0/1 Knapsack Algorithm** to recommend the best combination of movies within a user's available time while maximizing entertainment value.

---

## 📖 Overview

CinePlan is a Design and Analysis of Algorithms (DAA) project that demonstrates the practical application of the **0/1 Knapsack Algorithm** in solving a real-world optimization problem.

Instead of manually deciding which movies to watch, users simply specify the amount of free time they have, and CinePlan intelligently recommends the optimal set of movies that maximizes entertainment while staying within the available time.

The application combines algorithmic optimization, database management, API integration, and a modern JavaFX user interface.

---

## ✨ Features

- 🎥 Smart Movie Recommendation using the 0/1 Knapsack Algorithm
- ⏱️ Optimize movie selection based on available time
- ⭐ Uses entertainment score and IMDb ratings for intelligent recommendations
- 📊 Analytics Dashboard
- 🏆 Achievement Tracking
- 🔍 Movie Discovery
- 📄 Export marathon details to PDF
- 💾 SQLite database integration
- 🌐 TMDb API integration for movie information
- 🎨 Modern JavaFX User Interface

---

# 🧠 Algorithm Used

## 0/1 Knapsack Algorithm

The core functionality of CinePlan is powered by the **0/1 Knapsack Dynamic Programming Algorithm**.

### Problem Mapping

| Knapsack Problem | CinePlan |
|-----------------|----------|
| Weight | Movie Runtime |
| Value | Entertainment Score |
| Capacity | User's Available Time |
| Item | Movie |

The objective is to maximize the total entertainment score while ensuring the combined runtime does not exceed the user's available time.

### Additional Optimizations

When multiple movie combinations produce the same entertainment score, CinePlan further optimizes by:

- Higher cumulative IMDb rating
- Lower total runtime

This makes recommendations more practical and enjoyable.

---

# 🏗️ Project Architecture

```
User
   │
   ▼
JavaFX UI
   │
   ▼
Controllers
   │
   ▼
Services
   │
   ▼
Knapsack Optimizer
   │
   ▼
SQLite Database
        │
        ▼
     TMDb API
```

---

# 🛠️ Tech Stack

### Programming Language

- Java 21

### Frameworks

- JavaFX

### Database

- SQLite

### Build Tool

- Maven

### External API

- TMDb (The Movie Database)

### Libraries

- SQLite JDBC
- OpenPDF
- JavaFX Controls
- JavaFX FXML

---

# 📂 Project Structure

```
src
│
├── main
│   ├── java
│   │   └── com.cineplan
│   │       ├── algorithm
│   │       ├── controller
│   │       ├── database
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       └── utils
│   │
│   └── resources
│
├── pom.xml
└── cineplan.db
```

---

# 🚀 Getting Started

## Prerequisites

- Java JDK 21
- Maven
- Git

---

## Clone Repository

```bash
git clone https://github.com/your-username/cineplan.git
```

---

## Navigate to Project

```bash
cd cineplan
```

---

## Build Project

```bash
mvn clean install
```

---

## Run

```bash
mvn javafx:run
```

---

# 📊 Dynamic Programming Approach

The algorithm creates an optimized Dynamic Programming table where:

- Rows represent movies
- Columns represent available time
- Each state stores:
  - Entertainment Score
  - IMDb Rating
  - Runtime

After filling the DP table, the algorithm backtracks to determine the optimal movie selection.

### Time Complexity

```
O(n × W)
```

Where

- **n** = Number of movies
- **W** = Available viewing time

### Space Complexity

```
O(n × W)
```

---

# 📸 Screenshots

Add screenshots of:

- Home Screen
- Movie Discovery
- Dashboard
- Movie Recommendation
- Analytics
- Achievement Page

Example:

```
screenshots/
    home.png
    dashboard.png
    optimizer.png
    analytics.png
```

---

# 🎯 Learning Outcomes

This project demonstrates practical implementation of:

- Dynamic Programming
- 0/1 Knapsack Algorithm
- Algorithm Optimization
- JavaFX Application Development
- MVC Architecture
- SQLite Database Integration
- API Integration
- Software Engineering Principles

---

# 👨‍💻 Contributors

- Sukrutha K
- NeuralNexus Team

---

# 📚 Future Improvements

- Personalized AI-based recommendations
- Multiple optimization strategies
- Genre balancing
- Collaborative watch planning
- Cloud database support
- User authentication
- Streaming platform integration
- Recommendation history
- Watchlist synchronization

---

# 📜 License

This project was developed as part of a **Design and Analysis of Algorithms (DAA)** academic project.

Feel free to fork, modify, and learn from it for educational purposes.

---

# ⭐ If you found this project useful

Give the repository a ⭐ on GitHub!

It helps support the project and motivates future improvements.
