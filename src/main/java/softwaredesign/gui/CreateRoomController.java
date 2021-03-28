package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import softwaredesign.client.ClientProgram;

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

    ScrollButton roomSizeScroll = new ScrollButton();
    ScrollButton computerAmountScroll = new ScrollButton();
    ScrollButton deckSelectionScroll = new ScrollButton();

    @FXML
    void rotateRoomSize(MouseEvent event){
        roomSizeScroll.navigate((Shape) event.getSource());
        computerAmountScroll.resetAndAddText(new String[]{"0", "1", "2", "3"});
        for(Integer i = 3; i >= Integer.parseInt(roomSize.getText()); i--){
            System.out.println("Removing " + i);
            computerAmountScroll.remove(i.toString());
        }
    }

    @FXML
    void rotateComputerAmount(MouseEvent event){
        computerAmountScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void rotateDeckSelection(MouseEvent event){
        deckSelectionScroll.navigate((Shape) event.getSource());
    }

    @FXML
    void createRoom() throws Exception {
        String roomName = roomNameField.getText();
        if(roomName.isBlank()) return;
        Integer amountOfPlayers = Integer.parseInt(roomSize.getText());
        Integer amountOfComputers = Integer.parseInt(computerAmount.getText());
        String deckToUse = deckSelection.getText();     // Will be handled
        ClientProgram.handleCommand("create " + roomName + "," + amountOfPlayers + "," + amountOfComputers);
        ClientProgram.roomName = roomName;
        ClientProgram.gameRules[0] = roomSize.getText();
        ClientProgram.gameRules[1] = computerAmount.getText();
        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SCREEN);
        // Go to room mode
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomSizeScroll.initButtons(roomSizeLeft, roomSizeRight, roomSize);
        deckSelectionScroll.initButtons(deckSelectionLeft, deckSelectionRight, deckSelection);
        computerAmountScroll.initButtons(computerAmountLeft, computerAmountRight, computerAmount);
        roomSizeScroll.addText(new String[]{"2", "3", "4"});
        computerAmountScroll.addText(new String[]{"0", "1"});
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