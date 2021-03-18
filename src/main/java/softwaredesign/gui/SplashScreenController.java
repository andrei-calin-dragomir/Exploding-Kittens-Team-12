package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    @FXML
    private Button playOnlineButton;

    @FXML
    private Button playOfflineButton;

    @FXML
    private Button exitButton;

    public void goOnline(){
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.activate("room_selection");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
