import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SceneManager;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Your Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
