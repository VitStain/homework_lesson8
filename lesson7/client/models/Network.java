package lesson7.client.models;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lesson7.client.controllers.ChatController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String END_CMD_PREFIX = "/end"; //
    private static final String CLIENTS_LIST_PREFIX = "/clients";

    private static final int DEFAULT_SERVER_SOCKET = 8828;
    private static final String DEFAULT_SERVER_HOST = "localhost";

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final int port;
    private final String host;

    private String username;

    public Network(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public Network() {
        this.host = DEFAULT_SERVER_HOST;
        this.port = DEFAULT_SERVER_SOCKET;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Соединение не установлено");
            System.exit(-1);
            e.printStackTrace();
        }
    }

    public DataOutputStream getOut() {
        return out;
    }


    public void waitMessage(ChatController chatController) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();

                    if (message.startsWith(CLIENT_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 3);
                        String sender = parts[1];
                        String messageFromUser = parts[2];

                        Platform.runLater(() -> chatController.appendMessage(String.format("%s: %s", sender, messageFromUser)));
                    } else if (message.startsWith(SERVER_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 2);
                        String messageFromUser = parts[1];

                        Platform.runLater(() -> chatController.appendMessage(messageFromUser));

                    } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 3);
                        String sender = parts[1];
                        String msg = parts[2];
                        Platform.runLater(() -> chatController.appendMessage(String.format("%s: %s", sender, msg)));

                    } else if (message.startsWith(CLIENTS_LIST_PREFIX)) {
                        String[] parts = message.split("\\s+");
                        ObservableList<String> participantList = FXCollections.observableArrayList();
                        for (int i = 1; i < parts.length; i++) {
                            participantList.add(parts[i]);
                        }
                        Platform.runLater(() -> chatController.setParticipantList(participantList));

                    } else {
                        Platform.runLater(() -> System.out.println("!!Неизвестная ошибка сервера"));
                        Platform.runLater(() -> chatController.appendMessage(String.format("%s: %s", "!!!Неизвестная ошибка сообщение: ", message)));
                    }
                }


            } catch (IOException e) {
                System.out.println("Ошибка подключения");
                System.exit(-1);
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));

            String response = in.readUTF();
            if (response.startsWith(AUTHOK_CMD_PREFIX)) {
                this.username = response.split("\\s+", 2)[1];
                return null;
            } else {
                return response.split("\\s+", 2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void sendPrivateMessage(String message, String recipient) throws IOException {
        String command = String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, recipient, message);
        sendMessage(command);
    }
}
