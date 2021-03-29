package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OfflineSettingsController implements Initializable {
    ScrollButton playerAmountScroll = new ScrollButton();
    ScrollButton deckSelectionScroll = new ScrollButton();

    @FXML
    public void playClick(){
        //Sounds.playClick();
    }

    @FXML
    private Label playerAmount, deckSelection;

    @FXML
    private Shape startGame, leftButtonPlayers, rightButtonPlayers, leftButtonDeck, rightButtonDeck;

    @FXML
    void rotatePlayerAmount(MouseEvent event){
        playerAmountScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void rotateDeckAmount(MouseEvent event){
        deckSelectionScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void startGame() throws Exception {
        Integer amountOfComputers = Integer.parseInt(playerAmount.getText());
        Integer totalPlayers = amountOfComputers + 1;
        String deckToUse = deckSelection.getText();
        ClientProgram.startClient("127.0.0.1", true);
        ClientProgram.handleCommand("create sologame," + totalPlayers + "," + amountOfComputers);
        ClientProgram.handleCommand("start");
        ViewsManager.loadScene(ViewsManager.SceneName.GAME_VIEW);
        // Offline mode has been deleted wtf? I can't seem to find it anymore. Anyway just pass these arguments and start offline.
    }

    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(ViewsManager.SceneName.SPLASH_SCREEN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerAmountScroll.initButtons(leftButtonPlayers, rightButtonPlayers, playerAmount);
        deckSelectionScroll.initButtons(leftButtonDeck, rightButtonDeck, deckSelection);
        playerAmountScroll.addText(new String[]{"1", "2", "3", "4"});
        deckSelectionScroll.addText(getDeckNames());
    }

    public String[] getDeckNames(){
        ArrayList<String> allDecks = new ArrayList<>();
        File folder = new File("resources/decks");
        File[] allFiles = folder.listFiles();

        for (int i = 0; i < allFiles.length; i++)
            if (allFiles[i].isFile())
                allDecks.add(allFiles[i].getName().split("\\.")[0]);

        return allDecks.toArray(new String[allDecks.size()]);
    }
}