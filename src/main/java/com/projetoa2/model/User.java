package com.projetoa2.model;

public class User {
    private int id;
    private String username;
    private String password;
    private boolean admin;

    public User(int id, String username, String password, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return admin; }

    public String serialize() {
        return id + "|" + username + "|" + password + "|" + (admin?"1":"0");
    }

    public static User deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|");
        try {
            int id = Integer.parseInt(parts[0]);
            String username = parts[1];
            String password = parts[2];
            boolean admin = parts.length>3 && parts[3].equals("1");
            return new User(id, username, password, admin);
        } catch (Exception e) {
            return null;
        }
    }
}
