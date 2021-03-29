package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import softwaredesign.client.ClientProgram;
import softwaredesign.core.Deck;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateRoomController implements Initializable {

    @FXML
    private Label roomSize, computerAmount, deckSelection;

    @FXML
    private Shape roomSizeRight, roomSizeLeft, computerAmountRight, computerAmountLeft, deckSelectionRight, deckSelectionLeft;

    @FXML
    private TextField roomNameField;

    @FXML
    private Text roomNameExists;

    ScrollButton roomSizeScroll = new ScrollButton();
    ScrollButton computerAmountScroll = new ScrollButton();
    ScrollButton deckSelectionScroll = new ScrollButton();


    /**
     * Adapt room size based on how many computers you want.
     */
    @FXML
    void rotateRoomSize(MouseEvent event){
        roomSizeScroll.navigate((Shape) event.getSource());
        computerAmountScroll.resetAndAddText("0", "1", "2", "3");
        for(Integer i = 3; i >= Integer.parseInt(roomSize.getText()); i--){
            computerAmountScroll.remove(i.toString());
        }
    }

    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
    }

    @FXML
    void rotateComputerAmount(MouseEvent event){
        computerAmountScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void rotateDeckSelection(MouseEvent event){
        deckSelectionScroll.navigate((Shape) event.getSource());
    }

    /**
     * Tries to create a new room, then joins it if servers accepts it.
     * On-click function.
     */
    @FXML
    void createRoom() throws Exception {
        String roomName = roomNameField.getText();
        if(roomName.isBlank()) return;
        int amountOfPlayers = Integer.parseInt(roomSize.getText());
        int amountOfComputers = Integer.parseInt(computerAmount.getText());
        String deckToUse = deckSelection.getText();     // Will be handled
        String serializedDeck = "";
        if(!deckToUse.equals("default")) serializedDeck = Deck.serializeDeck(deckToUse, "Client");
        ClientProgram.handleCommand("create " + roomName + "," + amountOfPlayers + "," + amountOfComputers + "," + serializedDeck);
        ClientProgram.roomName = roomName;
        ClientProgram.gameRules[0] = roomSize.getText();
        ClientProgram.gameRules[1] = computerAmount.getText();
        Sounds.stopSound();
        waitForReply.start();
    }

    /**
     * Sends server the request for a room, joins it if accepted.
     */
    AnimationTimer waitForReply = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (!ClientProgram.serverMessage.isEmpty()) {
                String[] msg = ClientProgram.serverMessage.removeFirst().split(" ");
                if (msg[0].equals("ROOM")) {
                    if (msg[1].equals("TAKEN")) {
                        roomNameExists.setVisible(true);
                        super.stop();
                    }
                    else if (msg[1].equals("CREATED")) {
                        // Go to room mode
                        ClientProgram.offlineGame = false;
                        try { ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SCREEN); } catch (Exception ignore) {}
                        super.stop();
                    }
                }
            }
        }
    };

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!Sounds.isPlaying("startGameMusic")) Sounds.playStartGameMusic();
        roomSizeScroll.initButtons(roomSizeLeft, roomSizeRight, roomSize);
        deckSelectionScroll.initButtons(deckSelectionLeft, deckSelectionRight, deckSelection);
        computerAmountScroll.initButtons(computerAmountLeft, computerAmountRight, computerAmount);
        roomSizeScroll.addText("2", "3", "4");
        computerAmountScroll.addText("0", "1");
        deckSelectionScroll.addText(getDeckNames());
    }

    public String[] getDeckNames(){
        ArrayList<String> allDecks = new ArrayList<>();
        File folder = new File("resources/decks/client");
        File[] allFiles = folder.listFiles();

        for (int i = 0; i < allFiles.length; i++)
            if (allFiles[i].isFile())
                allDecks.add(allFiles[i].getName().split("\\.")[0]);

        return allDecks.toArray(new String[allDecks.size()]);
    }
}