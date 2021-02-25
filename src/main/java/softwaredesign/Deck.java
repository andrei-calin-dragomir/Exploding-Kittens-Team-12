package softwaredesign;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deck {
    public HashMap<String, Card> cardMap = new HashMap<>(){{
        put("exploding_kitten", new exploding_kitten());
        put("defuse", new defuse());
        put("attack", new attack());
        put("nope", new nope());
        put("favor", new favor());
        put("see_future", new see_future());
        put("alter_future", new alter_future());
        put("shuffle", new shuffle());
        put("skip", new skip());
        put("catermelon", new catermelon());
        put("taco_cat", new taco_cat());
        put("potato_cat", new potato_cat());
        put("rainbow_cat", new rainbow_cat());
        put("beard_cat", new beard_cat());
    }};
    public ArrayList<Card> cardDeck = new ArrayList<>();

    public Deck() throws IOException {
        deckConstruct();
    }

    public ArrayList<Card> getFullDeck(){
        return cardDeck;
    }

    public void reshuffle(){
        Collections.shuffle(cardDeck);
    }

    public Card draw(){
        Card cardDrawn = cardDeck.get(0);
        cardDeck.remove(0);
        return cardDrawn;
    }

    public int getDeckSize(){
        return cardDeck.size();
    }

    public void deckConstruct() throws IOException {
        String fileContent = Files.readString(Paths.get("src/main/java/softwaredesign/decks/default.json"), StandardCharsets.US_ASCII);
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for(int i = 0; i < cardAmounts.size(); i++){
            LinkedTreeMap<Object, Object> cardTree = cardAmounts.get(i);
            for(double j = 0; j < Double.parseDouble(cardTree.get("deckAmount").toString()); j++){
                cardDeck.add(cardMap.get(cardTree.get("className").toString()));
            }
        }
        reshuffle();
//        cardDeck.forEach(x -> System.out.println(x.action()));
    }
}
