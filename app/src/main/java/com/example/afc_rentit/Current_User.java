package com.example.afc_rentit;

import androidx.annotation.NonNull;

public class Current_User {
    private static Current_User instance;
    private Current_User(){};
    private int user_id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String address;
    private String gender;
    private boolean isOwner;
    public static Current_User getInstance(){
        if (instance == null){
            instance = new Current_User();
        }

        return instance;
    }

    public void resetCurrent_User(){
        instance = null;
    }
    public void setCurrent_User(int user_id, String username, String firstname, String lastname,
                                String email, String address, String gender, boolean isOwner) {

        this.user_id = user_id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.isOwner = isOwner;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
    @NonNull
    @Override
    public String toString() {
        return "user_id: " + user_id +
                "\nusername: " + username;
    }
}
