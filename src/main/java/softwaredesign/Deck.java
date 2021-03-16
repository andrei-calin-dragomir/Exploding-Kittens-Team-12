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
        put("exploding_kitten", new ExplodingKittenCard());
        put("defuse", new DefuseCard());
        put("attack", new AttackCard());
        put("nope", new NopeCard());
        put("favor", new FavorCard());
        put("see_future", new SeeTheFutureCard());
        put("shuffle", new ShuffleCard());
        put("skip", new SkipCard());
        put("catermelon", new CatCardMomma());
        put("taco_cat", new CatCardShy());
        put("potato_cat", new CatCardZombie());
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
            if(cardDrawn.getName().equals("exploding_kitten")) insertCard(cardDrawn, getDeckSize() - 1);
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
