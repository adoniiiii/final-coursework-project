package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Task;
import utils.TaskManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MainController {

    @FXML
    private Label greetingLabel;

    @FXML
    private Button addTaskBtn;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> taskCol;

    @FXML
    private TableColumn<Task, String> priorityCol;

    @FXML
    private TableColumn<Task, String> dueDateCol;

    @FXML
    private TableColumn<Task, String> statusCol;

    
    @FXML
    private void showEditConfirmation(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Task Options");
        alert.setHeaderText("What do you want to do with this task?");
        alert.setContentText(task.getDescription());
    
        ButtonType updateBtn = new ButtonType("Update");
        ButtonType deleteBtn = new ButtonType("Delete");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(updateBtn, deleteBtn, cancelBtn);
        alert.getDialogPane().setStyle("-fx-font-family: 'Century Gothic'; -fx-background-color: #eeeeee; -fx-font-size: 15px;");
        alert.showAndWait().ifPresent(response -> {
            if (response == updateBtn) {
                openEditTaskDialog(task);
            } else if (response == deleteBtn) {
                deleteTask(task);
            }
        });
    }
    public void setUser(String username, String role) {
        this.username = username;
        greetingLabel.setText("Hi, " + username + "!");
    
        taskTable.setItems(TaskManager.getUserTasks(username)); 
    }    
    
    private void deleteTask(Task task) {
        taskTable.getItems().remove(task);
        TaskManager.saveUserTasks(username, taskTable.getItems());
    }

    private void openEditTaskDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTaskView.fxml"));
            Parent root = loader.load();
    
            AddTaskController controller = loader.getController();
            controller.setTaskTable(taskTable);
            controller.setUsername(username);
            controller.prefillTask(task); 
    
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Task");
            stage.showAndWait();
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void initialize() {
        taskCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        taskTable.setEditable(true);
        
        dueDateCol.setCellFactory(_ -> new TableCell<Task, String>() {
            private final DatePicker datePicker = new DatePicker();
        
            {
                datePicker.setOnAction(_ -> {
                    Task task = getTableView().getItems().get(getIndex());
                    LocalDate selectedDate = datePicker.getValue();
                    if (selectedDate != null) {
                        task.setDueDate(selectedDate.toString());
                        TaskManager.saveUserTasks(username, taskTable.getItems()); 
                    }
                });
        
                datePicker.getEditor().setDisable(true);
                datePicker.getEditor().setOpacity(1);
        
                datePicker.setDayCellFactory(_ -> new DateCell() {
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
        
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    try {
                        datePicker.setValue(item != null && !item.isEmpty() ? LocalDate.parse(item) : null);
                    } catch (DateTimeParseException e) {
                        datePicker.setValue(null);
                    }
                    setGraphic(datePicker);
                }
            }
        });        

        priorityCol.setCellFactory(ComboBoxTableCell.forTableColumn("Urgent", "High", "Normal", "Low"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("To-Do", "In Progress", "Done"));

        dueDateCol.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setDueDate(event.getNewValue());
            TaskManager.saveUserTasks(username, taskTable.getItems());
        });

        priorityCol.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setPriority(event.getNewValue());
            TaskManager.saveUserTasks(username, taskTable.getItems());

        });

        statusCol.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setStatus(event.getNewValue());
            TaskManager.saveUserTasks(username, taskTable.getItems());

        });
        taskTable.setRowFactory(_ -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Task clickedTask = row.getItem();
        
                    int columnIndex = taskTable.getSelectionModel().getSelectedCells().get(0).getColumn();
                    if (columnIndex == taskCol.getTableView().getColumns().indexOf(taskCol)) {
                        showEditConfirmation(clickedTask);
                    }
                }
            });
            return row;
        });
        

    }

    private String username;
    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTaskView.fxml"));
            Parent root = loader.load();

            AddTaskController controller = loader.getController();
            controller.setTaskTable(taskTable);
            controller.setUsername(username);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Task");
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        utils.UserManager.setCurrentUser(null);
        utils.SceneManager.switchScene("LoginView.fxml");
    }

}
