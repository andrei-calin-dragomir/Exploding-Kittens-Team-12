package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

public class ShuffleCard extends Card {
    public void action(Deck currDeck, Hand playerHand) {
        currDeck.reshuffle();
        System.out.println("The cards have been shuffled!");
    }
}
