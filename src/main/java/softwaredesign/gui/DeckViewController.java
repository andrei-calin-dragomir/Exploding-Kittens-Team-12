package softwaredesign.gui;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import softwaredesign.client.ClientInfo;
import softwaredesign.client.ClientProgram;
import softwaredesign.gui.ViewsManager.SceneName;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Scene where you view what it's in an existing deck.
 */
public class DeckViewController implements Initializable {
    @FXML
    private Text explodingAmount, defuseAmount, attackAmount, favorAmount, shuffleAmount, futureAmount, skipAmount, reverseAmount, mommaAmount, zombieAmount, shyAmount;

    @FXML
    private Label deckName;

    private HashMap<String, Text> mapCardAmounts;


    @FXML
    void returnButton() throws Exception {
        ViewsManager.loadScene(SceneName.DECK_OPTIONS);
    }

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!Sounds.isPlaying("startGameMusic")) Sounds.playStartGameMusic();
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
            put("CatCardZombie",  zombieAmount);
        }};

        deckName.setText(ClientInfo.getCurrentDeck());
        try { deckLoader(); }
        catch (Exception e) {
            try {} catch (Exception ignore) {}
        }
    }

    public void deckLoader() {
        String fileContent;
        try{ fileContent = Files.readString(Paths.get("resources/decks/client/" + ClientInfo.getCurrentDeck() + ".json"), StandardCharsets.US_ASCII); }
        catch(Exception e){ System.out.println("Deck not found? " + e); return; }
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for (LinkedTreeMap<Object, Object> card : cardAmounts) {
            double amountDouble = Double.parseDouble(card.get("deckAmount").toString());
            Integer amountInt = (int) amountDouble;
            mapCardAmounts.get(card.get("className").toString()).setText(amountInt.toString());
        }
    }
}
