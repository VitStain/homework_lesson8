package lesson7.client.controllers;

/*import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lesson7.client.ChatGB;
import lesson7.client.models.Network;

import java.awt.*;
import java.io.IOException;*/

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lesson7.client.ChatGB;
import lesson7.client.models.Network;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AuthController {
    private lesson7.client.models.Network network;
    private ChatGB mainChatGB;
    private boolean notLog = true;

    @FXML
    public Label authLabel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    @FXML
    void initialize() {
        Thread thread = new Thread(() -> {
            long startTimeAuth = System.currentTimeMillis();
            try {
                int seconds = 0;
                while (seconds < 121) {
                    Thread.sleep(1000);
                    seconds = (int) ((System.currentTimeMillis() - startTimeAuth) / 1000);
                    String msg = "Авторизируйтесь, осталось: " + (120 - seconds) + " секунд";
                    Platform.runLater(() -> authLabel.setText(msg));
                }
                if (notLog) {
                    exit();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void exit() {
        System.exit(-3);
    }

    public void checkAuth(/*ActionEvent actionEvent*/) throws IOException {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if(login.length() == 0 || password.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText("Ошибка ввода");
            alert.setContentText("Поля не должны быть пустыми!");
            alert.show();
            return;

        }

        String authErrorMessage = network.sendAuthCommand(login, password);
        if (authErrorMessage == null) {
            notLog = false;
            mainChatGB.createChatDialog();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText("Ошибка авторизации");
            alert.setContentText(authErrorMessage);
            alert.show();
            System.out.println("!!Ошибка аутентификации");
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setChatGB(ChatGB chatGB) {
        this.mainChatGB = chatGB;
    }


}
