package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {
    private static final String FILE_PATH = "data/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String currentUser;
    private static Map<String, User> users = loadUsers();

    public static boolean authenticate(String username, String password) {
        User user = users.get(username);
    
        if (user != null &&
            user.getPassword().equals(password) &&
            user.isActive()) {
    
            setCurrentUser(username);
    
            user.setLastLogin(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    
            saveUsers(users);
            return true;
        }
    
        return false;
    }
    

    public static boolean register(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) return false;
        if (username.contains(" ")) return false;  

        if (users.containsKey(username)) return false;

        if (password.length() <= 4) return false;
    
        users.put(username, new User(password, "user")); 
        saveUsers(users);
        return true;
    }
    

    public static String getRole(String username) {
        User user = users.get(username);
        return user != null ? user.getRole() : "user";
    }

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    private static Map<String, User> loadUsers() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) return new HashMap<>();
            FileReader reader = new FileReader(FILE_PATH);
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            Map<String, User> loadedUsers = gson.fromJson(reader, type);
            return loadedUsers != null ? loadedUsers : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static void saveUsers(Map<String, User> users) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getLastLogin(String username) {
    User user = users.get(username);
    return user != null ? user.getLastLogin() : "N/A";
    }

    public static void deactivateUser(String username) {
        if (users.containsKey(username)) {
            users.get(username).setActive(false);
            saveUsers(users);
        }
    }
    public static void reactivateUser(String username) {
        if (users.containsKey(username)) {
            users.get(username).setActive(true);
            saveUsers(users);
        }
    }
    

    public static List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet());
    }
    public static int getDeactivatedUserCount() {
        int count = 0;
        for (User user : users.values()) {
            if (!user.isActive()) {
                count++;
            }
        }
        return count;
    }
    public static void deleteUser(String username) {
        users.remove(username);
        saveUsers(users);
    }
    public static boolean exists(String username) {
        return users.containsKey(username);
    }
    
    public static boolean isActive(String username) {
        User user = users.get(username);
        return user != null && user.isActive();
    }
    public static int getUserCount() {
        return users.size(); 
    }
}
