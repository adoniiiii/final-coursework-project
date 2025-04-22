package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import utils.TaskManager;
import utils.UserManager;

import java.util.*;

public class AdminController {

    @FXML private TableView<String> userTable;
    @FXML private TableColumn<String, String> usernameCol;
    @FXML private TableColumn<String, String> taskCountCol;
    @FXML private TableColumn<String, String> lastLoginCol;

    @FXML private Button deleteUserBtn;
    @FXML private Button deactivateUserBtn;
    @FXML private Button reactivateUserBtn;
    @FXML private Button reportBtn;
    @FXML private Button logoutBtn;
    @FXML private Label feedbackLabel;


    @FXML
    public void initialize() {
        ObservableList<String> users = FXCollections.observableArrayList(UserManager.getAllUsernames());
        userTable.setItems(users);

        usernameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
        taskCountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf(TaskManager.getUserTasks(data.getValue()).size())
        ));
        lastLoginCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            UserManager.getLastLogin(data.getValue())
        ));
    }

    @FXML
    private void handleDeleteUser() {
        String selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            feedbackLabel.setText("Please select a user to delete.");
            return;
        }
        if (selected.equals("admin")) {
            feedbackLabel.setText("Can not delete admin(>⩊<).");
            return;
        }
    
        UserManager.deleteUser(selected);
        TaskManager.deleteUserTasks(selected);
        userTable.getItems().remove(selected);
        feedbackLabel.setText("User '" + selected + "' deleted⊹₊⋆ .");
    }
    

    @FXML
    private void handleDeactivateUser() {
        String selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            feedbackLabel.setText("Please select a user to deactivate.");
            return;
        }
        if (selected.equals("admin")) {
            feedbackLabel.setText("Can not deactivate admin(¯▿¯).");
            return;
        }
    
        UserManager.deactivateUser(selected);
        feedbackLabel.setText("User '" + selected + "' deactivated (✧∀✧)/.");
    }
    @FXML
    private void handleReactivateUser() {
        String selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            feedbackLabel.setText("Please select a user to reactivate.");
            return;
        }
        if (selected.equals("admin")) {
            feedbackLabel.setText("Admin is always active ☆(>ᴗ•).");
            return;
        }
        if (UserManager.isActive(selected)) {
            feedbackLabel.setText("User '" + selected + "' is already active (^ ^)ノ.");
            return;
        }
        UserManager.reactivateUser(selected);
        feedbackLabel.setText("User '" + selected + "' reactivated ");
    }


    @FXML
    private void handleGenerateReport() {
        Map<String, Object> report = TaskManager.generateSummaryReport();
        TaskManager.saveReportToFile(report, "data/report.txt");

        StringBuilder content = new StringBuilder();
        report.forEach((k, v) -> content.append(k).append(": ").append(v).append("\n"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText("Summary Report");
        alert.setContentText(content.toString());
        alert.getDialogPane().setStyle("-fx-background-color: #eeeeee; -fx-font-family: 'Century Gothic'; -fx-font-size: 15px;");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        utils.SceneManager.switchScene("LoginView.fxml");
    }

}
