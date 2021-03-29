package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import softwaredesign.gui.ViewsManager.SceneName;

import java.net.URL;
import java.util.ResourceBundle;


public class SplashScreenController implements Initializable {

    @FXML
    private Button exitButton;

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    private void deckBuilder() throws Exception {
        ViewsManager.loadScene(SceneName.DECK_OPTIONS);
    }

    @FXML
    void muteSounds(){
        Boolean muted = Sounds.getMute();
        Sounds.setMute(!muted);
        if(Sounds.getMute()) Sounds.stopSound();
        else Sounds.playStartGameMusic();
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
        ViewsManager.loadScene(SceneName.OFFLINE_SETTINGS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sounds.setMute(true);
        Sounds.playStartGameMusic();
    }
}
