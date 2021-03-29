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
    private Text noRoomsText, roomError;

    @FXML
    private Button joinButton;

    @FXML
    private Button createButton;

    @FXML
    private Button backButton;

    @FXML
    void returnButton() throws Exception {
        ClientProgram.killConnectionSafely();
        checkForRoomsLoop.stop();
        ViewsManager.loadScene(SceneName.SPLASH_SCREEN);
    }
    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!Sounds.isPlaying("startGameMusic")) Sounds.playStartGameMusic();
        populateList();
        roomSelectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void createRoom() throws Exception {
        checkForRoomsLoop.stop();
        ViewsManager.loadScene(SceneName.CREATE_ROOM);
    }

    public void joinRoom() {
        checkForRoomsLoop.start();
        String selectedRoom = roomSelectionList.getSelectionModel().getSelectedItem();
        if(selectedRoom == null) {
            roomError.setText("Select a room");
            return;
        }
        ClientProgram.handleCommand("join " + selectedRoom);
        ClientProgram.roomName = roomSelectionList.getSelectionModel().getSelectedItem();
    }

    public void populateList(){
        checkForRoomsLoop.start();
        ClientProgram.handleCommand("list_rooms");
    }

    AnimationTimer checkForRoomsLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(ClientProgram.serverMessage.isEmpty()) return;
            String[] cmdlist = ClientProgram.serverMessage.removeFirst().split(" ");
            if(cmdlist[0].equals("ROOM")){
                switch(cmdlist[1]){
                    case "NOROOM":
                        ObservableList<String> emptyRoom = observableArrayList();
                        roomSelectionList.setItems(emptyRoom);
                        noRoomsText.setVisible(true);
                        break;
                    case "AVAILABLE":
                        noRoomsText.setVisible(false);
                        ObservableList<String> rooms = observableArrayList(cmdlist[2].split(","));
                        roomSelectionList.setItems(rooms);
                        break;
                    case "FULL":
                        Sounds.playErrorSound();
                        roomError.setText("Room is full");
                        ClientProgram.roomName = "";
                        break;
                    case "STARTED":
                        Sounds.playErrorSound();
                        roomError.setText("Game in progress");
                        ClientProgram.roomName = "";
                        break;
                }
            }
            else if(cmdlist[0].equals("JOINSUCCESS")){
                super.stop();
                Sounds.stopSound();
                try { ViewsManager.loadScene(SceneName.ROOM_SCREEN); }
                catch (Exception ignore) {}
            }
        }
    };
}
