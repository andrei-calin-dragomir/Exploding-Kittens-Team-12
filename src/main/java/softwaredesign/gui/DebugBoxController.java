package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import softwaredesign.client.ClientProgram;

import java.net.URL;
import java.util.ResourceBundle;

public class DebugBoxController implements Initializable {

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
            addText("Connected!");
        }

        @Override
        public void handle(long now) {
            if(ClientProgram.newMessage){
                addText(ClientProgram.serverMessage);
                ClientProgram.newMessage = false;
            }
        }
    };


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ClientProgram.startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
