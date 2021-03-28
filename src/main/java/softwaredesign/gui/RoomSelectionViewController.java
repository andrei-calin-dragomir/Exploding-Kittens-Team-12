package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.*;
import softwaredesign.gui.ViewsManager.SceneName;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //set up list:
        populateList();
        roomSelectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Action on "join":
        joinButton.setOnAction(e -> {
            String selectedItem = roomSelectionList.getSelectionModel().getSelectedItem(); //this is the selected string
            System.out.println("selected item = " + selectedItem);
        });

        //Action on "create":
        createButton.setOnAction(event -> {
            try {
//                launchRoomDialog();
                ViewsManager.loadScene(SceneName.CREATE_ROOM);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Action on "back":
        backButton.setOnAction(e -> {
            try {
                ViewsManager.loadScene(SceneName.SPLASH_SCREEN);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void createRoom(){
        try {
            ViewsManager.loadScene(SceneName.CREATE_ROOM);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                switch (cmdlist[1]){
                    case "NOROOM":
                        System.out.println("NO ROOMS FOUND, DOING NOTHING");
                        noRoomsText.setVisible(true);
                        super.stop();
                        break;
                    case "AVAILABLE":
                        System.out.println("ROOMS FOUND INDEED");
                        noRoomsText.setVisible(false);
                        ObservableList<String> rooms = observableArrayList(cmdlist[2].split(","));
                        roomSelectionList.setItems(rooms);
                        super.stop();
                        break;
                }
            }
        }
    };

    private void launchRoomDialog() throws IOException {
        URL newUrl = new File("src/main/resources/fxml/createRoomDialog.fxml").toURI().toURL();
        Pane pane = FXMLLoader.load(newUrl);
        Scene scene = new Scene(pane, 392, 400);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
