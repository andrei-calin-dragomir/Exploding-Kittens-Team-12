package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(Deck currDeck, Hand playerHand) {
        ArrayList<Card> deck = currDeck.getFullDeck();
        String futureCards = deck.get(0).getName() + " - " + deck.get(1).getName() + " - " + deck.get(2).getName();
        System.out.println("The top 3 cards are: " + futureCards);
    }
}
