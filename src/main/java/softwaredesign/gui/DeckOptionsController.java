package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class DeckOptionsController implements Initializable {
    ScrollButton deckSelectionScroll = new ScrollButton();

    @FXML
    private Label deckSelection;

    @FXML
    private Shape deckSelectionRight, deckSelectionLeft;

    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(ViewsManager.SceneName.SPLASH_SCREEN);
    }

    @FXML
    void rotateDeckSelection(MouseEvent event){
        deckSelectionScroll.navigate((Shape) event.getSource());
    }

    @FXML
    private void createDeck() throws Exception {
        ViewsManager.loadScene(ViewsManager.SceneName.SPLASH_SCREEN);

    }

    @FXML
    private void viewDeck() throws Exception {
        ClientProgram.currentDeck = deckSelection.getText();
        ViewsManager.loadScene(ViewsManager.SceneName.DECK_VIEW);
    }

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deckSelectionScroll.initButtons(deckSelectionLeft, deckSelectionRight, deckSelection);
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
