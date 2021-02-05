package lesson7.client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lesson7.client.controllers.AuthController;
import lesson7.client.controllers.ChatController;
import lesson7.client.models.Network;

import java.io.IOException;
import java.util.List;


public class ChatGB extends Application {

//    public static final List<String> USERS_TEST_DATA = List.of("Мартин_Некотов", "Борис_Николаевич", "Гендальф_Серый");
    private Network network;
    private Stage primaryStage;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        network = new Network();
        network.connect();

        openAuthDialog();
//        createChatDialog();


    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(ChatGB.class.getResource("views/auth-view.fxml"));

        Parent root = authLoader.load();
        authStage = new Stage();

        authStage.setTitle("Аутентификация");
        authStage.setScene(new Scene(root));
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
//        authStage.setX(1400);
        authStage.show();

        AuthController authLoaderController = authLoader.getController();
        authLoaderController.setNetwork(network);
        authLoaderController.setChatGB(this);
    }

    public void createChatDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatGB.class.getResource("views/chat-view.fxml"));

        Parent root = loader.load();

//        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root));
//        primaryStage.setY(2200);
//        primaryStage.setX(1400);


        chatController = loader.getController();
        chatController.setNetwork(network);

        authStage.close();
        primaryStage.show();
        primaryStage.setTitle(network.getUsername());
        network.waitMessage(chatController);
        chatController.setUsernameTitle(network.getUsername());


    }

    public static void main(String[] args) {
        launch(args);
    }


}