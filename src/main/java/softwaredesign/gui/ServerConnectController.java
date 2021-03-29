package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import softwaredesign.client.ClientProgram;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Scene where you select a username and server ip.
 */
public class ServerConnectController implements Initializable {

    @FXML
    private Label errorServer, usernameError;

    @FXML
    private TextField serverField, usernameField;

    @FXML
    private Button joinButton;

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    private Boolean tryConnect(String serverIP){
        if(serverIP.equals("")) serverIP = "127.0.0.1";
        if(!ClientProgram.currentServer.equals(serverIP))       // Avoids unnecessary reconnects
            if(!ClientProgram.connectAndLoop(serverIP,false)) {
                Sounds.playErrorSound();
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
            Sounds.playErrorSound();
            if(username.isBlank()) usernameError.setText("Username cannot be empty");
            else usernameError.setText("Username is too long");
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
                    System.out.println(msg[0]);
                    Sounds.playErrorSound();
                    usernameField.setText("");
                    usernameError.setText("Username already taken");
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

    @FXML
    void returnButton() throws Exception {
        waitForReply.stop();
        ViewsManager.loadScene(ViewsManager.SceneName.SPLASH_SCREEN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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