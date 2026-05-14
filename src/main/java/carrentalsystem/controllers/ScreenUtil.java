package carrentalsystem.controllers;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenUtil {

    public static void makeFullScreen(Stage stage, Scene scene) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.setResizable(true);
    }
}