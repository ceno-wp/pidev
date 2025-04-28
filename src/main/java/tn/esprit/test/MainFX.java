package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.services.ChatServer;
import tn.esprit.utils.MyDataBase;

import java.io.IOException;
import java.net.ServerSocket;

public class MainFX extends Application {
    private static ChatServer chatServer;

    public static void main(String[] args) {
        MyDataBase.getInstance();
        if (!isPortInUse(8887)) {
            chatServer = new ChatServer();
            chatServer.start();
        }
        launch(args);

    }

    @Override
    public void stop() throws Exception {
        if (chatServer != null) {
            chatServer.stop();
        }
        super.stop();
    }

    private static boolean isPortInUse(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader loader =new FXMLLoader(getClass().getResource("/login.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Legal Link");
            primaryStage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}