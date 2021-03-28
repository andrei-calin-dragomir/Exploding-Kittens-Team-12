package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import softwaredesign.gui.ViewsManager.SceneName;


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
    public void goOnline() throws Exception{
        //skip username selection if it is already set
        if (ClientProgram.username == null){
            ViewsManager.loadScene(SceneName.SERVER_CONNECT);
        } else {
            ViewsManager.loadScene(SceneName.ROOM_SELECTION);
        }
    }

    @FXML
    public void goOffline() throws Exception{
        System.out.println(playOnlineButton.getUserData());
        ViewsManager.loadScene(SceneName.OFFLINE_SETTINGS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
