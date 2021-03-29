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
        stage.close();
        System.exit(0);
    }

    @FXML
    private void deckBuilder(){

    }

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @FXML
    public void goOnline() throws Exception{
        ViewsManager.loadScene(SceneName.SERVER_CONNECT);
    }

    @FXML
    public void goOffline() throws Exception{
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.loadScene(SceneName.OFFLINE_SETTINGS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
