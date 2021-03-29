package softwaredesign.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.NotNull;
import softwaredesign.cards.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deck implements Iterable<Card>{
    public static HashMap<String, Class<? extends Card>> cardMap = new HashMap<>(){{
        put("ExplodingKittenCard", ExplodingKittenCard.class);
        put("ReverseCard", ReverseCard.class);
        put("DefuseCard", DefuseCard.class);
        put("AttackCard", AttackCard.class);
        put("FavorCard", FavorCard.class);
        put("SeeTheFutureCard", SeeTheFutureCard.class);
        put("ShuffleCard", ShuffleCard.class);
        put("SkipCard", SkipCard.class);
        put("CatCardMomma", CatCardMomma.class);
        put("CatCardShy", CatCardShy.class);
        put("CatCardZombie",  CatCardZombie.class);
    }};
    public List<Card> cardDeck = new ArrayList<>();

    public Iterator<Card> iterator() { return this.cardDeck.iterator(); }

    public Deck(int players, String deckName) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        deckConstruct(players, deckName);
    }

    public List<Card> getFullDeck(){
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

    public static String serializeDeck(String deckName, String location) throws IOException {
        String deckString = "";
        String fileContent = Files.readString(Paths.get("resources/decks/" + location + "/" + deckName + ".json"), StandardCharsets.US_ASCII);
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);
        for (LinkedTreeMap<Object, Object> cardTree : cardAmounts) {
            Integer amountAsInt = (int) Double.parseDouble(cardTree.get("deckAmount").toString());
            deckString += cardTree.get("className").toString() + ":" + amountAsInt + "==";
        }
        System.out.println(deckString);
        return deckString;
    }

    public static boolean createCustom(HashMap<String, Integer> amountMap, String deckName, String location) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ArrayList<Card> customDeck = new ArrayList<>();
        File deckFile = new File("resources/decks/" + location + "/" + deckName + ".json");
        deckFile.createNewFile();
        for(String cardName : amountMap.keySet()){
            if(amountMap.get(cardName) == 0) continue;
            Card tempCard = cardMap.get(cardName).getDeclaredConstructor().newInstance();
            tempCard.setDeckAmount(amountMap.get(cardName));
            customDeck.add(tempCard);
        }
        FileWriter myWriter = new FileWriter("resources/decks/" + location + "/" + deckName + ".json");
        myWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(customDeck, ArrayList.class));
        myWriter.close();
        return true;
    }

    public void deckConstruct(int players, String deckName) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String fileContent = Files.readString(Paths.get("resources/decks/server/" + deckName + ".json"), StandardCharsets.US_ASCII);
        ArrayList<LinkedTreeMap> cardAmounts = new Gson().fromJson(fileContent, ArrayList.class);

        for (LinkedTreeMap<Object, Object> cardTree : cardAmounts) {
            for (double j = 0; j < Double.parseDouble(cardTree.get("deckAmount").toString()); j++) {
                Card newCard = cardMap.get(cardTree.get("className").toString()).getDeclaredConstructor().newInstance();
                cardDeck.add(newCard);
            }
        }
        if(!cardDeck.contains(new ExplodingKittenCard())){
            for(int i = 0; i < players - 1; i++){
                cardDeck.add(new ExplodingKittenCard());
            }
        }
        reshuffle();
    }
}
