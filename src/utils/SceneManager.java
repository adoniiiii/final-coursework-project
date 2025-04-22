package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource("/views/" + fxmlPath));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void switchToMain(String username, String role) {
        try {
            if ("admin".equalsIgnoreCase(role)) {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/AdminView.fxml"));
                Parent root = loader.load();
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            } 
            else {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/MainView.fxml"));
                Parent root = loader.load();
                controllers.MainController controller = loader.getController();
                controller.setUser(username, role);
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
