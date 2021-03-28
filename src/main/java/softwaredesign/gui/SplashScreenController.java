package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

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
    public void goOnline(){
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.activate("server_connect");
    }

    @FXML
    public void goOffline(){
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.activate("offline_settings");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
//            ClientProgram.startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
