package softwaredesign;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import softwaredesign.cards.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deck {
    public static HashMap<String, Card> cardMap = new HashMap<>(){{
        put("ExplodingKittenCard", new ExplodingKittenCard());
        put("DefuseCard", new DefuseCard());
        put("AttackCard", new AttackCard());
        put("NopeCard", new NopeCard());
        put("FavorCard", new FavorCard());
        put("SeeTheFutureCard", new SeeTheFutureCard());
        put("ShuffleCard", new ShuffleCard());
        put("SkipCard", new SkipCard());
        put("CatCardMomma", new CatCardMomma());
        put("CatCardShy", new CatCardShy());
        put("CatCardZombie", new CatCardZombie());
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

    public Card getDefuse(){
        cardDeck.remove(new DefuseCard());
        return new DefuseCard();
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
            System.out.println(cardDrawn);
            if(cardDrawn.getName().equals("ExplodingKittenCard")) insertCard(cardDrawn, getDeckSize() - 1);
            else tempHand.add(cardDrawn);
            if(tempHand.size() == 7) {
                reshuffle();
                return tempHand;
            }
        }
    }

    public void deckConstruct() throws IOException {
        String fileContent = Files.readString(Paths.get("src/main/java/softwaredesign/decks/default.json"), StandardCharsets.US_ASCII);
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for (LinkedTreeMap<Object, Object> cardTree : cardAmounts) {
            for (double j = 0; j < Double.parseDouble(cardTree.get("deckAmount").toString()); j++) {
                cardDeck.add(cardMap.get(cardTree.get("className").toString()));
            }
        }
        reshuffle();
//        cardDeck.forEach(x -> System.out.println(x.action()));
    }
}
