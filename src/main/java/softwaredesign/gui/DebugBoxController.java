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
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DebugBoxController extends Application implements Initializable  {

    @FXML
    private TextField commandField;

    @FXML
    private Button sendCommandButton;

    @FXML
    private TextArea receivedMessages;

    @FXML
    void sendCommand(){
        String cmd = commandField.getText();
        commandField.setText("");
        ClientProgram.handleCommand(cmd);
    }

    void addText(String text){
        receivedMessages.setText(receivedMessages.getText() + text + "\n");
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
                addText(ClientProgram.serverMessage.removeFirst());
                System.out.println("GOT NEW MESSAGE");
            }
        }
    };


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameLoop.start();
        try {
            ClientProgram.startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        commandField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                sendCommand();
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
