package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class OfflineSettingsController implements Initializable {

    @FXML
    private Label playerAmount;

    @FXML
    private Shape startGame, leftButtonPlayers, rightButtonPlayers;

    ScrollButton playerAmountScroll = new ScrollButton();

    @FXML
    void rotatePlayerAmount(MouseEvent event){
        playerAmountScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void tryJoin()  {
        startGame.setDisable(true);
        ViewsManager.activate("room_selection");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerAmountScroll.initButtons(leftButtonPlayers, rightButtonPlayers, playerAmount);
        getDeckNames();
    }

    public ArrayList<String> getDeckNames(){
        ArrayList<String> allDecks = new ArrayList<>();
        File folder = new File("resources/decks");
        File[] allFiles = folder.listFiles();

        for (int i = 0; i < allFiles.length; i++)
            if (allFiles[i].isFile())
                allDecks.add(allFiles[i].getName().split("\\.")[0]);
        playerAmountScroll.addText(allDecks.toArray(new String[allDecks.size()]));
        return allDecks;
    }
}