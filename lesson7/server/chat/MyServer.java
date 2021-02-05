package lesson7.server.chat;

import jdk.dynalink.linker.LinkerServices;
import lesson7.server.chat.auth.BaseAuthService;
import lesson7.server.chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final ServerSocket serverSocket;
    private BaseAuthService authService;
    private List<ClientHandler> clients = new ArrayList<>();

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String END_CMD_PREFIX = "/end"; //
    private static final String CLIENTS_LIST_PREFIX = "/clients";

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }

    public BaseAuthService getAuthService() {
        return authService;
    }

    public void start() {

        System.out.println("Сервер запущен!");


        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.endAuthentication();
            }
        }

    }

    private void waitAndProcessNewClientConnection() throws IOException {
        authService = new BaseAuthService();
//        authService.startAuthentication();
        clients = new ArrayList<>();
        System.out.println("Ожидание подключения пользователя...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.handle();
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastClientMessage(String message, ClientHandler sender/*, boolean isServerMessage*/) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
//            client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
            client.sendMessage(CLIENT_MSG_CMD_PREFIX + " " + sender.getUsername() + " ", message);
        }
    }

//    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
//        broadcastMessage(message, sender);
//
//    }


    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
//                client.sendMessage(sender.getUsername(), privateMessage);
                String s = String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, sender.getUsername(), privateMessage);
                client.sendMessage(sender.getUsername(), s);

                return;
            }
        }
        String serverMsg = String.format("%s %s не существует", SERVER_MSG_CMD_PREFIX, recipient);
        sender.sendMessage(recipient, serverMsg);

    }

    public synchronized void broadcastServerMsg(String message) throws IOException {
        for (ClientHandler client : clients) {
            String serverMsg = String.format("%s %s", SERVER_MSG_CMD_PREFIX, message);
            client.sendMessage("Cервер ", serverMsg);

        }
    }

    public synchronized void broadcastClientsList() throws IOException {
        StringBuilder list = new StringBuilder(CLIENTS_LIST_PREFIX + " ");
        for (ClientHandler client : clients) {
            list.append(client.getUsername() + " ");
        }
        for (ClientHandler client : clients) {
            client.sendMessage(" ", list.toString());
        }
    }

}
