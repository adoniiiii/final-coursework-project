package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import utils.SceneManager;
import utils.UserManager;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        boolean authenticated = UserManager.authenticate(username, password);
        if (authenticated) {
            String role = UserManager.getRole(username);
            messageLabel.setText("");
            SceneManager.switchToMain(username, role);
        } else {
            if (!UserManager.exists(username)) {
                messageLabel.setText("User does not exist.");
            } else if (!UserManager.isActive(username)) {
                messageLabel.setText("Account is deactivated.");
            } else {
                messageLabel.setText("Incorrect password.");
            }
        }
    }        

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter a username and password to register.");
            return;
        }
        if (password.length() <= 4) {
            messageLabel.setText("Password must be at least 5 characters.");
            return;
        }
        
        if (username.contains(" ")) {
            messageLabel.setText("Username cannot contain spaces.");
            return;
        }
        
        boolean success = UserManager.register(username, password);
        if (success) {
            messageLabel.setText("Account created! You can now log in.");
        } else {
            messageLabel.setText("Username already exists or invalid input.");
        }
    }
}
