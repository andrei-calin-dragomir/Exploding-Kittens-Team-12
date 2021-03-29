package softwaredesign.gui;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import softwaredesign.cards.*;
import softwaredesign.client.ClientProgram;
import softwaredesign.core.Deck;
import softwaredesign.gui.ViewsManager.SceneName;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class DeckCreateController implements Initializable {
    HashMap<String, TextField> mapCardAmounts;

    @FXML
    public TextField deckName, explodingAmount, defuseAmount, attackAmount, favorAmount, shuffleAmount, futureAmount, skipAmount, reverseAmount, mommaAmount, zombieAmount, shyAmount;

    @FXML
    public Button createButton;

    @FXML
    public Text deckCreationText;

    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(SceneName.DECK_OPTIONS);
    }

    @FXML
    void deckConstructor() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Integer totalDeckSize = 0;
        String deckN = deckName.getText();
        if(deckN.isBlank()) {
            deckCreationText.setText("The name cannot be empty");
            return;
        }
        HashMap<String, Integer> amountMap = new HashMap<>();
        for(String cardName : mapCardAmounts.keySet()){
            String strAmount = mapCardAmounts.get(cardName).getText();
            if(strAmount.isBlank()) strAmount = "0";
            if(!ClientProgram.isInteger(strAmount)){
                deckCreationText.setText("Invalid input, try again");
                return;
            }
            int amount = Integer.parseInt(strAmount);
            amountMap.put(cardName, amount);
            totalDeckSize += amount;
        }
        if(totalDeckSize < 40) {
            deckCreationText.setText("You need at least 40 cards");
            return;
        }
        Deck.createCustom(amountMap, deckN, "client");
        deckCreationText.setText("Deck creation successful");
    }

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapCardAmounts = new HashMap<>(){{
            put("ExplodingKittenCard", explodingAmount);
            put("ReverseCard", reverseAmount);
            put("DefuseCard", defuseAmount);
            put("AttackCard", attackAmount);
            put("FavorCard", favorAmount);
            put("SeeTheFutureCard", futureAmount);
            put("ShuffleCard", shuffleAmount);
            put("SkipCard", skipAmount);
            put("CatCardMomma", mommaAmount);
            put("CatCardShy", shyAmount);
            put("CatCardZombie", zombieAmount);
        }};
    }
}
