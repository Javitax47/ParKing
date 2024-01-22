package com.example.parking;
// User.java
// User.java

public class User {
    private String name;
    private String email;
    private String password;

    private String ProfilePicture;

    // Constructor vacío requerido por Firebase Realtime Database
    public User(String newName, String newPassword) {
    }

    public User(String name, String email, String password /*String ProfilePicture*/) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.ProfilePicture = ProfilePicture;
    }


    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getProfilePicturePath() {
        return ProfilePicture;
    }

    public void setProfilePicturePath(String ProfilePicture) {
        this.ProfilePicture = ProfilePicture;
    }
}
