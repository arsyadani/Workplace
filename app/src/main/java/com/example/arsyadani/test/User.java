package com.example.arsyadani.test;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 29-Jan-18.
 */

public class User {
    public String id;
    public String email; // masuk
    public String telp; // masuk
    public String username; // masuk
    public String password; // masuk
    public String tipe; // tidak masuk
    public String foto; // masuk
    public ArrayList<String> favorit;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String id, String email, String telp, String username, String password, String tipe) {
        this.id = id;
        this.email = email;
        this.telp = telp;
        this.username = username;
        this.password = password;
        this.tipe = tipe;
    }

    public User(String email, String telp, String username, String password, String tipe) {
        this.email = email;
        this.telp = telp;
        this.username = username;
        this.password = password;
        this.tipe = tipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public ArrayList<String> getFavorit() {
        return favorit;
    }

    public void setFavorit(ArrayList<String> favorit) {
        this.favorit = favorit;
    }
}
