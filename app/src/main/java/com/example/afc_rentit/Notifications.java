package com.example.afc_rentit;

public class Notifications {
    private int id;
    private int userId;
    private String message;

    public Notifications(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
