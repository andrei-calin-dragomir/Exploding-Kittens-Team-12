package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import softwaredesign.client.ClientInfo;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

public class ChooseNameController {

    private String username;

    @FXML
    private TextField textField;

    @FXML
    private Text errorText;

    @FXML
    private Button selectGameButton;

    @FXML
    private void sendUsername() {
        username = textField.getText();
        ClientProgram.handleCommand("username " + username);
        waitForReply.start();
    }


    /**
     * This loops while checking for replies, until it finds one and either
     * accepts the username of asks for a correct one.
     */
    AnimationTimer waitForReply = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(!ClientInfo.getServerMessage().isEmpty()){
                String msg = ClientInfo.getServerMessage().removeFirst();
                if(msg.equals("USERNAMEACCEPTED")){
                    errorText.setText("Success!");
                    ClientInfo.setUsername(username);
                    try {
                        ViewsManager.loadScene(SceneName.ROOM_SELECTION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.stop();
                } else {
                    errorText.setText("Username taken");
                    super.stop();
                }
            }
        }
    };
}
