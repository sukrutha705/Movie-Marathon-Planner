package com.cineplan.model;

public class Achievement {
    private int id;
    private String name;
    private String description;
    private boolean unlocked;

    public Achievement() {}

    public Achievement(int id, String name, String description, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
    }

    public Achievement(String name, String description, boolean unlocked) {
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
