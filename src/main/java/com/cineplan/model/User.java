package com.cineplan.model;

public class User {
    private int id;
    private String username;
    private String favoriteGenre;

    public User() {}

    public User(int id, String username, String favoriteGenre) {
        this.id = id;
        this.username = username;
        this.favoriteGenre = favoriteGenre;
    }

    public User(String username, String favoriteGenre) {
        this.username = username;
        this.favoriteGenre = favoriteGenre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFavoriteGenre() { return favoriteGenre; }
    public void setFavoriteGenre(String favoriteGenre) { this.favoriteGenre = favoriteGenre; }
}
