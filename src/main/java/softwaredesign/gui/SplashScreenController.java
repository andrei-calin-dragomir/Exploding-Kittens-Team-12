package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

import java.net.URL;
import java.util.ResourceBundle;


public class SplashScreenController implements Initializable {

    @FXML
    private Button playOnlineButton;

    @FXML
    private Button playOfflineButton;

    @FXML
    private Button exitButton;

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        Sounds.playClick();
        stage.close();
        System.exit(0);
    }

    @FXML
    private void deckBuilder(){

    }

    @FXML
    public void goOnline() throws Exception{
        //skip username selection if it is already set
        Sounds.playClick();
        if (ClientProgram.username == null){
            ViewsManager.loadScene(SceneName.SERVER_CONNECT);
        } else {
            ViewsManager.loadScene(SceneName.ROOM_SELECTION);
        }
    }

    @FXML
    public void goOffline() throws Exception{
        Sounds.playClick();
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.loadScene(SceneName.OFFLINE_SETTINGS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
