package org.ivturbin.sandbox;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class MainWindowController {
    Sniffer sniffer = new Sniffer();
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text text;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    void btnStartClicked(ActionEvent event) {
        SnifferApp.logger.info("Start clicked");
        sniffer.openDevice();
        sniffer.startCapturing(text);
        btnStart.setDisable(true);
        btnStop.setDisable(false);
    }

    @FXML
    void btnStopClicked(ActionEvent event) {
        SnifferApp.logger.info("Stop clicked");
        sniffer.stopCapturing();
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    @FXML
    void initialize() {
        SnifferApp.logger.info("Main window initialize");

    }
}
