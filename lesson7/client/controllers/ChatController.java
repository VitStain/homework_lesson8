package lesson7.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lesson7.client.ChatGB;
import lesson7.client.models.Network;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ChatController {
    private Network network;
    private String selectedRecipient;
    public ObservableList<String> participantList = FXCollections.observableArrayList(
            "Ждём новых участников");

    @FXML
    public ListView<String> usersList;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    @FXML
    private Label usernameTitle;

    @FXML
    public void initialize() {
//        usersList.setItems(FXCollections.observableArrayList(ChatGB.USERS_TEST_DATA));
        usersList.setItems(participantList);
        sendButton.setOnAction(event -> ChatController.this.sendMessage());
        textField.setOnAction(event -> ChatController.this.sendMessage());

        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    private void sendMessage() {
        String message = textField.getText().trim();

        if (message.length() == 0) {
            return;
        }
        if (!message.isBlank()) {
            try {
                if (selectedRecipient != null) {
                    network.sendPrivateMessage(message, selectedRecipient);
                } else {
                    network.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("!!Ошибка при отправке сообщения");
            }
            appendMessage("Я: " + message);
            textField.clear();
//        Platform.runLater(() -> appendMessage("Я: " + message));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText("Ошибка ввода сообщения");
            alert.setContentText("Нельзя отправлять пустое сообщение");
            alert.show();
        }

    }

    public void appendMessage(String message) {
        String timeStamp = DateFormat.getInstance().format(new Date());


        chatHistory.appendText(timeStamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setUsernameTitle(String username) {
        this.usernameTitle.setText(username);
    }

    public synchronized void setParticipantList(ObservableList<String> participantList) {
        this.participantList = participantList;
        usersList.setItems(participantList);
        usersList.refresh();
    }

    @FXML
    void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("СЕТЕВОЙ ЧАТ");
        alert.show();
    }

    @FXML
    void delete() {
        chatHistory.clear();
    }

    @FXML
    void exit() {
        System.exit(0);
    }
}