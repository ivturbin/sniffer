package org.ivturbin.sandbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class SnifferApp extends Application {
    final Logger logger = LogManager.getLogger(SnifferApp.class.getName());

    @Override
    public void start(Stage primaryStage) throws IOException {
       logger.info("Application started");
        Parent root = FXMLLoader.load(getClass().getResource("/MainWindow.fxml"));

        primaryStage.setTitle("Sniffer");
        primaryStage.setScene(new Scene(root, 1100, 540));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
