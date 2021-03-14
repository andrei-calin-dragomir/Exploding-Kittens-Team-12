package softwaredesign;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deck {
    private HashMap<String, Card> cardMap = new HashMap<>(){{
        put("exploding_kitten", new exploding_kitten());
        put("defuse", new defuse());
        put("attack", new attack());
        put("nope", new nope());
        put("favor", new favor());
        put("see_future", new see_future());
        put("shuffle", new shuffle());
        put("skip", new skip());
        put("catermelon", new catermelon());
        put("taco_cat", new taco_cat());
        put("potato_cat", new potato_cat());
        put("rainbow_cat", new rainbow_cat());
        put("beard_cat", new beard_cat());
    }};
    private ArrayList<Card> cardDeck = new ArrayList<>();

    public Deck() throws IOException {
        String fileContent;
        try {
            fileContent = Files.readString(Paths.get("src/main/java/softwaredesign/decks/default.json"), StandardCharsets.US_ASCII);
        }
        catch (Exception e){
            fileContent = Files.readString(Paths.get("decks/default.json"), StandardCharsets.US_ASCII);
        }
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for (LinkedTreeMap<Object, Object> cardTree : cardAmounts) {
            for (double j = 0; j < Double.parseDouble(cardTree.get("deckAmount").toString()); j++) {
                cardDeck.add(cardMap.get(cardTree.get("className").toString()));
            }
        }
        reshuffle();
    }

    public ArrayList<Card> getFullDeck(){
        return cardDeck;
    }

    public void reshuffle(){
        Collections.shuffle(cardDeck);
    }

    public Card getDefuse(){
        cardDeck.remove(new defuse());
        return new defuse();
    }

    public Card draw(){
        Card cardDrawn = cardDeck.get(0);
        cardDeck.remove(0);
        return cardDrawn;
    }

    public int getDeckSize(){
        return cardDeck.size();
    }

    public void insertCard(Card cardToInsert){ insertCard(cardToInsert, 0); }

    public void insertCard(Card cardToInsert, int index){ cardDeck.add(index, cardToInsert); }

    public ArrayList<Card> getStartCards(){
        ArrayList<Card> tempHand = new ArrayList<>();
        while(true){
            Card cardDrawn = draw();
            if(cardDrawn.getName().equals("exploding_kitten")) insertCard(cardDrawn, getDeckSize() - 1);
            else tempHand.add(cardDrawn);
            if(tempHand.size() == 7) {
                reshuffle();
                return tempHand;
            }
        }
    }
}
