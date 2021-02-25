package softwaredesign;

import softwaredesign.Card;

import java.util.ArrayList;

public class Hand {
    ArrayList<Card> currentHand = new ArrayList<>();

    public void addToHand(Card cardToAdd){
        currentHand.add(cardToAdd);
    }

    public ArrayList<Card> getHand(){
        return currentHand;
    }
}
