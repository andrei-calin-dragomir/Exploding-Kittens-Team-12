package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

public class ChooseNameController {

    String username;

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

    AnimationTimer waitForReply = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(!ClientProgram.serverMessage.isEmpty()){
                String msg = ClientProgram.serverMessage.removeFirst();
                if(msg.equals("USERNAMEACCEPTED")){
                    errorText.setText("Success!");
                    ClientProgram.username = username;
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
