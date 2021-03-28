package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import softwaredesign.client.ClientProgram;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ServerConnectController implements Initializable {

    @FXML
    private Label errorServer, errorUsernameTaken, errorUsernameEmpty, errorUsernameLong;

    @FXML
    private TextField serverField, usernameField;

    @FXML
    private Button joinButton;

    CustomAlert usernameError = new CustomAlert();

    private Boolean tryConnect(String serverIP){
        if(serverIP.equals("")) serverIP = "127.0.0.1";
        if(!ClientProgram.currentServer.equals(serverIP))       // Avoids unnecessary reconnects
            if(!ClientProgram.connectAndLoop(serverIP,false)) {
                serverField.setText("");
                errorServer.setVisible(true);
                joinButton.setDisable(false);
                return true;
            }
        System.out.println("Connectedserver");
        errorServer.setVisible(false);
        return false;
    }

    private void handleUsername(String username){
        if(username.isBlank() || username.length() > 20){
            usernameField.setText("");
            if(username.isBlank()) usernameError.activate(errorUsernameEmpty);
            else usernameError.activate(errorUsernameLong);
            joinButton.setDisable(false);
            return;
        }
        System.out.println("Sending message");
        ClientProgram.handleCommand("username " + username);
        System.out.println("Starting wait");
        waitForReply.start();
    }

    AnimationTimer waitForReply = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(!ClientProgram.serverMessage.isEmpty()){
                String[] msg = ClientProgram.serverMessage.removeFirst().split(" ");
                if(msg[0].equals("USERNAMEACCEPTED")){
                    ClientProgram.username = msg[1];
                    super.stop();
                    try {
                        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    usernameField.setText("");
                    usernameError.activate(errorUsernameTaken);
                    joinButton.setDisable(false);
                    super.stop();
                }
            }
        }
    };

    @FXML
    void tryJoin() {
        joinButton.setDisable(true);
        if(tryConnect(serverField.getText())) return;
        System.out.println("Going to username");
        handleUsername(usernameField.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameError.setAlerts(errorUsernameEmpty, errorUsernameTaken, errorUsernameLong);
        errorServer.setVisible(false);
        serverField.setOnKeyPressed(event -> {          // Make "enter" functional
            if(event.getCode() == KeyCode.ENTER) {
                tryJoin();
            }
        });
        usernameField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                tryJoin();
            }
        });
    }
}