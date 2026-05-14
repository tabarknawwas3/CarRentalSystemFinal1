package carrentalsystem;

import carrentalsystem.controllers.ScreenUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/carrentalsystem/Login.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("DriveEase — Car Rental System");
        ScreenUtil.makeFullScreen(stage, scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}