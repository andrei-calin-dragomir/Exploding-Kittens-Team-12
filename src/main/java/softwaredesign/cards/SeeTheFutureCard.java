package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(ServerHeldGame heldGame) {
        ArrayList<Card> deck = heldGame.gameManager.mainDeck.cardDeck;
        String futureCards = deck.get(0).getName() + " " + deck.get(1).getName() + " " + deck.get(2).getName();
    }
}
