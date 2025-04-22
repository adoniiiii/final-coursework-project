package models;

public class User {
    private String password;
    private String role;
    private String lastLogin;
    private boolean active;

    public User() {}

    public User(String password, String role) {
        this.password = password;
        this.role = role;
        this.lastLogin = "";
        this.active = true;
    }

    // Getters & setters
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getLastLogin() { return lastLogin; }
    public boolean isActive() { return active; }

    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    public void setActive(boolean active) { this.active = active; }
}
