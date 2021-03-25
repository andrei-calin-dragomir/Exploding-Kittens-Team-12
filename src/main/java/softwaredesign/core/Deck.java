package softwaredesign.core;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import softwaredesign.cards.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deck {
    public static HashMap<String, Class<? extends Card>> cardMap = new HashMap<>(){{
        put("ExplodingKittenCard", ExplodingKittenCard.class);
        put("DefuseCard", DefuseCard.class);
        put("AttackCard", AttackCard.class);
        put("NopeCard",NopeCard.class);
        put("FavorCard", FavorCard.class);
        put("SeeTheFutureCard", SeeTheFutureCard.class);
        put("ShuffleCard", ShuffleCard.class);
        put("SkipCard", SkipCard.class);
        put("CatCardMomma", CatCardMomma.class);
        put("CatCardShy", CatCardShy.class);
        put("CatCardZombie",  CatCardZombie.class);
    }};
    public ArrayList<Card> cardDeck = new ArrayList<>();

    public Deck() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

    public void deckConstruct() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String fileContent = Files.readString(Paths.get("resources/decks/default.json"), StandardCharsets.US_ASCII);
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for (LinkedTreeMap<Object, Object> cardTree : cardAmounts) {
            for (double j = 0; j < Double.parseDouble(cardTree.get("deckAmount").toString()); j++) {
                Card newCard = cardMap.get(cardTree.get("className").toString()).getDeclaredConstructor().newInstance();        // Reflection wasn't allowed, so the easiest way to get a class for a string is with a HashMap
                cardDeck.add(newCard);
            }
        }
        reshuffle();
//        cardDeck.forEach(x -> System.out.println(x.action()));
    }
}
