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
    }

    @FXML
    public void goOnline() throws Exception{
        //skip username selection if it is already set
        if (ClientProgram.username == null){
            ViewsManager.loadScene(SceneName.CHOOSE_NAME);
        } else {
            ViewsManager.loadScene(SceneName.ROOM_SELECTION);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
