package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DebugBoxController extends Application implements Initializable  {

    @FXML
    private TextField commandField;

    @FXML
    private TextArea receivedMessages;

    void sendCommand(String cmd) throws IOException {
        if(cmd.equals("startgame")){
            startGameView();
            return;
        }
        commandField.setText("");
        addText("s> " + cmd);
        ClientProgram.handleCommand(cmd);
    }

    @FXML
    void sendTextAsCommand() throws IOException {
        String cmd = commandField.getText();
        sendCommand(cmd);
    }

    @FXML
    void startGameView() throws IOException{
        gameLoop.stop();
        URL newUrl = new File("src/main/resources/fxml/gameView.fxml").toURI().toURL();
        Pane pane = FXMLLoader.load(newUrl);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    void addText(String text){
        receivedMessages.setText(receivedMessages.getText() + text + "\n");
        receivedMessages.positionCaret(receivedMessages.getLength());
    }

    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void start(){
            addText("Game loop started!!");
            super.start();
        }

        @Override
        public void handle(long now) {
            if(!ClientProgram.serverMessage.isEmpty()){
                addText("r> " + ClientProgram.serverMessage.removeFirst());
            }
        }
    };

    @FXML
    public void connectPlayer1(){
        try {
            sendCommand("username player1");
            sendCommand("create room,3,1");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void connectPlayer2(){
        try {
            sendCommand("username player2");
            sendCommand("join room");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameLoop.start();
        String ip = "127.0.0.1";
        try {
            ClientProgram.startClient(ip,false);
            addText("Connected to " + ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        commandField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                try {
                    sendTextAsCommand();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/main/resources/fxml/debugBox.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene mainScene = new Scene(root);
        primaryStage.setTitle("Exploding Kittens");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
