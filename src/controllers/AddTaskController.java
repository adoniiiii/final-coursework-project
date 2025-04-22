package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

import models.Task;
import utils.TaskManager;

public class AddTaskController {

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dueDateField;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private Button saveBtn;

    @FXML
    private Label feedbackLabel;


    private Task editingTask = null;

    private TableView<Task> taskTable;

    public void setTaskTable(TableView<Task> taskTable) {
        this.taskTable = taskTable;
    }
    private String username;
    public void setUsername(String username) {
        this.username = username;
    }

    public void prefillTask(Task task) {
        editingTask = task;
        descriptionField.setText(task.getDescription());
        priorityCombo.setValue(task.getPriority());
    
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            dueDateField.setValue(LocalDate.parse(task.getDueDate()));
        }
    }
    
    @FXML
    public void initialize() {
        priorityCombo.getItems().addAll("Urgent", "High", "Normal", "Low");

        dueDateField.getEditor().setDisable(true);
        dueDateField.getEditor().setOpacity(1);

        dueDateField.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });
    }


    @FXML
    private void handleSave() {
        String desc = descriptionField.getText();
        String due = dueDateField.getValue() != null ? dueDateField.getValue().toString() : "";
        String priority = priorityCombo.getValue();
        
        if (desc.isEmpty() || due.isEmpty() || priority == null) {
            feedbackLabel.setText("Please fill out all required fields.");
            return;
        }
        
        if (editingTask != null) {
            editingTask.setDescription(desc);
            editingTask.setDueDate(due);
            editingTask.setPriority(priority);

        } else {

            Task task = new Task(desc, due, priority, "To-Do");
            taskTable.getItems().add(task);
        }

        if (username != null) {

            TaskManager.saveUserTasks(username, taskTable.getItems());
        }
        
        taskTable.refresh();

        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
