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
        if(serverIP.equals("localhost")) serverIP = "127.0.0.1";
        if(!ClientProgram.currentServer.equals(serverIP))       // Avoids unnecessary reconnects
            if(!ClientProgram.connectAndLoop(serverIP)) {
                serverField.setText("");
                errorServer.setVisible(true);
                joinButton.setDisable(false);
                return true;
            }
        errorServer.setVisible(false);
        return false;
    }

    private Boolean handleUsername(String username){
        if(username.isBlank() || username.length() > 20){
            if(username.isBlank()) usernameError.activate(errorUsernameEmpty);
            else usernameError.activate(errorUsernameLong);
            joinButton.setDisable(false);
            return true;
        }
        ClientProgram.handleCommand("username " + username);
        while(ClientProgram.serverMessage.isEmpty())
            try{ Thread.sleep(100); } catch(Exception ignore){} // Stupid java doesn't let me sleep without throwing exception, if I throw an exception then I have  to throw an excpetion at intiailize which doesn't work. Have to do this ugly stuff
        String serverResponse = ClientProgram.serverMessage.removeFirst();
        if(serverResponse.equals("USERNAMETAKEN")){
            usernameField.setText("");
            usernameError.activate(errorUsernameTaken);
            joinButton.setDisable(false);
            return true;
        }
        return false;
    }

    @FXML
    void tryJoin()  {
        joinButton.setDisable(true);
        if(tryConnect(serverField.getText())) return;
        if(handleUsername(usernameField.getText())) return;
        ViewsManager.activate("room_selection");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameError.setAlerts(errorUsernameEmpty, errorUsernameTaken, errorUsernameLong);
        errorServer.setVisible(false);
        serverField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) { tryJoin(); }
        });
        usernameField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) { tryJoin(); }
        });
    }
}