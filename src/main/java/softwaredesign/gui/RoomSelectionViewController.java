package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.text.Text;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class RoomSelectionViewController implements Initializable {

    @FXML
    private ListView<String> roomSelectionList;

    @FXML
    private Text noRoomsText;

    @FXML
    private Button joinButton;

    @FXML
    private Button createButton;

    @FXML
    private Button backButton;

    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(SceneName.SPLASH_SCREEN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateList();
        roomSelectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void createRoom() throws Exception {
        checkForRoomsLoop.stop();
        ViewsManager.loadScene(SceneName.CREATE_ROOM);
    }

    public void joinRoom() throws Exception {
        roomSelectionList.setDisable(true);
        ClientProgram.handleCommand("join " + roomSelectionList.getSelectionModel().getSelectedItem());
        ClientProgram.roomName = roomSelectionList.getSelectionModel().getSelectedItem();
        ViewsManager.loadScene(SceneName.ROOM_SCREEN);
    }

    public void populateList(){
        ClientProgram.handleCommand("list_rooms");
        checkForRoomsLoop.start();
    }

    AnimationTimer checkForRoomsLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(ClientProgram.serverMessage.isEmpty()) return;
            String[] cmdlist = ClientProgram.serverMessage.removeFirst().split(" ");
            if(cmdlist[0].equals("ROOM")){
                if(cmdlist[1].equals("NOROOM")){
                    noRoomsText.setVisible(true);
                    super.stop();
                    return;
                }
                noRoomsText.setVisible(false);
                ObservableList<String> rooms = observableArrayList(cmdlist[2].split(","));
                roomSelectionList.setItems(rooms);
                super.stop();
                }
            }
        };
}
