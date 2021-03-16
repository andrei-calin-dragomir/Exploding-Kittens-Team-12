package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        ArrayList<Card> deck = gameManager.mainDeck.cardDeck;
        String futureCards = deck.get(0).getName() + " " + deck.get(1).getName() + " " + deck.get(2).getName();
    }
}
