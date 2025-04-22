package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Task;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TaskManager {
    private static final String FILE_PATH = "data/tasks.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void addTask(String username, Task task) {
        Map<String, List<Task>> allTasks = loadAllTasks();
        List<Task> userTasks = allTasks.getOrDefault(username, new ArrayList<>());
        userTasks.add(task);
        allTasks.put(username, userTasks);
        saveAllTasks(allTasks);
    }

    public static ObservableList<Task> getUserTasks(String username) {
        Map<String, List<Task>> allTasks = loadAllTasks();
        List<Task> userTasks = allTasks.getOrDefault(username, new ArrayList<>());
        return FXCollections.observableArrayList(userTasks);
    }
    private static Map<String, List<Task>> loadAllTasks() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                return new HashMap<>();
            }
            FileReader reader = new FileReader(FILE_PATH);
            Type type = new TypeToken<Map<String, List<Task>>>() {}.getType();
            Map<String, List<Task>> allTasks = gson.fromJson(reader, type);
            return allTasks != null ? allTasks : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    private static void saveAllTasks(Map<String, List<Task>> allTasks) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(allTasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveUserTasks(String username, ObservableList<Task> tasks) {
        Map<String, List<Task>> allTasks = loadAllTasks();
        allTasks.put(username, new ArrayList<>(tasks));
        saveAllTasks(allTasks);
    }    
    public static Map<String, Object> generateSummaryReport() {
        Map<String, List<Task>> allTasks = loadAllTasks();
        Map<String, Object> report = new LinkedHashMap<>();
    
        int totalTasks = 0;
        Map<String, Integer> priorityCount = new HashMap<>();
        Map<String, Integer> statusCount = new HashMap<>();
    
        for (List<Task> taskList : allTasks.values()) {
            totalTasks += taskList.size();
            for (Task task : taskList) {
                priorityCount.merge(task.getPriority(), 1, Integer::sum);
                statusCount.merge(task.getStatus(), 1, Integer::sum);
            }
        }
    
        String mostCommonPriority = priorityCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    
        String mostCommonStatus = statusCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    
        report.put("Total Users", allTasks.keySet().size());
        report.put("Total Tasks", totalTasks);
        report.put("Most Frequent Priority", mostCommonPriority);
        report.put("Most Common Status", mostCommonStatus);
        report.put("Deactivated Users", utils.UserManager.getDeactivatedUserCount());

    
        return report;
    }
    public static void saveReportToFile(Map<String, Object> report, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Map.Entry<String, Object> entry : report.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    public static void deleteUserTasks(String username) {
        Map<String, List<Task>> allTasks = loadAllTasks();
        allTasks.remove(username);
        saveAllTasks(allTasks);
    }
}
