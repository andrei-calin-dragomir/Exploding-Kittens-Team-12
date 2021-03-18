package softwaredesign.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.*;

public class RoomSelectionViewController implements Initializable {

    @FXML
    private ListView<String> roomSelectionList;

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
                launchRoomDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Action on "back":
        backButton.setOnAction(e -> {
            ViewsManager.activate("splash_screen");
        });
    }

    private void populateList(){
        ObservableList<String> items = FXCollections.observableArrayList(
                "my", "example"
        );
        roomSelectionList.setItems(items);
    }

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
