package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateRoomDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextField numberOfPlayersField;

    @FXML
    private TextField numberOfComputersField;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
